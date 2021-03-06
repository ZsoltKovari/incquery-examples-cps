package org.eclipse.incquery.examples.cps.generator.utils

import org.apache.log4j.Logger
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.State
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.StateMachine
import org.eclipse.incquery.examples.cps.deployment.DeploymentFactory
import org.eclipse.incquery.examples.cps.traceability.CPSToDeployment
import org.eclipse.incquery.examples.cps.traceability.TraceabilityFactory
import org.eclipse.incquery.examples.cps.cyberPhysicalSystem.CyberPhysicalSystem
import com.google.common.collect.HashMultimap
import org.eclipse.incquery.examples.cps.generator.dtos.HostClass
import org.eclipse.incquery.examples.cps.generator.dtos.CPSFragment

class CPSModelBuilderUtil {
	
	protected extension Logger logger = Logger.getLogger("cps.generator.CPSModelBuilderUtil")
	protected extension CyberPhysicalSystemFactory cpsFactory = CyberPhysicalSystemFactory.eINSTANCE
	protected extension DeploymentFactory depFactory = DeploymentFactory.eINSTANCE
	protected extension TraceabilityFactory traceFactory = TraceabilityFactory.eINSTANCE
	
	def prepareEmptyModel(String cpsId) {
		val rs = new ResourceSetImpl()
		val cpsRes = rs.createResource(URI.createURI("dummyCPSUri"))
		val depRes = rs.createResource(URI.createURI("dummyDeploymentUri"))
		val trcRes = rs.createResource(URI.createURI("dummyTraceabilityUri"))
		
		val cps = createCyberPhysicalSystem => [
			id = cpsId
		]
		cpsRes.contents += cps
		
		val dep = createDeployment
		depRes.contents += dep
		 
		val cps2dep = createCPSToDeployment => [
			it.cps = cps
			it.deployment = dep
		]
		trcRes.contents += cps2dep
		cps2dep
	}
	
	def prepareCPSModel(String cpsUri) {
		val rs = new ResourceSetImpl()
		val cpsRes = rs.getResource(URI.createURI(cpsUri), true)
		val depRes = rs.createResource(URI.createURI("dummyDeploymentUri"))
		val trcRes = rs.createResource(URI.createURI("dummyTraceabilityUri"))
		
		val cps = cpsRes.contents.head as CyberPhysicalSystem
		
		val dep = createDeployment
		depRes.contents += dep
		 
		val cps2dep = createCPSToDeployment => [
			it.cps = cps
			it.deployment = dep
		]
		trcRes.contents += cps2dep
		cps2dep
	}
	
	def preparePersistedCPSModel(String dirUri, String modelName) {
		val rs = new ResourceSetImpl()
		val cpsRes = rs.createResource(URI.createURI(dirUri + "/" + modelName + ".cyberphysicalsystem"))
		val depRes = rs.createResource(URI.createURI(dirUri + "/" + modelName + ".deployment"))
		val trcRes = rs.createResource(URI.createURI(dirUri + "/" + modelName + ".traceability"))
		
		val cps = createCyberPhysicalSystem => [
			id = modelName
		]
		cpsRes.contents += cps
		
		val dep = createDeployment
		depRes.contents += dep
		 
		val cps2dep = createCPSToDeployment => [
			it.cps = cps
			it.deployment = dep
		]
		trcRes.contents += cps2dep
		cps2dep
	}
	
	def prepareHostTypeWithId(CPSToDeployment cps2dep, String hostId) {
		prepareHostTypeWithId(cps2dep.cps, hostId);
	}
	
	def prepareHostTypeWithId(CyberPhysicalSystem cps, String hostId) {
		debug('''Adding host type (ID: «hostId») to model''')
		val host = createHostType => [
			id = hostId
		]
		cps.hostTypes += host
		host
	}
	
	def prepareHostInstanceWithIP(HostType host, String instanceId, String ip) {
		debug('''Adding host instance (IP: «ip») to host type «host.id»''')
		val instance = createHostInstance => [
			id = instanceId
			nodeIp = ip
		]
		host.instances += instance
		instance
	}
	
	def prepareHostInstance(CPSToDeployment cps2dep) {
		val host = cps2dep.prepareHostTypeWithId("simple.cps.host")
		val ip = "1.1.1.1"
		val hostInstance = host.prepareHostInstanceWithIP("simple.cps.host.instance", ip)
		hostInstance
	}
	
	def prepareApplicationTypeWithId(CPSToDeployment cps2dep, String appId) {
		prepareApplicationTypeWithId(cps2dep.cps, appId);
	}
	
	def prepareApplicationTypeWithId(CyberPhysicalSystem cps, String appId) {
		debug('''Adding application type (ID: «appId») to model''')
		val appType = createApplicationType => [
			id = appId
		]
		cps.appTypes += appType
		appType
	}
	
	def prepareApplicationInstanceWithId(ApplicationType app, String appId, HostInstance host) {
		debug('''Adding application instance (ID: «appId») to «app.id»''')
		val instance = createApplicationInstance => [
			id = appId
			allocatedTo = host
		]
		app.instances += instance
		instance
	}
	
	def prepareApplicationInstanceWithId(ApplicationType app, String appId) {
		debug('''Adding application instance (ID: «appId») to «app.id»''')
		val instance = createApplicationInstance => [
			id = appId
		]
		app.instances += instance
		instance
	}

	def prepareAppInstance(CPSToDeployment cps2dep, HostInstance hostInstance) {
		val app = cps2dep.prepareApplicationTypeWithId("simple.cps.app")
		val instance = app.prepareApplicationInstanceWithId("simple.cps.app.instance", hostInstance)
		instance
	}
	
	def prepareStateMachine(ApplicationType app, String smId) {
		debug('''Adding state machine (ID: «smId») to «app.id»''')
		val instance = createStateMachine => [
			id = smId
		]
		app.behavior = instance
		instance
	}
	
	def prepareState(StateMachine sm, String stateId) {
		debug('''Adding state (ID: «stateId») to «sm.id»''')
		val state = createState => [
			id = stateId
		]
		sm.states += state
		state
	}
	
	def prepareTransition(State source, String trID, State target) {
		debug('''Adding transition (ID: «trID») between «source.id» and «target.id»''')
		val transition = source.prepareTransition(trID)
		transition.targetState = target
		transition
	}
	
	def prepareTransition(State source, String trID) {
		val transition = createTransition => [
			id = trID
		]
		source.outgoingTransitions += transition
		transition
	}
	
	def prepareCommunication(HostInstance srcHost, HostInstance trgHost) {
		debug('''Create connection from «srcHost.id» to «trgHost.id»''')
		srcHost.communicateWith.add(trgHost);
	}
	
	def static calculateHostInstancesToHostClassMap(CPSFragment fragment){
		val hostClassToInstanceMap = HashMultimap.<HostClass, HostInstance>create;
		fragment.hostTypes.keySet.forEach[hc |
			val instances = fragment.hostTypes.get(hc).map[ht | 
				ht.instances
			].flatten
			
			hostClassToInstanceMap.putAll(hc, instances)
		]
		return hostClassToInstanceMap;
	}
}
