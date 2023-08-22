package com.luebeck.external;

import com.luebeck.internal.UcumFunction;

public class UcumLBKService {

    private static final UcumFunction e = new UcumFunction();

    /**
     * Generates the UCUM service that is used to access the given functions of the library.
     */
    public UcumLBKService(){

    }

    /**
     * Determines whether the given input is a valid UCUM expression or not.
     * @param source - the source unit
     * @return Validity of the input according to the UCUM syntax
     */
    public boolean isValid(String source){
        return e.isValid(source);
    }

    /**
     * Determines whether the given inputs are commensurable according to UCUM.
     * @param source - the source unit
     * @param target - the target unit
     * @return Status of the commensurability of two units
     */
    public boolean isCommensurable(String source, String target){
        if (!e.eligibleForOperations(source) || !e.eligibleForOperations(target)){
            return false;
        } else {
            return e.isCommensurable(source, target);
        }
    }

    /**
     * Converts the given source UCUM unit and source quantity into the target UCUM unit should they be commensurable.
     * Setting sourceQuantity to 1 and raising the result to the power of -1 yields the conversion factor going from
     * source to target.
     * @param source - the source unit
     * @param target - the target unit
     * @param sourceQuantity - the source unit's quantity
     * @return Quantity of the source unit expressed as the target unit
     */
    public Double convert(String source, String target, double sourceQuantity){
        if (!e.eligibleForOperations(source) || !e.eligibleForOperations(target)){
            return null;
        } else {
            return e.convert(source, target, sourceQuantity);
        }
    }

    /**
     * Generates the canon vector of a UCUM unit in accordance with the UCUM essence document
     * as follows: [m,s,g,rad,K,C,cd], where each index represents a tally of its respective base unit.
     * Arbitrary units may not partake in the act of canonization.
     * @param source - the source unit
     * @return The base unit composition of the source unit
     */
    public int[] generateCanonVector(String source){
        if (!e.eligibleForCanonization(source)){
            return null;
        } else {
            return e.generateCanonVector(source);
        }
    }

    /**
     * Generates the canonized form of a UCUM unit as follows: [base unit term], [value].
     * Arbitrary units may not partake in the act of canonization.
     * @param source - the source unit
     * @return The pair of base unit composition and its magnitude ergo the canonized form
     */
    public String generateCanonizedForm(String source){
        if (!e.eligibleForCanonization(source)){
            return null;
        } else {
            return e.generateCanonizedForm(source);
        }
    }

    /**
     * Multiplies two UCUM units and their respective quantities.
     * @param source - the source unit
     * @param sourceQuantity - the source unit's quantity
     * @param target - the target unit
     * @param targetQuantity - the target unit's quantity
     * @return The multiplication's result in its canonized form
     */
    public String multiplyUnits(String source, double sourceQuantity, String target, double targetQuantity){
        if (!e.eligibleForOperations(source) || !e.eligibleForOperations(target)){
            return null;
        } else {
            return e.multiply(source, sourceQuantity, target, targetQuantity);
        }
    }

    /**
     * Divides two UCUM units and their respective quantities.
     * @param source - the source unit
     * @param sourceQuantity - the source unit's quantity
     * @param target - the target unit
     * @param targetQuantity - the target unit's quantity
     * @return The division's result in its canonized form
     */
    public String divideUnits(String source, double sourceQuantity, String target, double targetQuantity){
        if (!e.eligibleForCanonization(source) || !e.eligibleForCanonization(target)){
            return null;
        } else {
            return e.divide(source, sourceQuantity, target, targetQuantity);
        }
    }

    /**
     * Generates the display name for a given UCUM unit.
     * @param source - the source unit
     * @return Display name for a unit
     */
    public String generateDisplayName(String source){
        if (!e.isValid(source)){
            return null;
        } else {
            return e.generateDisplayName(source);
        }
    }

    /**
     * Converts a given positive quantity into a format that is valid within UCUM (e.g., 1.5 -> 15.10^-1)
     * @param quantity - the quantity to be converted
     * @return Valid UCUM representation of a positive numeric input especially for use with floating point values
     */
    public String convertNumberToUcum(double quantity){
        if (quantity < 0){
            return null;
        } else {
            return e.numberToUcumExpression(quantity);
        }
    }

}

