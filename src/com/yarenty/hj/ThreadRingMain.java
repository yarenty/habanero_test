package com.yarenty.hj;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;

import static edu.rice.hj.Module1.finish;

/**
 * <p>ThreadRingMain class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 *         Created: Mar/08/12 8:29 PM
 */
public class ThreadRingMain {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {
        final int numThreads = 4;
        final int numberOfHops = 10;

        finish(() -> {

            final ThreadRingActor[] ring = new ThreadRingActor[numThreads];
            for (int i = numThreads - 1; i >= 0; i--) {
                ring[i] = new ThreadRingActor(i);
                ring[i].start();
                if (i < numThreads - 1) {
                    ring[i].nextActor(ring[i + 1]);
                }
            }
            ring[numThreads - 1].nextActor(ring[0]);

            ring[0].send(numberOfHops);
        });
    }

    private static class ThreadRingActor extends Actor<Object> {

        private Actor<Object> nextActor;
        private final int id;

        ThreadRingActor(int id) {
            this.id = id;
        }

        public void nextActor(Actor<Object> nextActor) {
            this.nextActor = nextActor;
        }

        @Override
        protected void process(Object theMsg) {
            if (theMsg instanceof Integer) {
                Integer n = (Integer) theMsg;
                if (n > 0) {
                    System.out.println("Thread-" + id + " got token, remaining = " + n);
                    nextActor.send(n - 1);
                } else {
                    System.out.println("Terminating Thread-" + id);
                    nextActor.send(-1);
                    exit();
                }
            } else { /* ERROR - handle appropriately */ }
        }
    }
}
