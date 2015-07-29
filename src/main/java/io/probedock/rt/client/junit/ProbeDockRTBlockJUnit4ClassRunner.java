package io.probedock.rt.client.junit;

import io.probedock.client.junit.ProbeFilter;
import io.probedock.rt.client.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.logging.Logger;

/**
 * Extend the standard junit runner to add the filtering features
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ProbeDockRTBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {
	private static final Logger LOGGER = Logger.getLogger(ProbeDockRTBlockJUnit4ClassRunner.class.getCanonicalName());

	public ProbeDockRTBlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
		
		try {
			filter(new ProbeFilter(new Filter().getFilters()));
		}
		catch (NoTestsRemainException ntre) {
			LOGGER.info("No remaining tests to run for class: " + klass.getCanonicalName());
		}
	}
}
