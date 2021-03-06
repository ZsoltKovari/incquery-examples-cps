package org.eclipse.incquery.examples.cps.xform.m2t.monitor.util;

import org.eclipse.incquery.examples.cps.deployment.DeploymentBehavior;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.BehaviorChangeMatch;
import org.eclipse.incquery.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the org.eclipse.incquery.examples.cps.xform.m2t.monitor.behaviorChange pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class BehaviorChangeProcessor implements IMatchProcessor<BehaviorChangeMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pBehavior the value of pattern parameter behavior in the currently processed match
   * 
   */
  public abstract void process(final DeploymentBehavior pBehavior);
  
  @Override
  public void process(final BehaviorChangeMatch match) {
    process(match.getBehavior());
  }
}
