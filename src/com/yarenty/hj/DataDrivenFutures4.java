package com.yarenty.hj;

import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.metrics.AbstractMetricsManager;
import edu.rice.hj.runtime.metrics.HjMetricsImpl;

import static edu.rice.hj.Module1.*;

/**
 * Example to verify use of metrics with DDFs.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class DataDrivenFutures4 {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {

        HjSystemProperty.abstractMetrics.setProperty("true");

        finish(() -> {
            final HjFuture<Integer> A = future(() -> {
                doWork(1);
                return 1;
            });
            final HjFuture<Integer> B = futureAwait(A, () -> {
                doWork(1);
                return 1 + A.get();
            });
            final HjFuture<Integer> C = futureAwait(A, () -> {
                doWork(1);
                return 1 + A.get();
            });
            final HjFuture<Integer> D = futureAwait(B, C, () -> {
                doWork(1);
                return 1 + Math.max(B.get(), C.get());
            });
            final HjFuture<Integer> E = futureAwait(C, () -> {
                doWork(1);
                return 1 + C.get();
            });
            final HjFuture<Integer> F = futureAwait(D, E, () -> {
                doWork(1);
                final int res = 1 + Math.max(D.get(), E.get());
                System.out.println("Res = " + res);
                return res;
            });
        });

        final HjMetrics actualMetrics = abstractMetrics();
        AbstractMetricsManager.dumpStatistics(actualMetrics);

        final HjMetrics expectedMetrics = new HjMetricsImpl(6, 4, 1.5);
        if (!expectedMetrics.equals(actualMetrics)) {
            throw new IllegalStateException("Expected: " + expectedMetrics + ", found: " + actualMetrics);
        }

    }

}
