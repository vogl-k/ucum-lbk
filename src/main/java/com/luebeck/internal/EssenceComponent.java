package com.luebeck.internal;

class EssenceComponent {

    private static final String SCIENTIFIC_NOTATION_REGEX = "(e|E)";

    /**
     * Certain values in the Ucum Essence document are provided in scientific notation.
     * This function is used to parse them if needed.
     * @param value
     * @return double created from a string in scientific notation
     */
    double valueAsDouble(String value){
        if(value.contains(SCIENTIFIC_NOTATION_REGEX)){
            String[] baseAndExponent = value.split(SCIENTIFIC_NOTATION_REGEX);
            return Double.parseDouble(baseAndExponent[0]) * Math.pow(10, Double.parseDouble(baseAndExponent[1]));
        } else {
            return Double.parseDouble(value);
        }
    }
}
