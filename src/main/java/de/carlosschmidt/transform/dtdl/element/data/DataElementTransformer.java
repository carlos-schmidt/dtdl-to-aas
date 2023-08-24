package de.carlosschmidt.transform.dtdl.element.data;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.carlosschmidt.transform.dtdl.DTDLElementNames;
import de.carlosschmidt.transform.dtdl.DTDLElementTransformer;

/**
 * A data element transformer
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class DataElementTransformer extends DTDLElementTransformer {

    private static final String AAS_PROPERTY_VALUE_KEYWORD = "value";
    private static final String DTDL_SCHEMA_KEYWORD = "schema";
    private static final String DTDL_TYPE_KEYWORD = "@type";
    private static final int SECOND_ELEMENT_IN_ARRAY = 1;

    /**
     * Create a dataelementTransformer with standard data type "primitive"
     */
    public DataElementTransformer() {
        super("property_primitive");
    }

    @Override
    public JsonNode transform(JsonNode toTransform) throws IllegalArgumentException, IOException {
        var schemaNode = toTransform.get(DTDL_SCHEMA_KEYWORD);

        if (Objects.nonNull(toTransform.get(DTDL_TYPE_KEYWORD)) && toTransform.get(DTDL_TYPE_KEYWORD).isArray()) {

            // Semantically annotated element, must be primitive
            var semanticType = toTransform.get(DTDL_TYPE_KEYWORD).get(SECOND_ELEMENT_IN_ARRAY);
            Objects.requireNonNull(semanticType, "Semantic Type not found for json: " + toTransform.toPrettyString());

            return super.transformGenericElement(toTransform, loadConfiguration("property_primitive_semantic"));

        } else if (schemaNode.isValueNode() && !schemaNode.asText().startsWith("dtmi")) {

            // Primitive type, no external schema (dtmi:...)
            return super.transformGenericElement(toTransform, loadConfiguration("property_primitive"));

        } else {
            return transformComplex(toTransform);
        }

    }

    private JsonNode transformComplex(JsonNode toTransform) throws IllegalArgumentException, IOException {

        var collection = super.transformGenericElement(toTransform, loadConfiguration("property_complex_external"));

        if (toTransform.get(DTDL_SCHEMA_KEYWORD).isObject()) {
            // internally defined complex data type
            var collectionValue = transformOther(toTransform.get(DTDL_SCHEMA_KEYWORD));
            ((ObjectNode) collection).set(AAS_PROPERTY_VALUE_KEYWORD, collectionValue);
        }
        // If schema is defined in interface, transformInterface has to import property
        // structure into this complex element

        return collection;
    }

    @Override
    public boolean canHandle(JsonNode input) {
        String type = getType(input).name();
        return DTDLElementNames.PROPERTY.name().equalsIgnoreCase(type)
                || DTDLElementNames.TELEMETRY.name().equalsIgnoreCase(type)
                || DTDLElementNames.COMMANDPAYLOAD.name().equalsIgnoreCase(type);
    }
}
