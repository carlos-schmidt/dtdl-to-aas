package de.carlosschmidt.transform.dtdl;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.carlosschmidt.transform.ElementTransformer;

/**
 * Generic DTDL Element Transformer. Allows to transform any given generic
 * element (no child elements, only metadata)
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public abstract class DTDLElementTransformer extends ElementTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DTDLElementTransformer.class);

    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTIONS = "descriptions";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DTDL_TYPE_KEYWORD = "@type";

    private JsonNode singleLanguageMappingConfig;
    private JsonNode elementMappingConfig;

    private boolean isValid = false;

    /**
     * In case no config for this element is available. (Object, Enum)
     */
    protected DTDLElementTransformer() {
        super();
        try {
            singleLanguageMappingConfig = loadConfiguration("single_language");
            isValid = true;
        } catch (InvalidPathException invalidPathException) {
            LOGGER.error("Mapping file could not be found: {}", invalidPathException.getMessage());
        } catch (IOException ioException) {
            LOGGER.error("Loading of mapping configuration failed: {}", ioException.getMessage());
        }
    }

    /**
     * Initialize an ElementTransformer
     * 
     * @param mappingConfigName
     *            Name of mapping config. Name is given like this:
     *            resources/mapping-configurations/mapping_%YOUR_NAME%.json
     */
    protected DTDLElementTransformer(@NonNull final String mappingConfigName) {
        super();

        // as every DTDL element can contain displayName and description as single and
        // multiLanguage fields, they can be handled here
        try {
            singleLanguageMappingConfig = loadConfiguration("single_language");
            elementMappingConfig = loadConfiguration(mappingConfigName);
            isValid = true;
        } catch (InvalidPathException invalidPathException) {
            LOGGER.error("Mapping file could not be found: {}", invalidPathException.getMessage());
        } catch (IOException ioException) {
            LOGGER.error("Loading of mapping configuration failed: {}", ioException.getMessage());
        }

    }

    /**
     * Transform a generic DTDL element.
     * 
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    public JsonNode transform(JsonNode toTransform) throws IllegalArgumentException, IOException {
        return transformGenericElement(toTransform, elementMappingConfig);
    }

    /**
     * Transform another element (mostly child elements)
     * 
     * @param otherNode
     *            Element to be transformed
     * @return Transformed element
     * @throws IllegalArgumentException
     *             Type of element could not be found
     * @throws IOException
     *             Elements mapping configuration could not be found
     */
    public JsonNode transformOther(JsonNode otherNode) throws IllegalArgumentException, IOException {
        return DTDLElementTransformerFactory.createDTDLElementTransformer(getType(otherNode)).transform(otherNode);
    }

    /**
     * Transform a generic element consisting of only metadata and no further child
     * elements.
     * 
     * @param inputJsonNode
     *            Data source element (DTDL)
     * @param elementMappingConfig
     *            Data sink (AAS)
     * @return elementMappingConfig filled with values of data source
     */
    protected JsonNode transformGenericElement(JsonNode inputJsonNode, JsonNode elementMappingConfig) {
        var genericElement = applyMapping(inputJsonNode, elementMappingConfig);

        genericElement = setSingleMultiLanguageField((ObjectNode) genericElement,
                DISPLAY_NAME, inputJsonNode, DISPLAY_NAME);
        genericElement = setSingleMultiLanguageField((ObjectNode) genericElement,
                DESCRIPTIONS, inputJsonNode, DESCRIPTION);

        return genericElement;
    }

    /**
     * Get a type of a dtdl element by analysing its JSON representation.
     * 
     * @param input
     *            JSON representation of this element
     * @return Type of this element
     * @throws IllegalArgumentException
     *             Type name could not be found in allowed type names
     *             (DTDLElementNames.java)
     */
    protected DTDLElementNames getType(final JsonNode input) throws IllegalArgumentException {
        var types = resolveJsonPath(format("$.['%s']", DTDL_TYPE_KEYWORD), input);

        // Currently, the only element without a @type field is commandPayload.
        // It can nonetheless be of any complex datatype, so we must treat it like a
        // normal DTDL element.
        if (types == null || types.size() == 0) {
            return DTDLElementNames.COMMANDPAYLOAD;
        }

        // unpack array
        var typesValue = types.get(0);
        // in case of semantic type
        if (typesValue.isArray()) {
            for (JsonNode typeDef : typesValue) {
                if (DTDLElementNames.contains(typeDef.asText())) {
                    return DTDLElementNames.getValue(typeDef.asText());
                }
            }
        }
        return DTDLElementNames.getValue(typesValue.asText());
    }

    private JsonNode setSingleMultiLanguageField(ObjectNode whereToSet, final String whatToSet,
            final JsonNode whereToFind, final String whatToFind) {
        return whereToSet.set(whatToSet, transformSingleMultiLanguage(whereToFind.get(whatToFind)));
    }

    private ArrayNode transformSingleMultiLanguage(JsonNode jsonNode) {
        if (Objects.isNull(jsonNode)) {
            return null;
        }

        if (jsonNode.isObject()) {
            return transformMultiLanguageField(jsonNode);
        } else {
            return objectMapper.createArrayNode().add(applyMapping(jsonNode, singleLanguageMappingConfig.deepCopy()));
        }
    }

    private ArrayNode transformMultiLanguageField(final JsonNode input) {
        ArrayNode array = objectMapper.createArrayNode();
        // since multilanguage fields in DTDL consist of "language": "value", a separate
        // mapping has to be executed
        input.fields().forEachRemaining(languageElement -> {
            ObjectNode newNode = objectMapper.createObjectNode();
            newNode.put("language", languageElement.getKey());
            newNode.put("text", languageElement.getValue().asText());
            array.add(newNode);
        });
        return array;
    }

    /**
     * Returns whether the transformer is valid (i.e. config is loaded)
     * 
     * @return whether the transformer is valid
     */
    public boolean isValid() {
        return isValid;
    }
}
