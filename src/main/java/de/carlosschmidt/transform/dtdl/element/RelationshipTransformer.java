package de.carlosschmidt.transform.dtdl.element;

import com.fasterxml.jackson.databind.JsonNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * A relationship transformer
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class RelationshipTransformer extends DTDLElementTransformer {

    private static final String TYPE_NAME = DTDLElementNames.RELATIONSHIP.name();

    /**
     * Initializes a relationship transformer
     */
    public RelationshipTransformer() {
        super(TYPE_NAME);
    }

    @Override
    public boolean canHandle(JsonNode input) {
        return TYPE_NAME.equalsIgnoreCase(getType(input).name());
    }
}
