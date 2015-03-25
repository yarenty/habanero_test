package com.yarenty.hj;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;

import static edu.rice.hj.Module1.*;

/**
 * <p>IntegerCounterActorVersion class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class IntegerCounterActorVersion {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {

        initializeHabanero();

        final Counter counter = new Counter();

        finish(() -> {

            counter.start();
            final IntegerCounterActorVersion actor = new IntegerCounterActorVersion(counter);

            finish(() -> {
                for (int i = 0; i < 10; i++) {
                    async(() -> {
                        actor.inc();
                        actor.dec();
                    });
                }
            });
            counter.send(Counter.EXIT);
        });

        System.out.println("Final counter value = " + counter.counter());

        finalizeHabanero();
    }

    private final Counter counter;

    /**
     * <p>Constructor for IntegerCounterActorVersion.</p>
     *
     * @param counter a {@link edu.rice.hj.example.comp322.actors.IntegerCounterActorVersion.Counter} object.
     */
    public IntegerCounterActorVersion(final Counter counter) {
        this.counter = counter;
    }

    /**
     * <p>inc.</p>
     */
    public void inc() {
        // do something
        counter.send(Counter.INCREMENT);
        // do something else
    }

    /**
     * <p>dec.</p>
     */
    public void dec() {
        // do something
        counter.send(Counter.DECREMENT);
        // do something else
    }

    private static class Counter extends Actor<Object> {

        public static final Object INCREMENT = new Object();
        public static final Object DECREMENT = new Object();
        public static final Object EXIT = new Object();

        private int counter = 0;

        public int counter() {
            return counter;
        }

        @Override
        protected void process(final Object theMsg) {
            if (theMsg == INCREMENT) {
                counter++;
                System.out.println("After INCREMENT, counter = " + counter);
            } else if (theMsg == DECREMENT) {
                counter--;
                System.out.println("After DECREMENT, counter = " + counter);
            } else if (theMsg == EXIT) {
                exit();
            }
        }
    }
}
