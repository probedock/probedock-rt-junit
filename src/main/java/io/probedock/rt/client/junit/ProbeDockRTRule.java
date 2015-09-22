package io.probedock.rt.client.junit;

import io.probedock.client.junit.ProbeFilter;
import io.probedock.rt.client.Filter;
import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Workaround to avoid using a runner when other runner is used. In place, use this rule to filter
 * the tests to run
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ProbeDockRTRule implements TestRule {
    ProbeFilter filter = new ProbeFilter(new Filter().getFilters());

    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (filter.shouldRun(description)) {
                    base.evaluate();
                }
                else {
                    // do this so our test gets marked as ignored.  Not pretty, but it works
                    Assume.assumeTrue(false);
                }
            }
        };
    }
}
