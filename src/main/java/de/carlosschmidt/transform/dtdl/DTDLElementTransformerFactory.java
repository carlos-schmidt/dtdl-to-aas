package de.carlosschmidt.transform.dtdl;

import de.carlosschmidt.transform.dtdl.element.CommandTransformer;
import de.carlosschmidt.transform.dtdl.element.ComponentTransformer;
import de.carlosschmidt.transform.dtdl.element.InterfaceTransformer;
import de.carlosschmidt.transform.dtdl.element.RelationshipTransformer;
import de.carlosschmidt.transform.dtdl.element.data.DataElementTransformer;
import de.carlosschmidt.transform.dtdl.element.data.type.EnumTransformer;
import de.carlosschmidt.transform.dtdl.element.data.type.ObjectTransformer;

/**
 * Creates transformers by their type
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public final class DTDLElementTransformerFactory {

    private DTDLElementTransformerFactory() {
    }

    /**
     * Returns a transformer by its type
     * 
     * @param type
     *            DTDL element type
     * @return Transformer
     */
    public static DTDLElementTransformer createDTDLElementTransformer(DTDLElementNames type) {

        switch (type) {
            case INTERFACE:
                return new InterfaceTransformer();
            case PROPERTY:
            case TELEMETRY:
            case COMMANDPAYLOAD:
                return new DataElementTransformer();
            case COMPONENT:
                return new ComponentTransformer();
            case RELATIONSHIP:
                return new RelationshipTransformer();
            case COMMAND:
                return new CommandTransformer();
            case OBJECT:
                return new ObjectTransformer();
            case ENUM:
                return new EnumTransformer();
            default: // do not fail on single element
                return null;
        }

    }
}
