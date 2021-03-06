package org.eclipse.incquery.examples.cps.xform.m2t.distributed;

import org.eclipse.incquery.examples.cps.deployment.Deployment;
import org.eclipse.incquery.examples.cps.deployment.DeploymentApplication;
import org.eclipse.incquery.examples.cps.deployment.DeploymentBehavior;
import org.eclipse.incquery.examples.cps.deployment.DeploymentHost;
import org.eclipse.incquery.examples.cps.xform.m2t.api.ICPSGenerator;
import org.eclipse.incquery.examples.cps.xform.m2t.exceptions.CPSGeneratorException;
import org.eclipse.incquery.examples.cps.xform.m2t.util.FormatterUtil;
import org.eclipse.incquery.runtime.api.IncQueryEngine;

public class CodeGenerator implements ICPSGenerator {
    FormatterUtil util = new FormatterUtil();
	private boolean forceCodeFormatting;
	private Generator generator;

	public CodeGenerator(String projectName, IncQueryEngine engine, boolean forceCodeFormatting) {
		this.forceCodeFormatting = forceCodeFormatting;
		generator = new Generator(projectName, engine);
	}
	
	@Override
	public CharSequence generateHostCode(DeploymentHost host) throws CPSGeneratorException {
		if(forceCodeFormatting){
			return util.formatCode(generator.generateHostCode(host));
		}
		return generator.generateHostCode(host);
	}

	@Override
	public CharSequence generateApplicationCode(DeploymentApplication application) throws CPSGeneratorException {
		if(forceCodeFormatting){
			return util.formatCode(generator.generateApplicationCode(application));
		}
		return generator.generateApplicationCode(application);
	}

	@Override
	public CharSequence generateBehaviorCode(DeploymentBehavior behavior) throws CPSGeneratorException {
		if(forceCodeFormatting){
			return util.formatCode(generator.generateBehaviorCode(behavior));
		}
		return generator.generateBehaviorCode(behavior);
	}

	@Override
	public CharSequence generateDeploymentCode(Deployment deployment) throws CPSGeneratorException {
		if(forceCodeFormatting){
			return util.formatCode(generator.generateDeploymentCode(deployment));
		}
		return generator.generateDeploymentCode(deployment); 
	}
	 
}
