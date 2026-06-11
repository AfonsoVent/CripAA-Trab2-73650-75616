package crypto;

import java.io.Serializable;

import model.MOpeNode;

// Responsable to handle with binary trees of OPE
public class MOpeTree implements Serializable {
    // Allows save the tree from file
    private static final long serialVersionUID = 1L;

    private MOpeNode root;
    public MOpeTree() { this.root = null; }

    // Start of recursive insert tree
    public void insert(long value) {
        if (root == null) {
            // Root: Midpoint of range of a positive Long
            root = new MOpeNode(value, Long.MAX_VALUE / 2);
            return;
        }

        insertRecursive(root, value, Long.MAX_VALUE / 2, Long.MAX_VALUE / 4);
    }

    // Put the value, until found a "leaf"
    private void insertRecursive(MOpeNode current, long value, long currentMetric, long step) {
        // Ignore duplicates
        if (value == current.plaintext) return;

        if (value < current.plaintext) {
            // go left to decrease metric
            if (current.left == null) {
                current.left = new MOpeNode(value, currentMetric - step);
            } else {
                insertRecursive(current.left, value, currentMetric - step, step / 2);
            }
        } else {
            // go right to increase metric
            if (current.right == null) {
                current.right = new MOpeNode(value, currentMetric + step);
            } else {
                insertRecursive(current.right, value, currentMetric + step, step / 2);
            }
        }
    }

    // Returns metric OPE for a value
    public long getMetricFor(long value) {
        MOpeNode current = root;

        while (current != null) {
            if (value == current.plaintext) {
                return current.opeValue;
            } else {
                if (value < current.plaintext) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
        }

        throw new IllegalArgumentException("Searched completed, value: " + value + " not founded in tree.");
    }
}