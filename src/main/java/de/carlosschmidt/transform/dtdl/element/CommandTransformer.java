package de.carlosschmidt.transform.dtdl.element;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * A command transformer
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class CommandTransformer extends DTDLElementTransformer {

    private static final String TYPE_NAME = DTDLElementNames.COMMAND.name();

    private static final String AAS_PROPERTY_VALUE_KEYWORD = "value";

    /**
     * Initializes a command transformer
     */
    public CommandTransformer() {
        super(TYPE_NAME);
    }

    @Override
    public JsonNode transform(JsonNode toTransform) throws IllegalArgumentException, IOException {
        var operation = super.transform(toTransform);

        var request = transformOther(toTransform.get("request"));
        var response = transformOther(toTransform.get("response"));

        ((ObjectNode) operation.get("inputVariable").get(0)).set(AAS_PROPERTY_VALUE_KEYWORD, request);
        ((ObjectNode) operation.get("outputVariable").get(0)).set(AAS_PROPERTY_VALUE_KEYWORD, response);

        return operation;
    }

    @Override
    public boolean canHandle(JsonNode input) {
        return TYPE_NAME.equalsIgnoreCase(getType(input).name());
    }
}
