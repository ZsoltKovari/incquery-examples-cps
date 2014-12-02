package org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts.applications;

import org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts.Host;
import org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts.statemachines.State;

public class BaseApplication<StateType extends State> implements Application {

	protected StateType currentState;
	protected Host host;

	// Add current ApplicationID
	protected static final String APP_ID = "";
	
	public BaseApplication(Host host) {
		this.host = host;
	}
	
	@Override
	public StateType getCurrentState() {
		return currentState;
	}

	@Override
	public boolean hasMessageFor(String trigger) {
		return host.hasMessageFor(APP_ID, trigger);
	}

	@Override
	public void sendTrigger(String trgHostIP, String trgAppID, String trgTransactionID) {
		host.sendTrigger(trgHostIP, trgAppID, trgTransactionID);
	}

}