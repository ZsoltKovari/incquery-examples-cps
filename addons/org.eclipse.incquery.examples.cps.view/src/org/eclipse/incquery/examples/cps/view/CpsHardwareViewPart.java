package org.eclipse.incquery.examples.cps.view;

import java.util.Collection;

import org.eclipse.incquery.examples.cps.model.viewer.util.AllHostInstancesQuerySpecification;
import org.eclipse.incquery.examples.cps.model.viewer.util.ConnectTypesAndInstancesHostQuerySpecification;
import org.eclipse.incquery.examples.cps.model.viewer.util.HostTypesQuerySpecification;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.collect.ImmutableSet;

public class CpsHardwareViewPart extends AbstractCpsViewPart {

	@Override
	protected Collection<IQuerySpecification<?>> getSpecifications()
			throws IncQueryException {
		return ImmutableSet.<IQuerySpecification<?>>of(
				//CommunicationsQuerySpecification.instance(),
				ConnectTypesAndInstancesHostQuerySpecification.instance(),
				AllHostInstancesQuerySpecification.instance(),
				HostTypesQuerySpecification.instance()
				);
	}

}
