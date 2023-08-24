package de.carlosschmidt.transform;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Element Transformer. Inheriting classes can apply a mapping given a mapping
 * configuration as data sink and a jsonNode as a data source.
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public abstract class ElementTransformer implements Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementTransformer.class);

    /** Object Mapper to transform between JsonNode/JsonArray and String */
    protected final ObjectMapper objectMapper;

    /**
     * Initialize an ElementTransformer
     */
    protected ElementTransformer() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Writes data from input into mappingConfig using JsonPath expressions in
     * mappingConfig
     * 
     * @param input
     *            Data source
     * @param mappingConfig
     *            Data sink
     * @return filled mapping config
     */
    protected JsonNode applyMapping(final JsonNode input, final JsonNode mappingConfig) {
        var returnValue = mappingConfig;
        returnValue.fields().forEachRemaining(field -> {
            var fieldValue = field.getValue();
            if (Objects.nonNull(fieldValue)) {
                LOGGER.debug(field.toString());
                if (fieldValue.isNull()) {
                    return;
                } else if (fieldValue.isValueNode()) {
                    JsonNode parsedJsonPath = parseMultiJsonPath(fieldValue.asText(), input);
                    if (parsedJsonPath.size() > 0) {
                        parsedJsonPath.forEach(field::setValue);
                        return;
                    }
                    field.setValue(objectMapper.nullNode());
                } else if (!fieldValue.isEmpty() && fieldValue.isObject()) {
                    field.setValue(applyMapping(input, field.getValue()));
                } else if (!fieldValue.isEmpty() && fieldValue.isArray()) {
                    fieldValue.forEach(value -> field.setValue(applyMapping(input, value)));
                    field.setValue(fieldValue);
                }
            }
        });
        return returnValue;
    }

    /**
     * Parse a jsonPath expression (can be multiple expressions and strings divided
     * by |)
     * 
     * @param path
     *            JsonPath expression(s)
     * @param toParse
     *            data source
     * @return data
     */
    protected JsonNode parseMultiJsonPath(@NonNull final String path, @NonNull final JsonNode toParse) {
        ArrayNode replacements = objectMapper.createArrayNode();
        for (String subPath : path.split(Pattern.quote("|"))) {
            if (subPath.startsWith("$")) {
                var resolved = resolveJsonPath(subPath, toParse);

                if (Objects.nonNull(resolved)) {
                    resolved.forEach(replacements::add);
                }
            } else { // String literals among paths are valid
                replacements.add(subPath);
            }
        }

        return replacements;
    }

    /**
     * Parse a jsonPath expression
     * 
     * @param jsonPath
     *            JsonPath expression
     * @param toResolve
     *            data source
     * @return data
     */
    protected ArrayNode resolveJsonPath(@NonNull final String jsonPath, @NonNull final JsonNode toResolve) {

        var resolvedArrayNode = objectMapper.createArrayNode();
        Configuration conf = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();
        try {
            ArrayNode resolved = JsonPath.using(conf).parse(toResolve.toString()).read(jsonPath);
            resolved.forEach(resolvedArrayNode::add);

            return resolvedArrayNode;
        } catch (PathNotFoundException pathNotFoundException) {
            LOGGER.warn("Exception thrown by JsonPath: {}\nJsonNode: {}\nJsonPath: {}",
                    pathNotFoundException.getMessage(), toResolve, jsonPath);
            return null;
        }
    }

    /**
     * Read file by name
     * 
     * @param configMappingName
     *            file name contains this param
     * @return String of configMapping file contents
     * @throws IOException
     *             If an IOException occurs on reading the file
     */
    protected JsonNode loadConfiguration(@NonNull final String configMappingName) throws IOException {
        return objectMapper.readTree(Path.of(new File("").getAbsolutePath(),
                format("/src/main/resources/mapping-configurations/mapping_%s.json",
                        configMappingName.toLowerCase()))
                .toFile());
    }

}
