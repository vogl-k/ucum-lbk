package com.luebeck.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

class UcumEssence {

    static private UcumUnit[] ucumUnits = new UcumUnit[310];
    static private UcumPrefix[] ucumPrefixes = new UcumPrefix[24];

    static private HashMap codeCaseSensMap = new HashMap();
    static private HashMap codeCapitalMap = new HashMap();
    static private HashMap prefixCaseSensMap = new HashMap();
    static private HashMap prefixCapitalMap = new HashMap();

    /**
     * Constructor for what is essentially the UCUM Essence document turned into UcumUnit and UcumPrefix objects.
     */
    UcumEssence() {
        try {
            initializePrefixDatabase();
        } catch (IOException e) {
            return;
        }

        try {
            initializeUnitDatabase();
        } catch (IOException e) {
            return;
        }

        initializeMaps();
    }

    /**
     * Reads the prefix csv file and turns its contents into UcumPrefix objects.
     * @throws IOException
     */
    void initializePrefixDatabase() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("ucum_essence_prefix.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        int i = 0;
        String csvLine;
        String[] csvLineSplit;

        while ((csvLine = br.readLine()) != null){

            csvLineSplit = splitCSV(csvLine);

            ucumPrefixes[i] = new UcumPrefix(
                    csvLineSplit[0],
                    csvLineSplit[1],
                    csvLineSplit[2],
                    csvLineSplit[3],
                    csvLineSplit[4],
                    csvLineSplit[5]);
            i++;
        }
        is.close();
        br.close();
    }

    /**
     * Reads the unit csv files and turns their contents into UcumUnit objects. Base units and derived units are
     * handled independently as they are structured in a different way.
     * @throws IOException
     */
    void initializeUnitDatabase() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("ucum_essence_baseunit.csv");
        BufferedReader br_baseUnit = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        int i = 0;
        String csvLine;
        String[] csvLineSplit;

        while ((csvLine = br_baseUnit.readLine()) != null){

            csvLineSplit = splitCSV(csvLine);

            ucumUnits[i] = new UcumUnit(
                    csvLineSplit[0],
                    csvLineSplit[1],
                    csvLineSplit[2],
                    csvLineSplit[3],
                    csvLineSplit[4],
                    csvLineSplit[5]);
            i++;
        }
        br_baseUnit.close();

        is = getClass().getClassLoader().getResourceAsStream("ucum_essence_derived.csv");
        BufferedReader br_derivedUnit = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        while ((csvLine = br_derivedUnit.readLine()) != null){

            csvLineSplit = splitCSV(csvLine);

            ucumUnits[i] = new UcumUnit(
                    csvLineSplit[0],
                    csvLineSplit[1],
                    csvLineSplit[2],
                    csvLineSplit[3],
                    csvLineSplit[4],
                    csvLineSplit[5],
                    csvLineSplit[6],
                    csvLineSplit[7],
                    csvLineSplit[8],
                    csvLineSplit[9],
                    csvLineSplit[10],
                    csvLineSplit[11],
                    csvLineSplit[12],
                    csvLineSplit[13]);
            i++;
        }
        is.close();
        br_derivedUnit.close();
    }

    /**
     * Initializes the Prefix und Unit HashMaps used for look-ups.
     */
    void initializeMaps(){
        for (int i = 0; i < ucumPrefixes.length; i++) {
            prefixCaseSensMap.put(ucumPrefixes[i].getCodeCaseSens(), i);
            prefixCapitalMap.put(ucumPrefixes[i].getCodeCapital(), i);
        }

        for (int i = 0; i < ucumUnits.length; i++) {
            codeCaseSensMap.put(ucumUnits[i].getCodeCaseSens(), i);
            codeCapitalMap.put(ucumUnits[i].getCodeCapital(), i);
        }
    }

    /**
     * Splits the parameters in the csv files derived from the UCUM Essence document
     * @param csvLine - A line from the csv file that is to be split up
     * @return String Array containing the csv Essence information delimited by semicolons
     */
    private String[] splitCSV(String csvLine){
        csvLine = csvLine.replaceAll(";;", ";EMPTY_SLOT;");
        if (csvLine.endsWith(";")){
            csvLine = csvLine + "EMPTY_SLOT";
        }
        return csvLine.split(";");
    }

    /**
     * Looks up a unit in the UCUM Essence document and also serves as an existence verification of a given unit string.
     * Always looks for the case-sensitive unit first and can only be used with pure units not containing additional
     * information such as exponents.
     * @param input - The string that gets looked up in the UCUM Essence document
     * @return The UCUM unit pertaining to the input
     */
    UcumUnit containsUnit(String input){
        if(codeCaseSensMap.containsKey(input)){
            return ucumUnits[(int) codeCaseSensMap.get(input)];
        } else if (codeCapitalMap.containsKey(input)) {
            return ucumUnits[(int) codeCapitalMap.get(input)];
        } else {
            return null;
        }
    }

    /**
     * Looks up the input in the UCUM Essence document. It is used to specifically
     * look for case-sensitive units.
     * @param input - The string that gets looked up in the case-sensitive part of the UCUM Essence document
     * @return The UCUM unit pertaining the case-sensitive input
     */
    UcumUnit lookupCaseSensUnit(String input){
        if(codeCaseSensMap.containsKey(input)) {
            return ucumUnits[(int) codeCaseSensMap.get(input)];
        } else {
            return null;
        }
    }

    /**
     * Looks up the input in the UCUM Essence document. It is used to specifically
     * look for capital units.
     * @param input - The string that gets looked up in the capital part of the UCUM Essence document
     * @return The UCUM unit pertaining the capital input
     */
    UcumUnit lookupCapitalUnit(String input){
        if(codeCapitalMap.containsKey(input)) {
            return ucumUnits[(int) codeCapitalMap.get(input)];
        } else {
            return null;
        }
    }

    /**
     * Looks up the input in the UCUM Essence document. It is used to specifically
     * look for case-sensitive prefixes.
     * @param input - The string that gets looked up in the case-sensitive prefix part of the UCUM Essence document
     * @return The case-sensitive prefix
     */
    UcumPrefix lookupCaseSensPrefix(String input){
        if(prefixCaseSensMap.containsKey(input)) {
            return ucumPrefixes[(int) prefixCaseSensMap.get(input)];
        } else {
            return null;
        }
    }

    /**
     * Looks up the input in the UCUM Essence document. It is used to specifically
     * look for capital prefixes.
     * @param input - The string that gets looked up in the capital prefix part of the UCUM Essence document
     * @return The capital prefix
     */
    UcumPrefix lookupCapitalPrefix(String input){
        if(prefixCapitalMap.containsKey(input)) {
            return ucumPrefixes[(int) prefixCapitalMap.get(input)];
        } else {
            return null;
        }
    }

}
