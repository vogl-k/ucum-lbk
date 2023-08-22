package com.luebeck.internal;

class UcumUnit extends EssenceComponent {

    private final String codeCaseSens;
    private final String codeCapital;
    private final boolean isMetric;
    private final boolean isArbitrary;
    private final boolean isSpecial;
    private final boolean isBaseUnit;
    private final String dim;
    private final String cls;
    private final String[] name;
    private final String printSymbol;
    private final String property;
    private final String unitCaseSens;
    private final String unitCapital;
    private final double value;
    private final String func_name;
    private final String func_unit;

    /**
     * Constructor for a derived UCUM unit.
     * @param codeCaseSens - Case-sensitive unit symbol
     * @param codeCapital - Capital unit symbol
     * @param isMetric - Metric status
     * @param isSpecial - Special status
     * @param isArbitrary - Arbitrary status
     * @param cls - Unit class
     * @param name - Unit name(s)
     * @param printSymbol - Unit print symbol
     * @param property - Unit property
     * @param unitCaseSens - Case-sensitive unit symbol to dissolve to (e.g., L -> m3)
     * @param unitCapital - Capital unit symbol to dissolve to (e.g., L-> M3)
     * @param value - Unit value
     * @param func_name - Function name for special units
     * @param func_unit - Function unit fpr special units
     */
    UcumUnit (String codeCaseSens, String codeCapital, String isMetric, String isSpecial,
              String isArbitrary, String cls, String name, String printSymbol,
              String property, String unitCaseSens, String unitCapital, String value, String func_name,
              String func_unit){
        this.codeCaseSens = codeCaseSens;
        this.codeCapital = codeCapital;
        this.isMetric = Boolean.parseBoolean(isMetric);
        this.isArbitrary = Boolean.parseBoolean(isArbitrary);
        this.isSpecial = Boolean.parseBoolean(isSpecial);
        this.isBaseUnit = false;
        this.dim = null;
        this.cls = cls;
        this.name = name.split(",");
        this.printSymbol = printSymbol;
        this.property = property;
        this.unitCaseSens = unitCaseSens;
        this.unitCapital = unitCapital;
        this.value = valueAsDouble(value);
        this.func_name = func_name.equals("EMPTY_SLOT") ? null : func_name;
        this.func_unit = func_unit.equals("EMPTY_SLOT") ? null : func_unit;
    }

    /**
     * Constructor for a UCUM base unit.
     * @param codeCaseSens - Case-sensitive unit symbol
     * @param codeCapital - Capital unit symbol
     * @param dim - Dimension
     * @param name - Unit name
     * @param printSymbol - Unit print symbol
     * @param property - Unit property
     */
    UcumUnit(String codeCaseSens, String codeCapital, String dim, String name, String printSymbol, String property){
        this.codeCaseSens = codeCaseSens;
        this.codeCapital = codeCapital;
        this.name = name.split(",");
        this.printSymbol = printSymbol;
        this.property = property;
        //Defaults for matching these with derived units
        this.isMetric = true;
        this.isArbitrary = false;
        this.isSpecial = false;
        this.isBaseUnit = true;
        this.dim = dim;
        this.cls = null;
        this.unitCaseSens = null;
        this.unitCapital = null;
        this.value = 1;
        this.func_name = null;
        this.func_unit = null;
    }

    /**
     * Returns the case-sensitive code for this UCUM unit.
     * @return Case-sensitive unit symbol
     */
    String getCodeCaseSens() {
        return codeCaseSens;
    }

    /**
     * Returns the capital unit symbol for this UCUM unit.
     * @return Capital unit symbol
     */
    String getCodeCapital() {
        return codeCapital;
    }

    /**
     * Returns the value of this UCUM unit.
     * @return value
     */
    double getValue() {
        //Special + IT
        return value;
    }

    /**
     * Returns the case-sensitive unit symbol for normal units. Special units
     * use the func_unit entry instead. This function is used specifically when generating
     * nodes.
     * @return Case-sensitive unit symbol or special unit symbol
     */
    String getUnitForNode(){
        if (func_unit == null){
            return unitCaseSens;
        } else {
            return func_unit;
        }
    }

    /**
     * Determines whether this UCUM unit is special.
     * @return Special status of this unit
     */
    public boolean isSpecial() {
        return isSpecial;
    }

    /**
     * Determines whether this UCUM unit is arbitrary.
     * @return Arbitrary stauts of this unit
     */
    public boolean isArbitrary() {
        return isArbitrary;
    }

    /**
     * Determines whether this UCUM unit is metric.
     * @return Metric status of this unit
     */
    public boolean isMetric(){
        return isMetric;
    }

    /**
     * Returns a single name for this UCUM unit. Occasionally entries in the UCUM essence document
     * contain more than one name. Generally all names are stored in a UcumUnit object but
     * when generating a display name only the first entry is used.
     * @return First entry in the name array
     */
    public String getName() {
        return name[0];
    }

}
