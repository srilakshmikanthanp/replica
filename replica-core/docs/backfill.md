# How backfill runs without blocking writes

A backfill copies existing data to a destination while the source continues accepting writes. This explains how
backfill and live writes work together without losing changes.

## How it works

Each backfill step maintains a cursor representing the last row copied.

The backfill runner processes rows in batches, each in its own transaction:

1. Lock the backfill step exclusively (`SELECT ... FOR UPDATE`).
2. Read the next batch of rows after the cursor.
3. Write an event for each row to the outbox.
4. Advance the cursor to the last row copied, and commit, which releases the lock.

Meanwhile, live writes continue normally. Before writing an event to the outbox, the write locks the destination's
unfinished backfill steps for sharing (`SELECT ... FOR SHARE`) and holds that lock until it commits. Then it checks the
cursor:

- If the event affects a row beyond the cursor, it is skipped because backfill will copy that row.
- If the row is already behind the cursor, the event is written to the outbox normally.

Live writes only share the lock, so they do not block each other. They wait only while a batch is being written.

## Why it works

A backfill batch and a live write lock the same backfill step, so one always waits for the other to commit. They
cannot make conflicting decisions concurrently.

There are two possible outcomes:

- **Backfill goes first:** It copies the row and advances the cursor. The live write waits for the batch to commit,
  then sees that the row has been copied and sends its event. The destination applies the copy first, followed by the
  live change.
- **Live write goes first:** It sees that the row has not been copied and skips its event. The batch waits for the
  write to commit, then reads the updated row and copies its latest state.

In either case, the destination receives the correct state without blocking normal writes for the duration of the
backfill.

Backfill reads rows without locking them. If it locked them, a batch and a live write could wait for each other forever:

1. A live write updates a row, which locks that row, and then waits for the step lock.
2. A batch holds the step lock and tries to lock the same row, so it waits for the write.

With a plain read, the batch never waits for a row lock. It reads the row as last committed, finishes, and releases the
step lock. The write then sees the row has been copied and sends its event, so its change is not lost.

## Requirements

- **Register all affected events:** Use `affectedBy` to register every event type that can modify data copied by a
  backfill step, including the backfilled event itself.
- **Keep row keys stable:** Keys must not change while backfill is running.
- **Keep key ordering consistent:** The Java cursor comparison must match the database's `ORDER BY` ordering. Prefer
  numeric keys or binary collation for strings.
- **Read without locking rows:** Backfill sources must use ordinary reads, not `FOR UPDATE` or `FOR SHARE`.
- **Backfill dependent types together:** If one event type points at another, backfill both in the same job. The
  planner copies the referenced data first, and live events for the dependent type are held back until it is copied.
