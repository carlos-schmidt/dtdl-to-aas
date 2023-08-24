package de.carlosschmidt.transform;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Handles transformation of elements
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public interface TransformationController {

    /**
     * Returns whether exactly one transformer can handle this type.
     * 
     * @param input
     *            input to be handled
     * @return Whether exactly one transformer can handle this type
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    boolean canHandle(String input) throws JsonProcessingException;

    /**
     * Transform a given input with given type to its respective counterpart on AAS
     * side
     * 
     * @param toBeTransformed
     *            Input element
     * @return Elements respective counterpart on AAS side
     * @throws IllegalArgumentException
     *             No transformer could be found
     * @throws IOException
     */
    JsonNode transform(String toBeTransformed) throws IllegalArgumentException, IOException;
}
