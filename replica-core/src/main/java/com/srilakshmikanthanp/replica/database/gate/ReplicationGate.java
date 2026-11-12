package com.srilakshmikanthanp.replica.database.gate;

/**
 * Orders writes against the creation of a backfill job. A write holds the gate shared while it decides which
 * stations to send to, and job creation holds it exclusively, so job creation waits for the writes in progress and
 * any write that starts later already sees the new station.
 * <p>
 * Both methods must be called inside a transaction. The lock is held until that transaction commits or rolls back,
 * not until the method returns, so an implementation has to release it when the transaction ends (a database lock
 * does this by itself).
 */
public interface ReplicationGate {
	/**
	 * Many writes can hold the gate shared at once.
	 */
	void enterShared();

	/**
	 * Waits for every shared holder to commit and holds back new ones.
	 */
	void enterExclusive();
}
