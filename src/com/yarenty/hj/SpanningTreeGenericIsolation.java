package com.yarenty.hj;

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module2.isolatedWithReturn;

/**
 * <p>SpanningTreeGenericIsolation class.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public class SpanningTreeGenericIsolation {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(final String[] args) throws SuspendableException {
        int global_num_neighbors = 200;
        int num_nodes = 100_000;
        if (args.length > 0) {
            num_nodes = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            global_num_neighbors = Integer.parseInt(args[1]);
        }
        if (num_nodes < 2) {
            System.out.println("Error: number of nodes must be > 1");
            return;
        }
        if (global_num_neighbors < -1) {
            System.out.println("Error: negative number of neighbors entered\n");
            return;
        }

        final java.util.Random rand = new java.util.Random(12399);
        final Node[] nodes = new Node[num_nodes];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(Integer.toString(i));
        }
        for (int i = 0; i < nodes.length; i++) {
            final int num_neighbors = ((global_num_neighbors == -1) ? rand.nextInt(10) : global_num_neighbors);
            final Node[] neighbors = new Node[num_neighbors];
            for (int j = 0; j < neighbors.length; j++) {
                int neighbor_index = rand.nextInt(nodes.length);
                if (neighbor_index == i) {
                    neighbor_index = (neighbor_index + 1) % num_nodes;
                }
                neighbors[j] = nodes[neighbor_index];
            }
            nodes[i].SetNeighbors(neighbors);
        }

        for (int run_no = 0; run_no < 3; run_no++) {
            for (final Node node : nodes) {
                node.parent = null;
            }
            final Node root = nodes[0];
            root.parent = root;
            final long start = System.currentTimeMillis();
            finish(root::compute);
            final long stop = System.currentTimeMillis();
            System.out.println("Time: " + (stop - start) + " ms");
        }

    }

    private static class Node {
        Node[] neighbors;
        public Node parent = null;
        String name;

        public Node(String set_name) {
            neighbors = new Node[0];
            name = set_name;
        }

        public void SetNeighbors(Node[] n) {
            neighbors = n;
        }

        boolean tryLabeling(Node n) {
            return isolatedWithReturn(() -> {
                if (parent == null) {
                    parent = n;
                }
                return parent == n;
            });
        }

        void compute() {
            for (int i = 0; i < neighbors.length; i++) {
                Node child = neighbors[i];
                if (child.tryLabeling(this)) {
                    async(child::compute);
                }
            }
        }
    }
}


