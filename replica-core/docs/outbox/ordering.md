# Why replaying outbox events in outbox ID order works

This records the reasoning behind replaying transactional outbox events in ascending outbox ID order. No counterexample
is known under the assumptions below.

## Assumptions

### A1. Atomic Source Transaction

Business changes and the corresponding outbox event are written within the same database transaction. If the transaction
commits, both become visible together. If it rolls back, neither is persisted.

### A2. Outbox ID Allocation

The outbox identifier is allocated when the outbox row is inserted, and allocation order matches the order in which
outbox INSERT statements execute. No relationship between outbox ID order and transaction commit order is assumed.

### A3. Outbox Written Last

The outbox event is inserted after all business modifications have been performed and immediately before the transaction
commits. "Performed" means the business SQL statements have been executed, not only queued in a persistence context. An
ORM such as JPA may hold an `UPDATE` back until the transaction flushes, which is after the outbox insert, so flush
before writing the outbox row. Without that, two writers to the same row can get outbox IDs in the opposite order to
their updates.

### A4. Conflicting Writes Are Serialized

The database serializes conflicting writes. Two concurrent transactions cannot modify the same row simultaneously; one
waits until the other commits or rolls back before modifying that row.

**Note:** This assumption only protects against write-write conflicts. For write-read and read-write conflicts, see
assumption A5.

### A5. Self-Contained Events

Each outbox event contains all information required for the destination to reproduce the business effects of the source
transaction. Replay does not determine what to write by reading mutable replicated business state.

**Note:** This assumption protects against write-read and read-write conflicts. For example, here is classic write skew.

Two concurrent transactions:

```
T1:
    read A
    write B = A

T2:
    read B
    write A = B
```

Under READ COMMITTED, both transactions may commit because they modify different rows.

If replay required rereading the destination state (for example, "copy the current value of A into B"), reproducing the
source result would depend on the replay order.

Instead, each outbox event contains the values that were computed by the source transaction. Replay simply writes those
values and does not repeat the original reads.

<details>
<summary>Stale reads and lost updates</summary>

**Which values count**

"Computed by the source transaction" means values the transaction wrote, or values it read after locking the row. A
value read with a plain (non-locking) SELECT before the row was locked may be stale. A4 serializes the writes, but it
does not make an earlier read fresh. Putting a stale value into an event loses another transaction's update on the
destination.

**Example**

A row starts as `a=0, b=0, c=0`, and each event carries the full entity. Two concurrent transactions each update one
column:

```
T1: load entity (plain SELECT)   -> a=0, b=0, c=0
T2: load entity (plain SELECT)   -> a=0, b=0, c=0
T1: UPDATE ... SET a = 1         -> locks the row
T2: UPDATE ... SET c = 1         -> waits for T1
T1: event (a=1, b=0, c=0), outbox ID 5, commit
T2: updates c; source row is now a=1, b=0, c=1
T2: event (a=0, b=0, c=1), outbox ID 6, commit    <- a is stale
```

The outbox IDs follow the write order, but replaying 5 then 6 leaves the destination at `a=0, b=0, c=1`, while the
source has `a=1, b=0, c=1`. T2's event carries a value it never wrote.

This is the classic **lost update** problem: a read-modify-write based on a value read before the row was locked. Here
the stale value goes into the event instead of the UPDATE, so the source stays correct and the update is lost only on
the destination.

**Fixes**

An event therefore must not be built from an entity loaded before the row was locked. The usual lost update fixes keep
it correct:

- **Partial writes:** the event carries only the columns the transaction changed, and replay updates only those columns.
- **Pessimistic locking:** the transaction locks the row before reading it (`SELECT ... FOR UPDATE`), then builds the
  event from that read.
- **Optimistic locking:** the UPDATE checks a `VERSION` column (`WHERE ID = ? AND VERSION = ?`). If it changes 0 rows,
  the transaction reloads the row and retries, building the event from the reloaded values.

</details>

### A6. Atomic Replay

The destination applies all changes represented by a single outbox event atomically. An event is either fully applied or
not applied at all, and events are never interleaved.

### A7. Preservation of Write Dependencies

Replay must not introduce additional write dependencies that did not exist between the corresponding source
transactions.

For example, suppose two source transactions update different orders:

```sql
T1: UPDATE orders SET last_modified = '10:00' WHERE id = 1;
T2: UPDATE orders SET last_modified = '10:05' WHERE id = 2;
```

These transactions are independent because they modify different rows.

If replay instead updates a shared destination row:

```sql
E1: UPDATE summary SET last_changed = '10:00';
E2: UPDATE summary SET last_changed = '10:05';
```

Then E1 and E2 both modify the same destination row.

Replay therefore introduces a write dependency that did not exist between the corresponding source transactions.

## Observation

Under assumptions A1–A7, no counterexample is known where replaying events in ascending outbox ID order produces
a different final state from the source database.

## Reasoning

The interaction between two concurrent transactions can be classified into three cases:

### 1. Read-Read (R-R)

Both transactions read the same row but neither modifies it. Since no state is changed, replay order has no effect on
the final state.

### 2. Write-Write (W-W)

Both transactions modify the same row. The database serializes these writes, so one transaction performs its write
before the other. Because the outbox row is inserted as the final step of the transaction, the outbox ID reflects this
write order. Replaying events in ascending outbox ID order therefore reproduces the same final value. See A4 for
details.

### 3. Read-Write and Write-Read (R-W, W-R)

One transaction reads a row that another transaction modifies, whether the read comes before or after the write.

Replay does not repeat the original reads. Instead, each outbox event contains the values computed by the source
transaction. Replay simply writes those values and therefore does not depend on the current mutable state of the
destination database. See A5 for details.

## Intuition

For concurrent transactions, whenever the final state depends on the order of writes to the same row, the database
already determines that order. The outbox ID follows that order, so replaying by outbox ID reproduces the same final
state.
