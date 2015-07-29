package io.probedock.rt.client.junit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import io.probedock.client.junit.AbstractProbeListener;
import io.probedock.rt.client.Configuration;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * The Junit ProbeRTListener is a wrapper to the {@link io.probedock.rt.client.Listener} to
 * get the Junit {@link org.junit.runner.Description}, transform and send them to Probe Dock RT.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ProbeRTListener extends AbstractProbeListener {
	private static final Logger LOGGER = Logger.getLogger(ProbeRTListener.class.getCanonicalName());
	
	/**
	 * Mini ROX configuration
	 */
	private static final Configuration rtConfiguration = Configuration.getInstance();
	
	/**
	 * Mini ROX listener wrapped
	 */
	private final io.probedock.rt.client.Listener rtListener = new io.probedock.rt.client.Listener();
	
	/**
	 * Store the test that fail to handle correctly the difference between test
	 * failures and test success in the testFinished method.
	 */
	private final Set<String> testFailures = new HashSet<>();

	public ProbeRTListener() {}
	
	public ProbeRTListener(String category) {
		super(category);
	}
	
	@Override
	public void testRunStarted(Description description) throws Exception {
		super.testRunStarted(description);
		
		if (!rtConfiguration.isEnabled()) {
			return;
		}

		rtListener.testRunStart(
			configuration.getProjectApiId(),
			configuration.getProjectVersion(),
			getCategory(null, null)
		);
	}
	
	@Override
	public void testRunFinished(Result result) throws Exception {
		if (!rtConfiguration.isEnabled()) {
			return;
		}
		
		long runEndedDate = System.currentTimeMillis();
			
		// Notify mini ROX that the test run is finished
		rtListener.testRunEnd(
			configuration.getProjectApiId(),
			configuration.getProjectVersion(),
			getCategory(null, null),
			runEndedDate - runStartedDate
		);
	}
	
	@Override
	public void testStarted(Description description) throws Exception {
		super.testStarted(description);

		if (!rtConfiguration.isEnabled()) {
			return;
		}
		
		// Register the test for date calculation by the technical name
		testStartDates.put(getFingerprint(description), System.currentTimeMillis());
	}

	@Override
	public void testFinished(Description description) throws Exception {
		super.testFinished(description);

		if (!rtConfiguration.isEnabled()) {
			return;
		}
		
		// Detect if the test is in failure
		if (!testFailures.contains(getFingerprint(description))) {
			rtListener.testResult(
				createTestResult(getFingerprint(description), description, getMethodAnnotation(description), getClassAnnotation(description), true, null),
				configuration.getProjectApiId(),
				configuration.getProjectVersion(),
				getCategory(null, null)
			);
		}
	}	

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);

		if (!rtConfiguration.isEnabled()) {
			return;
		}
		
		Description description = failure.getDescription();
		String fingerprint = getFingerprint(description);
		
		// Register the test in the failures
		testFailures.add(fingerprint);

		rtListener.testResult(
			createTestResult(getFingerprint(description), description, getMethodAnnotation(description), getClassAnnotation(description), false, createAndlogStackTrace(failure)),
			configuration.getProjectApiId(),
			configuration.getProjectVersion(),
			getCategory(null, null)
		);
	}
}