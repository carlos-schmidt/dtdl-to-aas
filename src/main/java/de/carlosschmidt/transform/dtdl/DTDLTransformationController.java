package de.carlosschmidt.transform.dtdl;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.carlosschmidt.transform.TransformationController;
import de.carlosschmidt.transform.dtdl.element.CommandTransformer;
import de.carlosschmidt.transform.dtdl.element.ComponentTransformer;
import de.carlosschmidt.transform.dtdl.element.InterfaceTransformer;
import de.carlosschmidt.transform.dtdl.element.RelationshipTransformer;
import de.carlosschmidt.transform.dtdl.element.data.DataElementTransformer;

/**
 * Handles transformation of DTDL elements
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class DTDLTransformationController implements TransformationController {

    private List<DTDLElementTransformer> transformers;

    /**
     * Initialize controller and its known transformers
     */
    public DTDLTransformationController() {
        transformers = List.of(new InterfaceTransformer(), new CommandTransformer(), new ComponentTransformer(),
                new RelationshipTransformer(), new DataElementTransformer());
    }

    @Override
    public boolean canHandle(String input) throws JsonProcessingException {
        JsonNode inputJson = new ObjectMapper().readTree(input);
        return transformers.stream().filter(transformer -> transformer.canHandle(inputJson)).count() == 1;
    }

    @Override
    public JsonNode transform(String toBeTransformed) throws IllegalArgumentException, IOException {
        JsonNode inputJson = new ObjectMapper().readTree(toBeTransformed);
        return transformers.stream().filter(transformer -> transformer.canHandle(inputJson)).findFirst()
                .orElseThrow(IllegalArgumentException::new).transform(inputJson);
    }
}
