package org.eclipse.incquery.examples.cps.xform.m2t.listener;

import org.eclipse.incquery.examples.cps.deployment.Deployment;
import org.eclipse.incquery.examples.cps.deployment.DeploymentElement;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public interface IModelChangeListener {

	/**
	 * Sets the model whose changes are observed. Also creates an initial checkpoint with no changes registered.
	 * @param deployment the deployment model
	 * @param engine engine associated with the 
	 * @throws IncQueryException 
	 */
	void startListening(Deployment deployment, IncQueryEngine engine) throws IncQueryException;
	
	/**
	 * Creates a checkpoint which means:
	 * <li>Model changes since the last checkpont are saved</li>
	 * <li>The model changes in the future are tracked separately from the changes before the checkpoint</li>
	 */
	void createCheckpoint();
	
	/**
	 * Returns all changed elements between the last two checkpoints
	 * @return the changed elements
	 */
	DeploymentElement getChangedElements();

	/**
	 * TODO
	 * Implementations of this method should call createCheckpoint and subsequently getChangedElements  
	 * @return
	 */
	DeploymentElement createCheckpointAndGetChangedElements();

	
}