package org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts.statemachines;

import java.util.List;

import org.eclipse.incquery.examples.cps.m2t.proto.distributed.hosts.applications.Application;

import com.google.common.collect.Lists;

public enum BehaviorISSBState implements State<BehaviorISSBState>{
     ///////////
	// States
	ISSWait {
        @Override
        public List<BehaviorISSBState> possibleNextStates(Application app) {
        	List<BehaviorISSBState> possibleStates = Lists.newArrayList();
        	
        	// Add Neutral Transitions
        	
        	// Add Send Transitions
        	        	
        	// Add Wait Transitions
        	if(app.hasMessageFor(ISS_RECEIVING)){
        		possibleStates.add(ISSReceived);
        	}
        	
        	return possibleStates;
        }
    },
    ISSReceived {
        @Override
        public List<BehaviorISSBState> possibleNextStates(Application app) {
        	List<BehaviorISSBState> possibleStates = Lists.newArrayList();
        	
        	// Add Neutral Transitions
        	possibleStates.add(ISSWait);
        	
        	// Add Send Transitions
        	        	
        	// Add Wait Transitions
        	
        	return possibleStates;
        }
    };
	
     ////////////
    // Triggers
    // TODO should be Enum...?
	public static final String ISS_RECEIVING = "ISSReceiving";
	
	 /////////////////
	// General part
	abstract public List<BehaviorISSBState> possibleNextStates(Application app);
	
	public BehaviorISSBState stepTo(BehaviorISSBState nextState, Application app){
		if(possibleNextStates(app).contains(nextState)){
			return nextState;
		}
		return this;
	}

}