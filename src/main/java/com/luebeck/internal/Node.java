package com.luebeck.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Node {

    private static final String NON_NUMERIC = "[^0-9]+";
    private static final String IS_NUMERIC = "[0-9]+";
    private static final String HAS_DIMENSION = "[-+]?\\d+$";
    private static final UcumEssence ucumEssence = new UcumEssence();

    private String unitSymbol = null;
    private Node leftChild = null;
    private Node rightChild = null;
    private String annotation = null;
    private String prefixSymbol = null;
    private String unitToDissolveTo = null;
    private int prefixExponent = 0;
    private int dimensionExponent = 1;
    private double value = 1;
    private boolean isCaseSens = true;

    /**
     * The actual constructor that gets accessed via generateNode(String token). Operators assume default
     * values whereas Operands are sourced out into their own function createOperand(String token).
     * @param token - the string from which the node is generated
     * @throws UcumException when the input is not a valid UCUM unit
     */
    private Node(String token) throws UcumException {
        switch (token){
            case ".":
            case "/":
                this.unitSymbol = token;
                break;
            default:
                createOperand(token);
                break;
        }
    }

    /**
     * Tries to create an operand from a non-operator token. This includes UCUM units from the essence
     * document as well as integers.
     * @param token - the string from which the operand is generated
     * @throws UcumException when the input is not a valid UCUM unit.
     */
    //No error checking, assumes the parser has already filtered duds
    private void createOperand(String token) throws UcumException {
        //TempToken
        String trimmedToken = token;

        //Trim annotation
        this.annotation = fetchAnnotation(token);
        trimmedToken = discardAnnotation(trimmedToken);

        //Token is a pure integer
        if (trimmedToken.matches(IS_NUMERIC)){
            this.unitSymbol = trimmedToken;
            this.value = Double.parseDouble(trimmedToken);
            return;
        }

        if (containsDimensionExponent(trimmedToken)) {
            this.dimensionExponent = Integer.parseInt(trimmedToken.substring(trimmedToken.split(HAS_DIMENSION)[0].length()));
            //Trim Dimension
            trimmedToken = trimmedToken.split(HAS_DIMENSION)[0];

            //Integer with exponent
            if (trimmedToken.matches(IS_NUMERIC)){
                this.unitSymbol = trimmedToken;
                this.value = Double.parseDouble(trimmedToken);
                return;
            }
        }

        //Actual Unit Symbol verification starts here.
        // Both case-sensitive and capital symbols internally use the case-sensitive representation
        // for further processing if successfully located in the UcumEssence.
        UcumUnit ucumUnit;
        UcumPrefix ucumPrefix;

        //No prefix
        ucumUnit = ucumEssence.lookupCaseSensUnit(trimmedToken);
        if (ucumUnit != null){
            this.unitSymbol = trimmedToken;
            this.value = ucumUnit.getValue();
            this.unitToDissolveTo = ucumUnit.getUnitForNode();
            return;
        }

        ucumUnit = ucumEssence.lookupCapitalUnit(trimmedToken);
        if (ucumUnit != null){
            this.unitSymbol = trimmedToken;
            this.value = ucumUnit.getValue();
            this.unitToDissolveTo = ucumUnit.getUnitForNode();
            this.isCaseSens = false;
            return;
        }

        //Prefix routine begins here
        // The first look-up is always used for the case-sensitive pair
        // The second look-up is always used for the capital pair
        // The look-up with an initial length of three is only used for capital lookups
        if (trimmedToken.length() > 1){
            ucumUnit = ucumEssence.lookupCaseSensUnit(trimmedToken.substring(1));
            ucumPrefix = ucumEssence.lookupCaseSensPrefix(trimmedToken.substring(0,1));
            if (ucumUnit != null && ucumPrefix != null && ucumUnit.isMetric()){
                this.unitSymbol = trimmedToken.substring(1);
                this.value = ucumUnit.getValue();
                this.unitToDissolveTo = ucumUnit.getUnitForNode();
                this.prefixExponent = ucumPrefix.getSup();
                this.prefixSymbol = ucumPrefix.getCodeCaseSens();
                return;
            }

            ucumUnit = ucumEssence.lookupCapitalUnit(trimmedToken.substring(1));
            ucumPrefix = ucumEssence.lookupCapitalPrefix(trimmedToken.substring(0,1));
            if (ucumUnit != null && ucumPrefix != null && ucumUnit.isMetric()){
                this.unitSymbol = trimmedToken.substring(1);
                this.value = ucumUnit.getValue();
                this.unitToDissolveTo = ucumUnit.getUnitForNode();
                this.prefixExponent = ucumPrefix.getSup();
                this.prefixSymbol = ucumPrefix.getCodeCaseSens();
                this.isCaseSens = false;
                return;
            }
        }

        if (trimmedToken.length() > 2){
            ucumUnit = ucumEssence.lookupCaseSensUnit(trimmedToken.substring(2));
            ucumPrefix = ucumEssence.lookupCaseSensPrefix(trimmedToken.substring(0,2));
            if (ucumUnit != null && ucumPrefix != null && ucumUnit.isMetric()){
                this.unitSymbol = trimmedToken.substring(2);
                this.value = ucumUnit.getValue();
                this.unitToDissolveTo = ucumUnit.getUnitForNode();
                this.prefixExponent = ucumPrefix.getSup();
                this.prefixSymbol = ucumPrefix.getCodeCaseSens();
                return;
            }

            ucumUnit = ucumEssence.lookupCapitalUnit(trimmedToken.substring(2));
            ucumPrefix = ucumEssence.lookupCapitalPrefix(trimmedToken.substring(0,2));
            if (ucumUnit != null && ucumPrefix != null && ucumUnit.isMetric()){
                this.unitSymbol = trimmedToken.substring(2);
                this.value = ucumUnit.getValue();
                this.unitToDissolveTo = ucumUnit.getUnitForNode();
                this.prefixExponent = ucumPrefix.getSup();
                this.prefixSymbol = ucumPrefix.getCodeCaseSens();
                this.isCaseSens = false;
                return;
            }
        }

        if (trimmedToken.length() > 3){
            ucumUnit = ucumEssence.lookupCapitalUnit(trimmedToken.substring(3));
            ucumPrefix = ucumEssence.lookupCapitalPrefix(trimmedToken.substring(0,3));
            if (ucumUnit != null && ucumPrefix != null && ucumUnit.isMetric()){
                this.unitSymbol = trimmedToken.substring(3);
                this.value = ucumUnit.getValue();
                this.unitToDissolveTo = ucumUnit.getUnitForNode();
                this.prefixExponent = ucumPrefix.getSup();
                this.prefixSymbol = ucumPrefix.getCodeCaseSens();
                this.isCaseSens = false;
                return;
            }
        }

        throw new UcumException(token + " is not a valid UCUM unit");
    }

    /**
     * Static call for generating Nodes. Accesses the private constructor.
     * @param token - the string from which a node is generated
     * @return A node that is either an operand or an operator
     */
    static Node generateNode(String token){
        try {
            Node node = new Node(token);
            return node;
        } catch (UcumException e){
            return null;
        }
    }

    /**
     * Generates the display name for this node. It consists of the human-readable name of the unit associated
     * with the node, its exponent, its prefix and optionally an annotation.
     * @return Display name of this node
     */
    String generateDisplayName(){
        String dimensionExponent = "";
        String unitSymbol = "";
        String prefixName = "";

        //If it is not a unit it is an integer
        if (ucumEssence.containsUnit(this.unitSymbol) != null){
            unitSymbol = ucumEssence.containsUnit(this.unitSymbol).getName();
        } else {
            unitSymbol = this.unitSymbol;
        }

        switch (this.unitSymbol){
            case ".":
                return " * ";
            case "/":
                return " / ";
            default:
                if (this.dimensionExponent != 1){
                    dimensionExponent = " ^ " + this.dimensionExponent;
                }
                if (this.prefixSymbol != null){
                    prefixName = ucumEssence.lookupCaseSensPrefix(this.prefixSymbol).getName();
                }
                return "[" + prefixName +
                        unitSymbol +
                        dimensionExponent +
                        getAnnotationForDisplayName() + "]";
        }
    }

    /**
     * Discards the annotation associated with a given input. For example, g{feathers} would become g by calling
     * this function on it.
     * @param input - the input from which the annotation is to be discarded
     * @return Input without its annotation
     */
    private String discardAnnotation(String input){
        if(input.startsWith("{") && input.endsWith("}")){
            input = "1";
        }
        if(input.contains("{")){
            input = input.substring(0,input.indexOf("{"));
        }
        return input;
    }

    /**
     * Extracts the annotation of a given input should it exist. For example, g{feathers} would return {feathers}
     * @param input - the input from which the annotation is to be extracted
     * @return The input's annotation
     */
    private String fetchAnnotation(String input){
        int index;
        if ((index = input.indexOf("{")) != -1 && input.endsWith("}")){
            return input.substring(index);
        } else {
            return null;
        }
    }

    /**
     * Verifies whether the given input contains a dimension exponent.
     * @param input - the input that is tested for containing an exponent
     * @return Existence status of the input's dimension exponent
     */
    private boolean containsDimensionExponent(String input){
        Pattern pattern = Pattern.compile(HAS_DIMENSION, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * Verifies whether this node represents an integer.
     * @return This node's status as representing an integer operand
     */
    boolean isNumeric(){
        return !this.unitSymbol.matches(NON_NUMERIC);
    }

    /**
     * Calculate this node's final value taking into account the prefix exponent, dimension exponent and this node's
     * value according to the UCUM essence document.
     * @return This node's final value (or magnitude)
     */
    double calculateFinalValue(){
        double prefixValue = Math.pow(10,prefixExponent);
        if (isNumeric()){
            return Math.pow(value, dimensionExponent);
        } else {
            return this.value * Math.pow(prefixValue, dimensionExponent);
        }
    }

    /**
     * Generates an annotation for this node that is formatted in a way to accommodate the display name
     * generator.
     * @return This node's annotation formatted in a human-readable way for the display anem generator
     */
    String getAnnotationForDisplayName(){
        if (this.annotation != null){
            return " of " + this.annotation;
        } else {
            return "";
        }
    }

    /**
     * Sets the left child node of this node.
     * @param leftChild - The node to become the left child
     */
    void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * Sets the right child node of this node.
     * @param rightChild - the node to become the right child
     */
    void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Return the left child node of this node.
     * @return Node's left child
     */
    Node getLeftChild() {
        return leftChild;
    }

    /**
     * Return the right child node of this node.
     * @return Node's right child
     */
    Node getRightChild() {
        return rightChild;
    }

    /**
     * Verifies whether this node was created from a case-sensitive or a capital unit.
     * @return Case Sensitivity status
     */
    boolean isCaseSens(){
        return isCaseSens;
    }

    /**
     * Verifies whether this node contains a left child node.
     * @return Existence status of the left child node
     */
    boolean hasLeftChild(){
        return this.leftChild != null;
    }

    /**
     * Verifies whether this node contains a right child node.
     * @return Existence status of the right child node
     */
    boolean hasRightChild(){
        return this.rightChild != null;
    }

    /**
     * Returns the unit this node's current unit gets dissolved to according to the UCUM essence (e.g., L to m3)
     * @return Next unit to dissolve to according to the UCUM Essence
     */
    String getUnitToDissolveTo() {
        return unitToDissolveTo;
    }

    /**
     * Returns this node's dimension exponent
     * @return This node's dimension exponent
     */
    public int getDimensionExponent() {
        return dimensionExponent;
    }

    /**
     * Sets this node's dimension exponent
     * @param dimensionExponent - The dimension exponent to be associated with this node
     */
    void setDimensionExponent(int dimensionExponent) {
        this.dimensionExponent = dimensionExponent;
    }

    /**
     * Inverts this node's right child node should it exist. It is used to turn branches associated with the
     * right side of a division operator node into multiplications which makes evaluating the tree
     * simpler.
     */
    void invertRightChildDimensionExponent(){
        if (this.rightChild != null){
            this.rightChild.dimensionExponent = this.rightChild.dimensionExponent * -1;
        }
    }

    /**
     * Verifies whether this node is a base unit.
     * @return This node's base unit status
     */
    boolean canBeDissolved(){
        return this.unitToDissolveTo != null;
    }


    /**
     * Verifies whether this node is arbitrary meaning it contains an arbitrary unit such as [CCID_50].
     * @return The status of this node being considered arbitrary
     */
    boolean isArbitrary(){
        if (isNumeric()){
            return false;
        }
        return ucumEssence.containsUnit(this.unitSymbol).isArbitrary();
    }

    /**
     * Verifies whether this node is special meaning it contains a special (non-ratio) unit such as Cel.
     * @return The status of this node being considered special
     */
    boolean isSpecial(){
        if (isNumeric()){
            return false;
        }
        return ucumEssence.containsUnit(this.unitSymbol).isSpecial();
    }

    /**
     * Returns this node's unit symbol meaning the valid UCUM Unit without an exponent, prefix or annotation.
     * @return This node's pure unit symbol
     */
    String getUnitSymbol() {
        return unitSymbol;
    }
}
