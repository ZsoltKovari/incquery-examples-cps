package org.eclipse.incquery.examples.cps.performance.tests.queries

import com.google.common.base.Stopwatch
import java.util.Random
import java.util.concurrent.TimeUnit
import org.apache.log4j.Logger
import org.eclipse.incquery.examples.cps.generator.CPSPlanBuilder
import org.eclipse.incquery.examples.cps.generator.dtos.CPSFragment
import org.eclipse.incquery.examples.cps.generator.dtos.CPSGeneratorInput
import org.eclipse.incquery.examples.cps.generator.utils.CPSModelBuilderUtil
import org.eclipse.incquery.examples.cps.performance.tests.config.cases.SimpleScalingCase
import org.eclipse.incquery.examples.cps.planexecutor.PlanExecutor
import org.eclipse.incquery.examples.cps.tests.CPSTestBase
import org.eclipse.incquery.examples.cps.xform.m2m.incr.expl.queries.CpsXformM2M
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.emf.EMFScope
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class QueryTest extends CPSTestBase{
	
	protected extension Logger logger = Logger.getLogger("cps.performance.tests.queries.QueryTest")
    protected extension CPSModelBuilderUtil modelBuilder
	
	IQuerySpecification querySpec
	
	@Parameters(name = "{index}: {1}")
    public static def transformations() {
        #[
        	#[CpsXformM2M.instance.trigger, "Trigger"].toArray
        ]
    }
	
	new(IQuerySpecification querySpec, String wrapperType){
    	this.querySpec = querySpec
		modelBuilder = new CPSModelBuilderUtil
    }
    
    @Ignore
	@Test
	def basicScenario1k(){
		val testId = "basicScenario1k"
		startTest(testId)
		
		executeScenarioXform(1000)
		
		endTest(testId)
	}
	
    @Ignore
	@Test
	def basicScenario10k(){
		val testId = "basicScenario10k"
		startTest(testId)
		
		executeScenarioXform(10000)
		
		endTest(testId)
	}
	
    @Ignore
	@Test
	def basicScenario100k(){
		val testId = "basicScenario100k"
		startTest(testId)
		
		executeScenarioXform(100000)
		
		endTest(testId)
	}
    
    def executeScenarioXform(int size) {
		val seed = 11111
		val Random rand = new Random(seed)
		val SimpleScalingCase bs = new SimpleScalingCase(size, rand)
		bs.executeScenarioXformForConstraints(seed)
	}
	
	def executeScenarioXformForConstraints(SimpleScalingCase benchmarkCase, long seed) {	
		val constraints = benchmarkCase.getConstraints()
		val cps2dep = prepareEmptyModel("testModel"+System.nanoTime)
		
		val CPSGeneratorInput input = new CPSGeneratorInput(seed, constraints, cps2dep.cps)
		var plan = CPSPlanBuilder.buildDefaultPlan
		
		var PlanExecutor<CPSFragment, CPSGeneratorInput> generator = new PlanExecutor()
		
		var generateTime = Stopwatch.createStarted
		var fragment = generator.process(plan, input)
		generateTime.stop
		info("Generating time: " + generateTime.elapsed(TimeUnit.MILLISECONDS) + " ms")
			
		var matcherTime = Stopwatch.createStarted
		val engine = AdvancedIncQueryEngine.createUnmanagedEngine(new EMFScope(cps2dep.eResource.resourceSet))
		val matcher = engine.getMatcher(querySpec)
		matcherTime.stop
		info("Match set size: " + matcher.countMatches)
		info("Matcher time: " + matcherTime.elapsed(TimeUnit.MILLISECONDS) + " ms")
		
	}
    
    
    def startTest(String testId){
    	info('''START TEST: type: «querySpec.fullyQualifiedName» ID: «testId»''')
    }
    
    def endTest(String testId){
    	info('''END TEST: type: «querySpec.fullyQualifiedName» ID: «testId»''')
    }
}