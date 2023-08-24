package de.carlosschmidt.transform.dtdl.element.data.type;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * An enum transformer
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class EnumTransformer extends DTDLElementTransformer {

    private static final String TYPE_NAME = DTDLElementNames.ENUM.name();

    private static final String DTDL_SCHEMA_KEYWORD = "schema";

    @Override
    public JsonNode transform(JsonNode inputJsonNode) throws IOException {
        ArrayNode properties = objectMapper.createArrayNode();

        for (JsonNode field : ((ArrayNode) inputJsonNode.get("enumValues"))) {
            // Enum value schema is defined once in enum, not in enum values
            ((ObjectNode) field).set(DTDL_SCHEMA_KEYWORD, inputJsonNode.get("valueSchema"));
            // Enum Values can only be string or int -> primitive
            properties.add(transformGenericElement(field, loadConfiguration("property_primitive")));
        }
        return properties;
    }

    @Override
    public boolean canHandle(JsonNode input) {
        return TYPE_NAME.equalsIgnoreCase(getType(input).name());
    }

}
