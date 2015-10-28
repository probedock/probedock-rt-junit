package io.probedock.rt.client.junit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import io.probedock.client.common.config.ProbeConfigurationException;
import io.probedock.client.common.utils.TestResultDataUtils;
import io.probedock.client.junit.AbstractProbeListener;
import io.probedock.rt.client.Configuration;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * The Junit ProbeRTListener is a wrapper to the {@link io.probedock.rt.client.Listener} to get the Junit {@link
 * org.junit.runner.Description}, transform and send them to Probe Dock RT.
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
     * Store the test that fail to handle correctly the difference between test failures and test success in the
     * testFinished method.
     */
    private final Set<String> testFailures = new HashSet<>();

    /**
     * Store the ignored tests to not send them to Probe Dock RT
     */
    private final Set<String> testIgnored = new HashSet<>();

    /**
     * Project data
     */
    private String projectApiId;
    private String projectVersion;

    public ProbeRTListener() {
        try {
            projectApiId = configuration.getProjectApiId();
            projectVersion = configuration.getProjectVersion();
        } catch (ProbeConfigurationException pce) {
            LOGGER.warning(
                "Unable to retrieve the project API ID, the probedock.yml project configuration is probably missing. " +
                    "Dummy data for project API ID and version will be used in place."
            );

            projectApiId = "Any";
            projectVersion = "Any";
        }
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);

        if (!rtConfiguration.isEnabled()) {
            return;
        }

        rtListener.testRunStart(
            projectApiId,
            projectVersion,
            TestResultDataUtils.getCategory(configuration, null, null, getCategory())
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
            projectApiId,
            projectVersion,
            TestResultDataUtils.getCategory(configuration, null, null, getCategory()),
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

        String fingerprint = getFingerprint(description);

        // Detect if the test is in failure and do not send test result when the test is ignored
        if (!testIgnored.contains(fingerprint) && !testFailures.contains(fingerprint)) {
            rtListener.testResult(
                createTestResult(getFingerprint(description), description, getMethodAnnotation(description), getClassAnnotation(description), true, null),
                projectApiId,
                projectVersion,
                TestResultDataUtils.getCategory(description.getTestClass().getPackage().getName(), configuration, null, null, getCategory())
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

        // Do not send test result when the test is ignored
        if (testIgnored.contains(fingerprint)) {
            return;
        }

        // Register the test in the failures
        testFailures.add(fingerprint);

        rtListener.testResult(
            createTestResult(getFingerprint(description), description, getMethodAnnotation(description), getClassAnnotation(description), false, createAndlogStackTrace(failure)),
            projectApiId,
            projectVersion,
            TestResultDataUtils.getCategory(description.getTestClass().getPackage().getName(), configuration, null, null, getCategory())
        );
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
        registerIgnoredTest(failure.getDescription());
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
        registerIgnoredTest(description);
    }

    /**
     * Register an ignored test to not send it to Probe Dock RT
     *
     * @param description The description of the test
     */
    private void registerIgnoredTest(Description description) {
        if (rtConfiguration.isEnabled()) {
            testIgnored.add(getFingerprint(description));
        }
    }
}