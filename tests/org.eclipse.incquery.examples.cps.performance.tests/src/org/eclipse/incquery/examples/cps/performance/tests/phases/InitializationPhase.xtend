package org.eclipse.incquery.examples.cps.performance.tests.phases

import eu.mondo.sam.core.phases.AtomicPhase;
import eu.mondo.sam.core.DataToken;
import eu.mondo.sam.core.results.PhaseResult;
import org.eclipse.incquery.examples.cps.performance.tests.CPSDataToken
import eu.mondo.sam.core.metrics.TimeMetric

class InitializationPhase extends AtomicPhase{
	
	new(String name) {
		super(name)
	}
	
	override execute(DataToken token, PhaseResult phaseResult) {
		val cpsToken = token as CPSDataToken
		val transformInitTimer = new TimeMetric("Time")
		
		transformInitTimer.startMeasure
		cpsToken.xform.initializeTransformation(cpsToken.cps2dep)
		transformInitTimer.stopMeasure
		
		phaseResult.addMetrics(transformInitTimer)
	}
	
}