package org.eclipse.incquery.examples.cps.xform.m2t.monitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.examples.cps.deployment.BehaviorState;
import org.eclipse.incquery.examples.cps.deployment.BehaviorTransition;
import org.eclipse.incquery.examples.cps.deployment.Deployment;
import org.eclipse.incquery.examples.cps.deployment.DeploymentApplication;
import org.eclipse.incquery.examples.cps.deployment.DeploymentElement;
import org.eclipse.incquery.examples.cps.deployment.DeploymentHost;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.ChangeMonitorJob;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.AbstractDeploymentChangeMonitor;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.ApplicationBehaviorCurrentStateChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.ApplicationIdChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.BehaviorChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.DeploymentHostIpChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.DeploymentHostsChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.HostApplicationsChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.HostIpChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.TransitionChangeQuerySpecification;
import org.eclipse.incquery.examples.cps.xform.m2t.monitor.util.TriggerChangeQuerySpecification;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Lifecycles;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.job.EnableJob;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("unchecked")
public class DeploymentChangeMonitor extends AbstractDeploymentChangeMonitor {

	private Set<DeploymentElement> appearBetweenCheckpoints;
	private Set<DeploymentElement> updateBetweenCheckpoints;
	private Set<DeploymentElement> disappearBetweenCheckpoints;
	private Set<DeploymentElement> appearAccumulator;
	private Set<DeploymentElement> updateAccumulator;
	private Set<DeploymentElement> disappearAccumulator;
	private boolean deploymentBetweenCheckpointsChanged;
	private boolean deploymentChanged;
	private ExecutionSchema executionSchema;
	private HashMap<DeploymentApplication, String> appsToIDs;
	private HashMap<DeploymentHost, String> hostsToIPs;
	
	public DeploymentChangeMonitor(Deployment deployment, IncQueryEngine engine) {
		super(deployment, engine);
		this.appearBetweenCheckpoints = Sets.newHashSet();
		this.updateBetweenCheckpoints = Sets.newHashSet();
		this.disappearBetweenCheckpoints = Sets.newHashSet();
		this.appearAccumulator = Sets.newHashSet();
		this.updateAccumulator = Sets.newHashSet();
		this.disappearAccumulator = Sets.newHashSet();
		deploymentBetweenCheckpointsChanged = false;
		deploymentChanged = false;


		UpdateCompleteBasedSchedulerFactory schedulerFactory = Schedulers
				.getIQEngineSchedulerFactory(engine);
		executionSchema = ExecutionSchemas
				.createIncQueryExecutionSchema(engine, schedulerFactory);

		hostsToIPs = Maps.newHashMap();
		appsToIDs = Maps.newHashMap();
		for (DeploymentHost deploymentHost : deployment.getHosts()) {
			hostsToIPs.put(deploymentHost, deploymentHost.getIp());
			for (DeploymentApplication deploymentApplication : deploymentHost.getApplications()) {
				appsToIDs.put(deploymentApplication, deploymentApplication.getId());
			}
		}
		executionSchema.getContext().put(ChangeMonitorJob.HOSTS,hostsToIPs);
		executionSchema.getContext().put(ChangeMonitorJob.APPLICATIONS,Maps.newHashMap());
	}
	
	@Override 
	public DeploymentChangeDelta createCheckpoint() {
		appearBetweenCheckpoints = appearAccumulator;
		updateBetweenCheckpoints = updateAccumulator;
		disappearBetweenCheckpoints = disappearAccumulator;
		appearAccumulator = Sets.newHashSet();
		updateAccumulator = Sets.newHashSet();
		disappearAccumulator = Sets.newHashSet();
		deploymentBetweenCheckpointsChanged = deploymentChanged;
		Map<DeploymentElement, String> elementsUpdatedOrDeleted = (Map<DeploymentElement, String>) executionSchema.getContext().get(ChangeMonitorJob.OUTDATED_ELEMENTS);

		for (DeploymentElement deploymentElement : elementsUpdatedOrDeleted.keySet()) {
			// Refresh the list of host IP addresses and app identifiers
			if(deploymentElement instanceof DeploymentHost){
				hostsToIPs.put((DeploymentHost) deploymentElement, ((DeploymentHost) deploymentElement).getIp());
			}
			else if(deploymentElement instanceof DeploymentApplication){
				appsToIDs.put((DeploymentApplication) deploymentElement, ((DeploymentApplication) deploymentElement).getId());
			}
		}
		executionSchema.getContext().put(ChangeMonitorJob.OUTDATED_ELEMENTS,Maps.newHashMap());
		
		return new DeploymentChangeDelta(
				appearBetweenCheckpoints,
				updateBetweenCheckpoints, 
				disappearBetweenCheckpoints, 
				elementsUpdatedOrDeleted,
				deploymentBetweenCheckpointsChanged
				);
	}

	@Override
	public DeploymentChangeDelta getDeltaSinceLastCheckpoint() {
		return new DeploymentChangeDelta(
				appearAccumulator, 
				updateAccumulator,
				disappearAccumulator, 
				(Map<DeploymentElement, String>) executionSchema.getContext().get(ChangeMonitorJob.OUTDATED_ELEMENTS),
				deploymentChanged
				);
	}

	@Override
	public void startMonitoring() throws IncQueryException {
	    ModelChangeListenerQueries.instance().prepare(engine);
		Set<Job<?>> allJobs = Sets.newHashSet();

		Set<Job<IPatternMatch>> deploymentJobs = createDeploymentJobs();
		allJobs.addAll(deploymentJobs);

		IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>> deploymentHostChangeQuerySpec = (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) DeploymentHostsChangeQuerySpecification
				.instance();
		IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>> deploymentHostIpChangeQuerySpec = (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) DeploymentHostIpChangeQuerySpecification
				.instance();

		registerJobsForPattern(executionSchema, deploymentJobs,
				deploymentHostChangeQuerySpec);
		registerJobsForPattern(executionSchema, deploymentJobs,
				deploymentHostIpChangeQuerySpec);

		Map<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, Set<Job<IPatternMatch>>> querySpecificationsToJobs = getDeploymentElementChangeQuerySpecifications();

		for (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>> querySpec : querySpecificationsToJobs
				.keySet()) {
			registerJobsForPattern(executionSchema,
					querySpecificationsToJobs.get(querySpec), querySpec);
		}
		Collection<Set<Job<IPatternMatch>>> registeredJobs = querySpecificationsToJobs
				.values();
		for (Set<Job<IPatternMatch>> deploymentElementJobs : registeredJobs) {
			allJobs.addAll(deploymentElementJobs);
		}

		executionSchema.startUnscheduledExecution();

		// Enable the jobs to listen to changes
		for (Job<?> job : allJobs) {
			EnableJob<?> enableJob = (EnableJob<?>) job;
			enableJob.setEnabled(true);
		}

	}

	private Set<Job<IPatternMatch>> createDeploymentJobs() {

		Set<Job<IPatternMatch>> jobs = Sets.newHashSet();

		IMatchProcessor<IPatternMatch> matchProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				deploymentChanged = true;
			}
		};

		Job<IPatternMatch> appear = Jobs.newStatelessJob(
				IncQueryActivationStateEnum.APPEARED, matchProcessor);
		Job<IPatternMatch> disappear = Jobs.newStatelessJob(
				IncQueryActivationStateEnum.DISAPPEARED, matchProcessor);
		Job<IPatternMatch> update = Jobs.newStatelessJob(
				IncQueryActivationStateEnum.UPDATED,matchProcessor);
		
		jobs.add(Jobs.newEnableJob(appear));
		jobs.add(Jobs.newEnableJob(disappear));
		jobs.add(Jobs.newEnableJob(update));

		return jobs;
	}

	private void registerJobsForPattern(
			ExecutionSchema executionSchema,
			Set<Job<IPatternMatch>> deploymentElementJobs,
			IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>> changeQuerySpecification) {
		RuleSpecification<IPatternMatch> applicationRules = Rules
				.newMatcherRuleSpecification(changeQuerySpecification,
						Lifecycles.getDefault(true, true),
						deploymentElementJobs);
		executionSchema.addRule(applicationRules);
	}

	private Map<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, Set<Job<IPatternMatch>>> getDeploymentElementChangeQuerySpecifications()
			throws IncQueryException {
		Map<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, Set<Job<IPatternMatch>>> querySpecifications = Maps
				.newHashMap();
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) HostApplicationsChangeQuerySpecification
						.instance(), hostChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) HostIpChangeQuerySpecification
						.instance(), hostChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) ApplicationIdChangeQuerySpecification
						.instance(), applicationChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) ApplicationBehaviorCurrentStateChangeQuerySpecification
						.instance(), applicationChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) BehaviorChangeQuerySpecification
						.instance(), behaviorChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) TransitionChangeQuerySpecification
						.instance(), behaviorChangeJobs());
		querySpecifications
				.put((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) TriggerChangeQuerySpecification
						.instance(), behaviorChangeJobs());
		return querySpecifications;
	}

	private Set<Job<IPatternMatch>> hostChangeJobs() {
		IMatchProcessor<IPatternMatch> appearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerAppear(match);
			}
		};
		IMatchProcessor<IPatternMatch> disappearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				DeploymentHost host = (DeploymentHost) match.get(0);
				if (host.eContainer() != null) {
					registerUpdate(match);
				} else {
					registerDisappear(match);
				}
			}
		};
		IMatchProcessor<IPatternMatch> updateProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerUpdate(match);
			}
		};

		return createDeploymentElementJobs(appearProcessor, disappearProcessor,
				updateProcessor);
	}

	/**
	 * @return specific jobs for applicationChanges
	 */
	private Set<Job<IPatternMatch>> applicationChangeJobs() {
		IMatchProcessor<IPatternMatch> appearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerAppear(match);
			}
		};
		IMatchProcessor<IPatternMatch> disappearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerDisappear(match);
			}
		};
		IMatchProcessor<IPatternMatch> updateProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerUpdate(match);
			}
		};

		return createDeploymentElementJobs(appearProcessor, disappearProcessor,
				updateProcessor);
	}

	private Set<Job<IPatternMatch>> behaviorChangeJobs() {
		IMatchProcessor<IPatternMatch> appearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerAppear(match);
			}
		};
		IMatchProcessor<IPatternMatch> disappearProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				if (match.get("state") != null) {
					BehaviorState state = ((BehaviorState) match.get("state"));
					if (state.eContainer() == null) {
						registerUpdate(match);
					}
				} else if (match.get("transition") != null) {
					BehaviorTransition transition = ((BehaviorTransition) match
							.get("transition"));
					if (transition.eContainer() == null) {
						registerUpdate(match);
					}
				} else {
					registerDisappear(match);
				}
			}
		};
		IMatchProcessor<IPatternMatch> updateProcessor = new IMatchProcessor<IPatternMatch>() {
			@Override
			public void process(IPatternMatch match) {
				registerUpdate(match);
			}
		};

		return createDeploymentElementJobs(appearProcessor, disappearProcessor,
				updateProcessor);
	}

	private void registerUpdate(IPatternMatch match) {
		DeploymentElement deploymentElement = (DeploymentElement) match.get(0);
		if (!appearAccumulator.contains(deploymentElement)) {
			updateAccumulator.add(deploymentElement);
		}
	}

	private void registerAppear(IPatternMatch match) {
		DeploymentElement deploymentElement = (DeploymentElement) match.get(0);

		disappearAccumulator.remove(deploymentElement);
		updateAccumulator.remove(deploymentElement);

		appearAccumulator.add(deploymentElement);
	}

	private void registerDisappear(IPatternMatch match) {
		DeploymentElement deploymentElement = (DeploymentElement) match.get(0);

		appearAccumulator.remove(deploymentElement);
		updateAccumulator.remove(deploymentElement);

		disappearAccumulator.add(deploymentElement);
	}

	private Set<Job<IPatternMatch>> createDeploymentElementJobs(
			IMatchProcessor<IPatternMatch> appearProcessor,
			IMatchProcessor<IPatternMatch> disappearProcessor,
			IMatchProcessor<IPatternMatch> updateProcessor) {
		Set<Job<IPatternMatch>> jobs = Sets.newHashSet();

		Job<IPatternMatch> appear = new ChangeMonitorJob<IPatternMatch>(
				IncQueryActivationStateEnum.APPEARED, appearProcessor);
		Job<IPatternMatch> disappear = new ChangeMonitorJob<IPatternMatch>(
				IncQueryActivationStateEnum.DISAPPEARED, disappearProcessor);
		Job<IPatternMatch> update = new ChangeMonitorJob<IPatternMatch>(
				IncQueryActivationStateEnum.UPDATED, updateProcessor);

		jobs.add(Jobs.newEnableJob(appear));
		jobs.add(Jobs.newEnableJob(disappear));
		jobs.add(Jobs.newEnableJob(update));

		return jobs;
	}

}
