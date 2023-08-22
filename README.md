# UCUM-LBK - A Compact Java Library for UCUM

## What is UCUM-LBK?
UCUM-LBK is a compact Java library for working with the UCUM standard. UCUM-LBK doesn't rely on external dependencies and aims to be more robust in regard to nesting unit terms and various smaller syntax details. In addition to supporting case sensitive UCUM units, this library also enables users to work with capital unit representations. Moreover, the contents of the UCUM-Essence document (originally published as an XML-file) are provided as .csv files that might prove to be useful even outside of the scope of working with the UCUM-LBK library itself.

## Which UCUM version is it based on?
The current version is based on V2.1 of UCUM

## Which functionalities does UCUM-LBK support?
UCUM-LBK implements all basic functions outlined in the UCUM validation document aswell as a few additions:
### isValid(String source)
* Determines whether the given input is a valid UCUM expression or not.
### isCommensurable(String source, String target)
* Determines whether the given inputs are commensurable according to UCUM.
### convert(String source, String target, double sourceQuantity)
* Converts the given source UCUM unit and source quantity into the target UCUM unit should they be commensurable.
* Setting sourceQuantity to 1 and raising the result to the power of -1 yields the conversion factor going from source to target.
### generateCanonVector(String source)
* Generates the canon vector of a UCUM unit in accordance with the UCUM-Essence document as follows:
* In accordance with the UCUM-Essence document the order is [m, s, g, rad, K, C, cd].
### generateCanonizedForm(String source)
* Generates the canonized form of a UCUM unit as [base unit term], [value]
### multiplyUnits(String source, double sourceQuantity, String target, String targetQuantity)
* Multiplies two UCUM units and their respective quantities.
### divideUnits(String source, double sourceQuantity, String target, String targetQuantity)
* Divides two UCUM units and their respective quantities.
### generateDisplayName(String source)
* Generates the display name for a given UCUM unit. Takes annotations into account.
### convertNumberToUcum(double quantity)
* Converts a given positive quantity into a format that is valid within UCUM (e.g., 1.5 -> 15.10^-1)

## A note on case sensitive and capital representations
Every UCUM unit is represented by a case sensitive and a capital code. Although
case sensitive units make up the bulwark of UCUM units used, users may still wish
to work with capital codes for various reasons. In version 2.1 of the UCUM standard
there exists an overlap between the representation of four units which 	introduces 	ambiguities when dealing with those units in particular. It is assumed that case sensitive units are more commonly used which is why this library defaults to treat any of these units as case sensitive representations until proven otherwise. What this
means is that unless an ambigious unit is used with other units that establish its
capital nature it is assumed to be case sensitive.

## Current Limitations
UCUM-LBK does not yet support the transformation of special units such a Celsius and units related to IT
