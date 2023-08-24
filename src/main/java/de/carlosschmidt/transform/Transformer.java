package de.carlosschmidt.transform;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Transformer interface. Every ElementTransformer must implement this/these
 * methods
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public interface Transformer {

    /**
     * Transform a given JsonNode object into another JsonNode object
     * 
     * @param toTransform
     *            jsonNode to be transformed
     * @return Transformed jsonNode
     * @throws IOException
     * @throws IllegalArgumentException
     */
    JsonNode transform(JsonNode toTransform) throws IllegalArgumentException, IOException;

    /**
     * Returns whether this transformer can handle this input.
     * 
     * @param input
     *            input to handle
     * @return Whether this transformer can handle this input
     */
    boolean canHandle(JsonNode input);
}
