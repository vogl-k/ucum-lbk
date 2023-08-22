package com.luebeck.internal;

class UcumPrefix extends EssenceComponent {

    private final String codeCaseSens;
    private final String codeCapital;
    private final String name;
    private final String printSymbol;
    private final double value;//IT Units
    private final int sup;

    /**
     * Constructor for a UCUM Prefix
     * @param codeCaseSens - Case-sensitive symbol
     * @param codeCapital - Capital symbol
     * @param name - prefix name
     * @param printSymbol - prefix print symbol
     * @param value - prefix value
     * @param sup - metric prefix exponent
     */
    UcumPrefix(String codeCaseSens,
               String codeCapital,
               String name,
               String printSymbol,
               String value,
               String sup) {
        this.codeCaseSens = codeCaseSens;
        this.codeCapital = codeCapital;
        this.name = name;
        this.printSymbol = printSymbol;
        this.value = valueAsDouble(value);
        this.sup = printSymbol.equals(null) ? null :  Integer.parseInt(sup);
    }

    /**
     * Returns the power of this prefix.
     * @return Prefix power
     */
    int getSup() {
        return sup;
    }

    /**
     * Returns the case-sensitive code for this prefix.
     * @return Case Sensitive unit symbol
     */
    String getCodeCaseSens() {
        return codeCaseSens;
    }

    /**
     * Returns the capital code for this prefix.
     * @return Capital unit symbol
     */
    String getCodeCapital() {
        return codeCapital;
    }

    /**
     * Returns the name of this prefix.
     * @return Prefix name
     */
    String getName(){
        return name;
    }

}
