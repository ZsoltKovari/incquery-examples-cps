package org.eclipse.incquery.examples.cps.xform.m2t.api

import org.eclipse.incquery.examples.cps.deployment.DeploymentHost
import org.eclipse.incquery.examples.cps.deployment.DeploymentApplication
import org.eclipse.incquery.examples.cps.deployment.Deployment
import org.eclipse.incquery.examples.cps.deployment.DeploymentBehavior
import org.eclipse.incquery.examples.cps.xform.m2t.exceptions.CPSGeneratorException

interface ICPSGenerator {
	def CharSequence generateHostCode(DeploymentHost host) throws CPSGeneratorException
	def CharSequence generateApplicationCode(DeploymentApplication application) throws CPSGeneratorException
	def CharSequence generateBehaviorCode(DeploymentBehavior behavior) throws CPSGeneratorException
	def CharSequence generateDeploymentCode(Deployment deployment) throws CPSGeneratorException
}