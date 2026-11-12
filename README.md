# Replica

A Java library that replicates changes from one database to others, using a transactional outbox on the source and an
inbox on each destination. It can also backfill existing data to a station that is added later. It comes with a
runnable playground: a master and slaves on local H2 databases, and a web app to change data and watch it arrive.

## How it works

1. A change and the events describing it are written to the source database in the same transaction (the outbox), so
   neither can be lost without the other.
2. A publisher sends the outbox rows to each destination station. Delivery is at least once.
3. The destination stores each row once in its inbox, then applies the events in order with its own handlers.
4. When a new station is added, its existing data is copied through the same outbox (backfill), and writes are paused
   until the copy is done so nothing is missed or reordered.

## Modules

| Module | What it is |
| --- | --- |
| [`replica-core`](replica-core/README.md) | The library: outbox, inbox and backfill, with no framework or database code. The README is the guide to using it. |
| `replica-spring` | Spring Data JPA entities, repositories and a transaction executor for the core interfaces. |
| `replica-playground` | A Spring Boot app that runs as a master or a slave, with categories, tags and products as sample data. |
| `replica-playground-ui` | A React web app for the playground: change data on the master, watch slaves, outbox and inbox, simulate parallel writes. |

Requires Java 21 or later and Node.js.

## Run the playground

Run these Gradle tasks, each in its own run window (in IntelliJ: the Gradle tool window, then `playground`) or its own
terminal:

```
./gradlew runMaster    # http://localhost:8080, database ./data/master
./gradlew runSlave1    # http://localhost:8081, database ./data/slave1
./gradlew runSlave2    # http://localhost:8082, database ./data/slave2
./gradlew runWeb       # http://localhost:5173
```

The tasks start the server with these properties, which both default to `master`:

- `playground.role`: `master` or `slave`.
- `playground.station-name`: the server's own name. It also names its database file, `./data/<station-name>`.

For another slave, use `./gradlew runSlave -Pstation=slave3 -Pport=8083`.

## Use it

Open http://localhost:5173.

1. **Point the web app at the master** (Servers page). It starts with `http://localhost:8080`. Change it if the master
   runs elsewhere.
2. **Add each slave** (Stations page). Enter the slave's URL, for example `http://localhost:8081`. The web app asks the
   slave for its name, and the master then copies its existing data to that slave and sends it every later change.
   Changes are refused for a short while during that first copy.

The slaves are the stations the master replicates to, so a slave you add shows up everywhere on its own: the
Playground page, the Servers page and the server pickers.

After that, change categories, tags and products on the master. The Playground page shows each server's counts, and
the Outbox and Inbox pages show the messages on their way.
