package org.eclipse.incquery.examples.cps.xform.m2m.incr.qrt.rules

import org.eclipse.incquery.examples.cps.xform.m2m.incr.qrt.queries.StateMachineMatch
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.evm.specific.Jobs
import org.eclipse.incquery.runtime.evm.specific.Lifecycles
import org.eclipse.incquery.runtime.evm.specific.Rules
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.examples.cps.deployment.DeploymentApplication
import org.eclipse.incquery.examples.cps.deployment.DeploymentBehavior
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.ApplicationInstance

class StateMachineRules {
	static def getRules(IncQueryEngine engine) {
		#{
			new StateMachineMapping(engine).specification
		}
	}
}

class StateMachineMapping extends AbstractRule<StateMachineMatch> {
	new(IncQueryEngine engine) {
		super(engine)
	}

	override getSpecification() {
		createPriorityRuleSpecification => [
			ruleSpecification = Rules.newMatcherRuleSpecification(stateMachine, Lifecycles.getDefault(true, true),
				#{appearedJob, updateJob, disappearedJob})
			priority = 3
		]
	}

	private def getAppearedJob() {
		Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED,
			[ StateMachineMatch match |
				val depApp = engine.cps2depTrace.getAllValuesOfdepElement(null, null, match.appInstance).filter(
					DeploymentApplication).head
				val smId = match.stateMachine.id
				debug('''Mapping state machine with ID: «smId»''')
				val behavior = createDeploymentBehavior => [
					description = smId
				]
				depApp.behavior = behavior
				val traces = engine.cps2depTrace.getAllValuesOftrace(null, match.stateMachine, null)
				if (traces.empty) {
					trace('''Creating new trace for state machine''')
					rootMapping.traces += createCPS2DeplyomentTrace => [
						cpsElements += match.stateMachine
						deploymentElements += behavior
					]
				} else {
					trace('''Adding new behavior to existing trace''')
					traces.head.deploymentElements += behavior
				}
				debug('''Mapped state machine with ID: «smId»''')
			])
	}

	private def getUpdateJob() {
		Jobs.newStatelessJob(IncQueryActivationStateEnum.UPDATED,
			[ StateMachineMatch match |
				val smId = match.stateMachine.id
				debug('''Updating mapped state machine with ID: «smId»''')
				val depSMs = engine.cps2depTrace.getAllValuesOfdepElement(null, null, match.stateMachine).filter(
					DeploymentBehavior)
				depSMs.forEach [
					if (description != smId) {
						trace('''ID changed to «smId»''')
						description = smId
					}
				]
				debug('''Updated mapped state machine with ID: «smId»''')
			])
	}

	private def getDisappearedJob() {
		Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED,
			[ StateMachineMatch match |
				val depApp = engine.cps2depTrace.getAllValuesOfdepElement(null, null, match.appInstance).head as DeploymentApplication;
				val depBehavior = depApp.behavior
				val smId = depBehavior.description
				logger.debug('''Removing state machine with ID: «smId»''')
				depApp.behavior = null;
				val smTrace = engine.cps2depTrace.getAllValuesOftrace(null, match.stateMachine, null).head
				smTrace.deploymentElements -= depBehavior
				if (smTrace.deploymentElements.empty) {
					trace('''Removing empty trace''')
					rootMapping.traces -= smTrace
				}
				logger.debug('''Removed state machine with ID: «smId»''')
			])
	}

}
