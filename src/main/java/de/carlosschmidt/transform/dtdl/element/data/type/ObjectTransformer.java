package de.carlosschmidt.transform.dtdl.element.data.type;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * An object transformer.
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class ObjectTransformer extends DTDLElementTransformer {

    private static final String TYPE_NAME = DTDLElementNames.OBJECT.name();

    @Override
    public JsonNode transform(JsonNode inputJsonNode) throws IOException {
        ArrayNode properties = objectMapper.createArrayNode();
        for (JsonNode field : inputJsonNode.get("fields")) {
            properties.add(transformOther(field));
        }
        return properties;
    }

    @Override
    public boolean canHandle(JsonNode input) {
        return TYPE_NAME.equalsIgnoreCase(getType(input).name());
    }
}
