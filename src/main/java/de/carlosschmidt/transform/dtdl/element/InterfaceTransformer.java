package de.carlosschmidt.transform.dtdl.element;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * An interface transformer to transform into an AAS Submodel.
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class InterfaceTransformer extends DTDLElementTransformer {

    private static final String TYPE_NAME = DTDLElementNames.INTERFACE.name();

    private static final String AAS_PROPERTY_VALUE_KEYWORD = "value";

    /**
     * Initialize an interface transformer.
     */
    public InterfaceTransformer() {
        super(TYPE_NAME);
    }

    @Override
    public JsonNode transform(JsonNode toTransform) throws IllegalArgumentException, IOException {
        var submodel = super.transform(toTransform);

        // Transform "content" elements
        ArrayNode submodelElements = objectMapper.createArrayNode();
        for (JsonNode child : toTransform.get("contents")) {
            submodelElements.add(transformOther(child));
        }

        // Finally, add complex fields to properties with schema interface definitions
        for (JsonNode submodelElement : submodelElements) {
            var valueField = submodelElement.get(AAS_PROPERTY_VALUE_KEYWORD);
            if (Objects.nonNull(valueField) && valueField.isTextual() && valueField.asText().startsWith("dtmi")) {
                ((ObjectNode) submodelElement).set(AAS_PROPERTY_VALUE_KEYWORD,
                        transformOther(parseMultiJsonPath(
                                format("$.[?(@.['@id'] == '%s')]",
                                        submodelElement.get(AAS_PROPERTY_VALUE_KEYWORD).asText()),
                                toTransform.get("schemas")).get(0)));
            }
        }

        ((ObjectNode) submodel).set("submodelElements", submodelElements);

        return submodel;
    }

    @Override
    public boolean canHandle(JsonNode input) {
        return TYPE_NAME.equalsIgnoreCase(getType(input).name());
    }
}
