package com.luebeck.internal;

public class UcumFunction {

    private static final UcumParser ucumParser = new UcumParser();

    /**
     * Generates the UCUM service that is used to access the library's functions.
     */
    public UcumFunction(){

    }

    /**
     * Generates tokens from a given source. Assumes the source represents a valid UCUM unit.
     * @param source - the input string that will get split up
     * @return Operands and Operators extracted from source
     */
    private String[] generateTokens(String source){
        return ucumParser.tokenize(source);
    }

    /**
     * Determines whether the given input is a valid UCUM expression or not.
     * @param source - the source unit
     * @return Validity of the input according to the UCUM syntax
     */
    public boolean isValid(String source){

        if (!ucumParser.passesInitialSyntaxCheck(source)){
            return false;
        }

        String[] tokens = generateTokens(source);

        if (isMixedCase(tokens)){
            return false;
        }

        //TODO Pass on to the other functions that regulate input validity
        if (!passesSpecialUnitOperationCheck(tokens)){
            return false;
        }

        return true;
    }

    /**
     * Determines whether the source is eligible to partake in tasks related to canonization. This excludes
     * arbitrary units.
     * @param source - the source unit
     * @return Eligibility for partaking in tasks related to canonization
     */
    public boolean eligibleForCanonization(String source){

        if (!ucumParser.passesInitialSyntaxCheck(source)){
            return false;
        }

        String[] tokens = generateTokens(source);

        if (isMixedCase(tokens)){
            return false;
        }

        if (containsArbitraryUnits(tokens)){
            return false;
        }

        if (!passesSpecialUnitOperationCheck(tokens)){
            return false;
        }

        return true;
    }

    /**
     * Determines whether the source is eligible to partake in 'operations' such as multiplication, division etc.
     * @param source - the source unit
     * @return Eligibility for partaking in tasks related to operations such as multiplication and division
     */
    // Betrifft alle Umwandlungsaufgaben
    public boolean eligibleForOperations(String source){

        if (!ucumParser.passesInitialSyntaxCheck(source)){
            return false;
        }

        String[] tokens = generateTokens(source);

        if (isMixedCase(tokens)){
            return false;
        }

        //TODO Remove containsSpecialUnits upon implementing Special unit conversions
        if (containsSpecialUnits(tokens)){
            return false;
        }

        //TODO Then reactivate this
        //if (!passesSpecialUnitOperationCheck(tokens)){
        //    return false;
        //}

        if (containsArbitraryUnits(tokens)){
            return false;
        }

        return true;
    }

    /**
     * Determines whether an array of tokens contains a mix of case-sensitive and capital unit symbols.
     * @param tokens - the units to be verified
     * @return Status of mixed case usage of unit symbols
     */
    private boolean isMixedCase(String[] tokens){
        Node currentNode;
        boolean precedent = false;
        boolean firstOperandFound = false;

        if (tokens.length == 1){
            return false;
        }

        for (int i = 0; i < tokens.length; i++) {

            switch (tokens[i]){
                case ".":
                case "/":
                case "(":
                case ")":
                    break;
                default:
                    currentNode = Node.generateNode(tokens[i]);

                    if (currentNode == null) {
                        return true;
                    }

                    if (firstOperandFound){
                        if ((currentNode.isCaseSens() != precedent) &&
                                !currentNode.isNumeric()){
                            return true;
                        }
                    } else {
                        precedent = currentNode.isCaseSens();
                        firstOperandFound = true;
                    }
            }
        }

        return false;
    }

    /**
     * Determines whether an array of tokens passes the rules laid out in the 'Special' section of
     * the UCUM guidelines meaning a valid UCUM term containing a special (non-ratio) unit may only contain
     * additional scalars and no other units.
     * @param tokens - the units to be verified
     * @return Status of Special Unit syntax violation
     */
    private boolean passesSpecialUnitOperationCheck(String[] tokens) {
        Node currentNode;
        boolean nonSpecialFlag = false;
        boolean specialFlag = false;

        for (int i = 0; i < tokens.length; i++) {
            switch (tokens[i]) {
                case ".":
                case "/":
                case "(":
                case ")":
                    continue;
                default:
                    currentNode = Node.generateNode(tokens[i]);

                    if (currentNode == null){
                        return false;
                    }
                    if (currentNode.isNumeric()) {
                        continue;
                    }
                    if (currentNode.isSpecial()) {
                        specialFlag = true;
                        if (!passesSpecialUnitExponentCheck(currentNode)){
                            return false;
                        }
                    } else {
                        nonSpecialFlag = true;
                    }
            }
        }

        if (nonSpecialFlag && specialFlag){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines whether a node representing a special unit contains an exponent other than '1'. Special
     * units may not be part of UCUM expressions involving anything but scalars. 'Cel2' would naturally
     * be equivalent to formulating the expression as 'Cel.Cel' etc.
     * @param node - the node to be verified
     * @return Special unit syntax rule violation related to their usage in mathematical operations
     */
    private boolean passesSpecialUnitExponentCheck(Node node){
        if (node.isSpecial() &&
                node.getDimensionExponent() != 1){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines whether an array of tokens contains arbitrary units.
     * @param tokens - the units to be verified
     * @return Status of a UCUM expression containing arbitrary units
     */
    private boolean containsArbitraryUnits(String[] tokens){

        Node currentNode;
        for (int i = 0; i < tokens.length; i++) {
            switch (tokens[i]){
                case ".":
                case "/":
                case "(":
                case ")":
                    continue;
                default:
                    currentNode = Node.generateNode(tokens[i]);

                    if (currentNode == null){
                        return true;
                    }

                    if (currentNode.isArbitrary()) {
                        return true;
                    }
            }
        }

        return false;
    }

    /**
     * Determines whether an array of tokens contains special units.
     * @param tokens - the units to be verified
     * @return Status of a UCUM expression containing special units
     */
    //TODO Remove upon implementing Special Unit conversions
    private boolean containsSpecialUnits(String[] tokens){

        Node currentNode;
        for (int i = 0; i < tokens.length; i++) {
            switch (tokens[i]){
                case ".":
                case "/":
                case "(":
                case ")":
                    continue;
                default:
                    currentNode = Node.generateNode(tokens[i]);

                    if (currentNode == null){
                        return true;
                    }

                    if (currentNode.isSpecial()) {
                        return true;
                    }
            }
        }

        return false;
    }

    /*
            These functions involve conversions
     */

    /**
     * Determines whether the given inputs are commensurable according to UCUM.
     * @param source - the source unit
     * @param target - the target unit
     * @return Status of the commensurability of two units
     */
    public boolean isCommensurable(String source, String target){
        TraversalResult trvResultSource = TraversalResult.generateTraversalResult(ucumParser.generateRoot(source));
        TraversalResult trvResultTarget = TraversalResult.generateTraversalResult(ucumParser.generateRoot(target));
        return trvResultSource.generateCanonVectorAsBaseUnit().equals(trvResultTarget.generateCanonVectorAsBaseUnit());
    }

    /**
     * Multiplies two UCUM units and their respective quantities.
     * @param source - the source unit
     * @param sourceQuantity - the source unit's quantity
     * @param target - the target unit
     * @param targetQuantity - the target unit's quantity
     * @return The multiplication's result in its canonized form
     */
    public String multiply(String source, double sourceQuantity, String target, double targetQuantity){
        TraversalResult multiplication = TraversalResult.generateTraversalResult(ucumParser.generateRoot(
                "(" + source + ").(" + target + ")"));
        multiplication.multiplyValue(sourceQuantity * targetQuantity);

        return multiplication.generateCanonVectorAsBaseUnit();
    }

    /**
     * Divides two UCUM units.
     * @param source - the source unit
     * @param sourceQuantity - the source unit's quantity
     * @param target - the target unit
     * @param targetQuantity - the target unit's quantity
     * @return The division's result in its canonized form
     */
    public String divide(String source, double sourceQuantity, String target, double targetQuantity){
        TraversalResult division = TraversalResult.generateTraversalResult(ucumParser.generateRoot(
                "(" + source + ")/(" + target + ")"));
        division.multiplyValue(sourceQuantity / targetQuantity);

        return division.generateCanonVectorAsBaseUnit();
    }

    /**
     * Converts the given source UCUM unit and source quantity into the target UCUM unit should they be commensurable.
     * Setting sourceQuantity to 1 and raising the result to the power of -1 yields the conversion factor going from
     * source to target.
     * @param source - the source unit
     * @param target - the target unit
     * @param quantity - the source unit's quantity
     * @return Quantity of the source unit expressed as the target unit
     */
    public double convert(String source, String target, double quantity){
        TraversalResult trvResultSource = TraversalResult.generateTraversalResult(ucumParser.generateRoot(source));
        TraversalResult trvResultTarget = TraversalResult.generateTraversalResult(ucumParser.generateRoot(target));
        return trvResultSource.getValue()/trvResultTarget.getValue() * quantity;
    }

    /**
     * Converts a given positive quantity into a format that is valid within UCUM (e.g., 1.5 -> 15.10^-1)
     * @param quantity - the quantity to be converted
     * @return Valid UCUM representation of a positive numeric input especially for use with floating point values
     */
    public String numberToUcumExpression(double quantity){

        int scientificExponent;
        String quantityAsString = String.valueOf(quantity);

        if (quantityAsString.contains("E")){
            String[] scientificNotation = quantityAsString.split("(e|E)");
            quantityAsString = scientificNotation[0];
            scientificExponent = Integer.valueOf(scientificNotation[1]);
            String[] decimalSeparator = quantityAsString.split("[.]");

            if (decimalSeparator[1].matches("0+")){
                return decimalSeparator[0] + ".10^" + scientificExponent;
            }

            int ucumExponent = -decimalSeparator[1].length() + scientificExponent;
            if (ucumExponent == 0){
                return quantityAsString.replace(".","");
            } else {
                return quantityAsString.replace(".", "") + ".10^" + ucumExponent;
            }
        } else {
            String[] decimalSeparator = quantityAsString.split("[.]");
            if (decimalSeparator[1].matches("0+")) {
                return decimalSeparator[0];
            } else if (decimalSeparator[0].equals("0")) {
                return decimalSeparator[1] + ".10^-" + (decimalSeparator[1].length());
            } else {
                return decimalSeparator[0] + decimalSeparator[1] + ".10^-" + (decimalSeparator[1].length());
            }
        }
    }

    /*
            These functions involve the canonization process itself
     */

    /**
     * Generates the canonized form of a UCUM unit as follows: [base unit term], [value].
     * Arbitrary units may not partake in the act of canonization.
     * @param source - the source unit
     * @return The pair of base unit composition and its magnitude ergo the canonized form
     */
    public String generateCanonizedForm(String source){
        TraversalResult trvResult = TraversalResult.generateTraversalResult(ucumParser.generateRoot(source));
        return trvResult.generateCanonVectorAsBaseUnit() + ", " + trvResult.getValue();
    }

    /**
     * Generates the canon vector of a UCUM unit in accordance with the UCUM essence document
     * as follows: [m,s,g,rad,K,C,cd], where each index represents a tally of its respective base unit.
     * Arbitrary units may not partake in the act of canonization.
     * @param source - the source unit
     * @return The base unit composition of the source unit
     */
    public int[] generateCanonVector(String source){
        TraversalResult trvResult = TraversalResult.generateTraversalResult(ucumParser.generateRoot(source));
        return trvResult.getCanonVector();
    }

    /**
     * Generates the display name for a given UCUM unit.
     * @param source - the source unit
     * @return Display name for a unit
     */
    public String generateDisplayName(String source){
        String[] tokens = generateTokens(source);
        String displayName = "";

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("(")){
                displayName += "(";
            } else if (tokens[i].equals(")")){
                displayName += ")";
            } else {
                Node currentNode = Node.generateNode(tokens[i]);
                displayName += currentNode.generateDisplayName();
            }
        }

        return displayName;
    }

}
