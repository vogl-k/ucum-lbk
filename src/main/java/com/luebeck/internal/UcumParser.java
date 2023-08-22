package com.luebeck.internal;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UcumParser {

    private static final String CONSECUTIVE_EXPONENT = "[+-]{2}";
    private static final String CONSECUTIVE_OPERATOR = "[.\\/]{2}";
    private static final String OMITTED_MULTIPLICATION = "[^.\\/]\\(";
    private static final String EMPTY_PARENTHESES = "\\(\\)";
    private static final String MISSING_OPERAND_DIV = "\\/\\)";
    private static final String MISSING_OPERAND_MUL = "\\(\\.\\)";
    private static final String NESTED_BRACES = "\\{.*\\{.*}.*}";
    private static final String BRACES_EXPONENT = "\\}[-+]*\\d";
    private static final String INTEGER_BEGINS_WITH_ZERO = "[^\\d]0\\d+";
    private static final String ANNOTATION_IN_FRONT = "\\}[^.\\/\\n]";
    private static final String INTEGER_NEGATIVE_EXPONENT = "\\d-";
    private static final String INTEGER_BEGINS_WITH_PLUS_MINUS = "[./]+[-+]+\\d";
    private static final String PARENTHESES_EXPONENT = "\\)[-+]*\\d";

    /**
     * Verifies whether the input violates any syntax rules laid out by UCUM. The used regex patterns
     * are member variables of UcumParser. Annotation contents are exempt from being detected.
     * @param regex - the regex pattern used to check for syntax violations
     * @param input - the input to be verified
     * @return Validity status of the given input
     */
    private boolean containsIllegalPattern(String regex, String input){
        boolean openingBraceFound;
        boolean closingBraceFound;

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);

        //Exempts annotations from the regex check
        while (matcher.find()){
            if (matcher.start()-1 < 0){
                return true;
            } else {
                openingBraceFound = input.charAt(matcher.start()-1) == '{';
            }

            if (matcher.end() == input.length()){
                return true;
            } else {
                closingBraceFound = input.charAt(matcher.end()) == '}';
            }

            if (openingBraceFound != true && closingBraceFound != true){
                return true;
            }
        }

        return false;
    }

    /**
     * Verifies whether the curly braces in the input are balanced.
     * @param input - the input to be verified
     * @return Status of curly brace imbalances
     */
    private boolean passesBracesCheck(String input){
        Stack<Character> braceStack = new Stack<>();
        char currentChar;

        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            switch (currentChar){
                case '{':
                    braceStack.add(currentChar);
                    break;
                case '}':
                    if (braceStack.isEmpty()){
                        return false;
                    } else {
                        braceStack.pop();
                    }
                    break;
                default:
                    break;
            }
        }

        return braceStack.isEmpty();
    }

    /**
     * Verifies whether or not the input passes all necessary syntax checks before being further delegated to
     * the actual parser.
     * @param input - the input to be verified
     * @return Stauts of the input passing all tests successfully
     */
    public boolean passesInitialSyntaxCheck(String input){

        if (input.equals("")){
            return false;
        }

        //ASCII Boundary Check
        if (!(input.codePoints().allMatch(c -> c >= 33 && c <= 126))){
            return false;
        }

        if (input.startsWith(".") || input.endsWith(".")){
            return false;
        }

        if (input.endsWith("/")){
            return false;
        }

        if (input.startsWith("+")){
            return false;
        }

        if (input.startsWith("-")){
            return false;
        }

        if (!passesBracesCheck(input)){
            return false;
        }

        if (containsIllegalPattern(OMITTED_MULTIPLICATION, input)){
            return false;
        }

        if (containsIllegalPattern(ANNOTATION_IN_FRONT, input)){
            return false;
        }

        if (containsIllegalPattern(EMPTY_PARENTHESES, input)){
            return false;
        }

        if (containsIllegalPattern(CONSECUTIVE_EXPONENT, input)){
            return false;
        }

        if (containsIllegalPattern(INTEGER_BEGINS_WITH_ZERO, input)){
            return false;
        }

        if (containsIllegalPattern(CONSECUTIVE_OPERATOR, input)){
            return false;
        }

        if (containsIllegalPattern(MISSING_OPERAND_DIV, input)){
            return false;
        }

        if (containsIllegalPattern(MISSING_OPERAND_MUL, input)){
            return false;
        }

        if (containsIllegalPattern(INTEGER_NEGATIVE_EXPONENT, input)){
            return false;
        }

        if (containsIllegalPattern(INTEGER_BEGINS_WITH_PLUS_MINUS, input)){
            return false;
        }

        if (containsIllegalPattern(PARENTHESES_EXPONENT, input)){
            return false;
        }

        if (containsIllegalPattern(BRACES_EXPONENT, input)){
            return false;
        }

        //Naturally this must not skip the annotations
        if (input.matches(NESTED_BRACES)){
            return false;
        }

        //Used as a parentheses balance check
        try {
            generatePostfixNotation(input);
        } catch (UcumException e) {
            return false;
        }

        return true;
    }

    /**
     * Compartmentalizes an input into tokens. Tokens are parentheses, the operators for multiplication and division
     * as well as all operands.
     * @param input - the input to be split
     * @return Array of tokens generated from the input
     */
    public String[] tokenize(String input){
        ArrayList<String> tokens = new ArrayList();
        StringBuilder strBuilder = new StringBuilder();
        char currentChar;

        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            switch (currentChar){
                case '.':
                case '/':
                case '(':
                case ')':
                    if (strBuilder.length() > 0){
                        tokens.add(strBuilder.toString());
                        strBuilder.setLength(0);
                    }

                    tokens.add(String.valueOf(currentChar));
                    break;
                case '{':
                    while(input.charAt(i) != '}'){
                        //Shouldn't lead to a loop if brace issues are properly handled
                        strBuilder.append(input.charAt(i));
                        i++;
                    }
                    strBuilder.append(input.charAt(i));
                    break;
                default:
                    strBuilder.append(currentChar);
                    break;
            }
        }

        if (strBuilder.length() > 0){
            tokens.add(strBuilder.toString());
            strBuilder.setLength(0);
        }

        return tokens.toArray(new String[0]);
    }

    /**
     * Creates tokens from the input and orders them according to reverse polish notation (postfix) for
     * stack-based processing. Simplified version of the Shunting-Yard algorithm.
     * @param input - the input to be processed
     * @return Tokens sorted according to reverse polish notation
     * @throws UcumException when a mismatch between parentheses is detected
     */
    private ArrayList<String> generatePostfixNotation(String input) throws UcumException {
        Stack<String> inputStack = new Stack<>();
        ArrayList<String> output = new ArrayList<>();

        if (input.startsWith("/")){
            input = "1" + input;
        }

        if (input.contains("(/")){
            input = input.replace("(/", "(1/");
        }

        String[] tokens = tokenize(input);

        for (int i = 0; i < tokens.length; i++) {
            switch (tokens[i]){
                case "/":
                case ".":
                    while(!inputStack.empty() && !inputStack.peek().equals("(")){
                        output.add(inputStack.pop());
                    }
                    inputStack.push(tokens[i]);
                    break;
                case ("("):
                    inputStack.push(tokens[i]);
                    break;
                case(")"):
                    while (!inputStack.empty() && !inputStack.peek().equals("(")){
                        output.add(inputStack.pop());
                    }
                    if (inputStack.isEmpty()){
                        throw new UcumException("Parentheses imbalance in input");
                    } else {
                        inputStack.pop();
                    }
                    break;
                default:
                    output.add(tokens[i]);
                    break;
            }
        }

        while(!inputStack.empty()){
            if (inputStack.peek().equals("(")){
                throw new UcumException("Parentheses imbalance in input");
            }
            output.add(inputStack.pop());
        }
        return output;
    }

    /**
     * Generates a tree root representing the structure of a UCUM expression and cascades dimension calculations down
     * its branches.
     * @param input - a UCUM expression from which to create a tree root
     * @return Root node of the created tree with dimensions cascaded.
     */
    public Node generateRoot(String input){
        Node root = generateTree(input);
        calculateDimensionSubtree(root);
        return root;
    }

    /**
     * Generates a tree representing the input. Calls itself recursively until all tokens have
     * been dissolved to base units.
     * @param input - a UCUM expression from which to create a tree
     * @return Root node of the created tree.
     */
    private Node generateTree(String input) {

        Stack<Node> nodeStack = new Stack<>();
        Stack<Node> dissolveStack = new Stack<>();
        ArrayList<String> tokens = new ArrayList<>();
        String currentToken;
        Node currentNode = null;

        try {
            tokens = generatePostfixNotation(input);
        } catch (UcumException e) {
            return null;
        }

        for (int i = 0; i < tokens.size(); i++) {
            currentToken = tokens.get(i);
            switch (currentToken) {
                case "/":
                    currentNode = Node.generateNode(currentToken);
                    currentNode.setRightChild(nodeStack.pop());
                    currentNode.setLeftChild(nodeStack.pop());
                    //Cascade the inversion
                    currentNode.invertRightChildDimensionExponent();
                    //currentNode.setTrimmedToken(".");
                    nodeStack.push(currentNode);
                    break;
                case ".":
                    currentNode = Node.generateNode(currentToken);
                    currentNode.setLeftChild(nodeStack.pop());
                    currentNode.setRightChild(nodeStack.pop());
                    nodeStack.push(currentNode);
                    break;
                default:
                    currentNode = Node.generateNode(currentToken);
                    nodeStack.push(currentNode);
                    dissolveStack.push(currentNode);
                    break;
            }
        }

        while (!dissolveStack.isEmpty()){
            Node test = dissolveStack.pop();
            if (test.canBeDissolved()){
                test.setRightChild(generateTree(test.getUnitToDissolveTo()));
            }
        }
        return currentNode;
    }

    /**
     * Accounts for the fact that certain units, although they might not themselves exhibit a dimension exponent,
     * may dissolve into units that do. The changes in dimension must therefore be cascaded down the subtree recursively
     * after the initial tree has been created. "ar" is an example of such a unit.
     * @param root - the root of the tree from which dimension calculations are cascaded down
     */
    private void calculateDimensionSubtree(Node root){
        if (root.hasLeftChild()) {
            root.getLeftChild().setDimensionExponent(root.getLeftChild().getDimensionExponent() *
                    root.getDimensionExponent());
            calculateDimensionSubtree(root.getLeftChild());
        }
        if (root.hasRightChild()) {
            root.getRightChild().setDimensionExponent(root.getRightChild().getDimensionExponent()
                    * root.getDimensionExponent());
            calculateDimensionSubtree(root.getRightChild());
        }
    }
}