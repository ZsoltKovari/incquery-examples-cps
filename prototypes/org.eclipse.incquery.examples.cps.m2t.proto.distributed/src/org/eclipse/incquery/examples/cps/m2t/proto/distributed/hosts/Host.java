package org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts;

public interface Host {

	boolean hasMessageFor(String appID, String trigger);
	void sendTrigger(String trgHostIP, String trgAppID, String trgTransactionID);
	
	void receiveTrigger(String trgAppID, String trgTransactionID);
}