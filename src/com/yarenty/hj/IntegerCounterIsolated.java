package com.yarenty.hj;

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.*;
import static edu.rice.hj.Module2.isolated;

/**
 * <p>IntegerCounterIsolated class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class IntegerCounterIsolated {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {

        initializeHabanero();

        final IntegerCounterIsolated anObj = new IntegerCounterIsolated();
        finish(() -> {
            for (int i = 0; i < 100; i++) {

                async(anObj::foo);
                async(anObj::bar);
                async(anObj::foo);

            }
        });

        System.out.println("Counter = " + anObj.counter());

        finalizeHabanero();
    }

    // Can also use atomic variables instead of isolated
    private int counter = 0;

    private int counter() {
        return counter;
    }

    /**
     * <p>foo.</p>
     */
    public void foo() {
        // do something
        isolated(() -> {
            counter++;
        });
        // do something else
    }

    /**
     * <p>bar.</p>
     */
    public void bar() {
        // do something
        isolated(() -> {
            counter--;
        });
        // do something else
    }
}
