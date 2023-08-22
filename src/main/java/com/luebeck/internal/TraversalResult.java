package com.luebeck.internal;

class TraversalResult {

    private int[] canonVector = new int[7];
    private double value = 1;

    /**
     * The actual constructor that gets accessed via generateTraversalResult(Node root). Calls recursiveTraversal
     * in order to populate its member variables.
     * @param root - the UCUM unit's tree's root
     */
    private TraversalResult(Node root) {
        recursiveTraversal(root);
    }

    /**
     * Static call for generating TraversalResults. Accesses the private constructor.
     * @param root - the UCUM unit's tree's root
     * @return TraversalResult of the given root and its associated tree structure
     */
    static TraversalResult generateTraversalResult (Node root){
        return new TraversalResult(root);
    }

    /**
     * Populates the TraversalResult member variables by recursively traversing the associated tree.
     * @param currentNode - the node currently being evaluated
     */
    private void recursiveTraversal(Node currentNode) {

        this.value = this.value * currentNode.calculateFinalValue();

        switch (currentNode.getUnitSymbol()) {
            case "m":
                canonVector[0] = canonVector[0] + currentNode.getDimensionExponent();
                break;
            case "s":
                canonVector[1] = canonVector[1] + currentNode.getDimensionExponent();
                break;
            case "g":
                canonVector[2] = canonVector[2] + currentNode.getDimensionExponent();
                break;
            case "rad":
                canonVector[3] = canonVector[3] + currentNode.getDimensionExponent();
                break;
            case "K":
                canonVector[4] = canonVector[4] + currentNode.getDimensionExponent();
                break;
            case "C":
                canonVector[5] = canonVector[5] + currentNode.getDimensionExponent();
                break;
            case "cd":
                canonVector[6] = canonVector[6] + currentNode.getDimensionExponent();
                break;
            default:
                break;
        }

            if (currentNode.hasLeftChild()) {
                recursiveTraversal(currentNode.getLeftChild());
            }

            if (currentNode.hasRightChild()) {
                recursiveTraversal(currentNode.getRightChild());
            }
        }

    /**
     * Returns the value of this TraversalResult.
     * @return value of this TraversalResult
     */
    double getValue() {
        return value;
    }

    /**
     * Returns the canon vecotr (base unit representation) of this TraversalResult.
     * @return canonc vecotr of this TraversalResult
     */
    int[] getCanonVector(){
        return canonVector;
    }

    /**
     * Multiplies this TraversalResult's current value with quantity.
     * @param quantity - The quantity to multiply this TravesalResult's value with
     */
    void multiplyValue(double quantity){
        value *= quantity;
    }

    /**
     * Generates a single base unit representation used to express the canonized form of this TraversalResult
     * in just base units. Used seven times in total, once for each of the base units.
     * @param baseUnit - One of the seven base units
     * @param count - The amount of base units (e.g., m2 -> 2)
     * @return The canonic base unit representation for a single part of the entire canon representation
     */
    private String baseUnit(String baseUnit, int count){
        if (count==0){
            return "";
        } else if (count==1){
            return baseUnit + ".";
        } else {
            return baseUnit + count + ".";
        }
    }

    /**
     * Generates a string used to express the canon vector of this TraversalResult in base units.
     * @return Base unit representation of this TraversalResult.
     */
    String generateCanonVectorAsBaseUnit () {
        String output = baseUnit("m", canonVector[0]) +
                baseUnit("s", canonVector[1]) +
                baseUnit("g", canonVector[2]) +
                baseUnit("rad", canonVector[3]) +
                baseUnit("K", canonVector[4]) +
                baseUnit("C", canonVector[5]) +
                baseUnit("cd", canonVector[6]);

        if (output.isEmpty()){
            return "1";
        } else {
            return output.substring(0, output.length()-1);//truncate final operator
        }
    }

}
