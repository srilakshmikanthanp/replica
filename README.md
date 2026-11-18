# Replica

A Java library that replicates changes from one database to others, using a transactional outbox on the source and an
inbox on each destination. It can also backfill existing data to a station added later. Delivery is at least once, and
the destination applies events in order. See the [`replica-core` guide](replica-core/README.md) for how it works.

It comes with a runnable playground: a master and slaves on local H2 databases, and a web app to change data and watch
it arrive.

## Modules

- [`replica-core`](replica-core): The core replication library
- [`replica-spring-jpa`](replica-spring-jpa): Spring Data JPA implementation of the core interfaces
- [`replica-integration-test`](replica-integration-test): Integration tests on H2 databases
- [`replica-playground-api`](replica-playground-api): Playground with a master and slave on H2 databases
- [`replica-playground-web`](replica-playground-web): Playground web app to interact with the master and slaves

## Running the playground

Requires Java 21 or later and Node.js. Copy `.env.example` to `.env`, then run each task in its own terminal:

```
./gradlew runMaster    # http://localhost:8080
./gradlew runSlave1    # http://localhost:8081
./gradlew runWeb       # http://localhost:5173
```

Open http://localhost:5173 and add the slave on the Stations page with its URL, `http://localhost:8081`. The master
copies its existing data to the slave and sends every later change. Another slave:
`./gradlew runSlave -Pstation=slave3 -Pport=8083`.

## Running the tests

```
./gradlew test
```
