package com.yarenty.hj;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;

import java.util.Random;

import static edu.rice.hj.Module1.*;

/**
 * <p>TrigSumTaskParActorMain class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class TrigSumTaskParActorMain {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {

        final int limit = 50;
        System.out.println("TrigSumTaskParActorMain.main: limit = " + limit);

        initializeHabanero();

        for (int iter = 0; iter < 3; iter++) {
            System.out.printf("Run %d\n", iter);

            final long parStartTime = System.nanoTime();

            final ConsumerActor consumerActor = new ConsumerActor();
            final ProducerActor producerActor = new ProducerActor(consumerActor);

            finish(() -> {
                consumerActor.start();
                producerActor.start();

                for (int i = 1; i <= limit; i++) {
                    producerActor.send(i);
                }
                producerActor.send(null);
            });

            final long parExecTime = System.nanoTime() - parStartTime;
            System.out.println("  Completed in " + parExecTime / 1e6 + " milliseconds");
        }

        finalizeHabanero();
    }

    private static class ProducerActor extends Actor<Object> {

        private final Random random = new Random(123456L);
        private final ConsumerActor consumerActor;

        private ProducerActor(final ConsumerActor consumerActor) {
            this.consumerActor = consumerActor;
        }

        @Override
        protected void process(final Object theMsg) {
            if (theMsg != null) {
                // produce an array and forward to max finder
                final double[] newArray = produceArray();
                consumerActor.send(newArray);
            } else {
                // signal termination to next actor before terminating self
                consumerActor.send(null);
                exit();
            }
        }

        private double[] produceArray() {
            final int baseSize = 500_000;
            final int newSize = (int) (baseSize + (baseSize * random.nextDouble()));
            final double[] resArray = new double[newSize];
            for (int i1 = 0; i1 < resArray.length; i1++) {
                resArray[i1] = (1_000 * random.nextDouble());
            }
            return resArray;
        }
    }

    private static class ConsumerActor extends Actor<Object> {

        private double resultSoFar = 0;

        @Override
        protected void process(final Object theMsg) {
            if (theMsg != null) {

                // final long parStartTime = System.nanoTime();

                // find the maximum
                final double[] dataArray = (double[]) theMsg;
                double localRes = 0.0d;
                try {
                    localRes = doComputation(dataArray);
                } catch (SuspendableException e) {
                    e.printStackTrace();
                }
                resultSoFar += localRes;

                // final long parExecTime = System.nanoTime() - parStartTime;
                // System.out.println("  Completed in " + parExecTime / 1e6 + " milliseconds");

            } else {
                System.out.println(" Result: " + resultSoFar);
                exit();
            }
        }

        private double doComputation(final double[] dataArray) throws SuspendableException {

            final double[] localSum = new double[2];
            finish(() -> {
                final int limit1 = dataArray.length / 2;
                async(() -> {
                    localSum[0] = doComputation(dataArray, 0, limit1);
                });
                localSum[1] = doComputation(dataArray, limit1, dataArray.length);
            });
            return localSum[0] + localSum[1];
        }

        private double doComputation(final double[] dataArray, final int startIndex, final int endIndex) {
            double localRes = 0;
            for (int i = startIndex; i < endIndex; i++) {
                final double v = Math.sin(dataArray[i]) * Math.cos(dataArray[i]);
                localRes += v;
            }
            return localRes;
        }
    }
}
