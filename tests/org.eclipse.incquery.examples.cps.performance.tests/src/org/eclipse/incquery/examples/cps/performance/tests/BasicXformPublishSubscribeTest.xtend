package org.eclipse.incquery.examples.cps.performance.tests

import java.util.Random
import org.eclipse.incquery.examples.cps.performance.tests.config.GeneratorType
import org.eclipse.incquery.examples.cps.performance.tests.config.TransformationType
import org.eclipse.incquery.examples.cps.performance.tests.config.cases.PublishSubscribeCase

class BasicXformPublishSubscribeTest extends BasicXformTest {
	
	new(TransformationType wrapperType, int scale, GeneratorType generatorType) {
		super(wrapperType, scale, generatorType)
	}
	
	override getCase(int scale, Random rand) {
		return new PublishSubscribeCase(scale, rand)
	}
	
}