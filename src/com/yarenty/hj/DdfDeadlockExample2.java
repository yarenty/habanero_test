package com.yarenty.hj;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;

import static edu.rice.hj.Module1.*;

/**
 * Example to verify use of metrics with DDFs.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class DdfDeadlockExample2 {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {

        HjSystemProperty.trackDeadlocks.setProperty("true");

        initializeHabanero();

        final HjDataDrivenFuture<Integer> right = newDataDrivenFuture();
        final HjDataDrivenFuture<Integer> left = newDataDrivenFuture();
        finish(() -> {
            // async that waits on left before resolving right
            asyncAwait(left, () -> {
                right.put(1);
            });
            // async that waits on right before resolving left
            asyncAwait(right, () -> {
                left.put(2);
            });
        });

        finalizeHabanero();
    }

}
