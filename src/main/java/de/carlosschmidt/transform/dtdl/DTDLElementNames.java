package de.carlosschmidt.transform.dtdl;

/**
 * @author Carlos Schmidt
 */
public enum DTDLElementNames {
    /**
     * DTDL Interface
     */
    INTERFACE("Interface"),
    /**
     * DTDL Property
     */
    PROPERTY("Property"),
    /**
     * DTDL Telemetry
     */
    TELEMETRY("Telemetry"),
    /**
     * DTDL Command
     */
    COMMAND("Command"),
    /**
     * DTDL Relationship
     */
    RELATIONSHIP("Relationship"),
    /**
     * DTDL Component
     */
    COMPONENT("Component"),
    /**
     * DTDL CommandPayload
     */
    COMMANDPAYLOAD("CommandPayload"),
    /**
     * DTDL Object
     */
    OBJECT("Object"),
    /**
     * DTDL Enum
     */
    ENUM("Enum");

    private String name;

    private DTDLElementNames(String s) {
        name = s;
    }

    /**
     * Returns enum element for given arg string
     * 
     * @param arg0
     *            enum element's name
     * @return Enum element for given argument
     * @throws IllegalArgumentException
     *             in case no such value exists
     */
    public static DTDLElementNames getValue(String arg0) throws IllegalArgumentException {
        return valueOf(arg0.toUpperCase());
    }

    /**
     * Checks if enum contains this value
     * 
     * @param test
     *            value to be checked
     * @return True if value is in enum
     */
    public static boolean contains(String test) {
        for (DTDLElementNames dtdlElement : DTDLElementNames.values()) {
            if (dtdlElement.name.equalsIgnoreCase(test)) {
                return true;
            }
        }

        return false;
    }
}
