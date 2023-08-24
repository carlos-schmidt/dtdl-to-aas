package de.carlosschmidt.dtdl;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import de.carlosschmidt.aas.AASModelValidator;
import de.carlosschmidt.transform.dtdl.DTDLTransformationController;

/**
 * Unit test for DTDLElementTrasformer.
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
class DTDLElementTransformerTest {

    private void testTransformElement(String element) throws IOException {
        var bumblebeeController = new DTDLTransformationController();

        var input = String.join("", Files.readAllLines(Path.of("./src/test/resources/source/" + element + ".json")));
        var transformed = bumblebeeController.canHandle(input) ? bumblebeeController.transform(input).toPrettyString()
                : null;
        assertNotEquals(null, transformed, "Could not transform");
        assertTrue(new AASModelValidator().validate(transformed));

        var transformedExpectation = String.join("",
                Files.readAllLines(Path.of("./src/test/resources/transformed/" + element + ".json")));
        assertEquals(removeFormatting(transformedExpectation), removeFormatting(transformed));
    }

    /**
     * testTransform
     */
    @Test
    void testTransform() throws IOException {
        testTransformElement("command");
        testTransformElement("component");
        testTransformElement("object_external");
        testTransformElement("enum_internal");
        testTransformElement("command_primitive_dateTime");
        testTransformElement("My3DPrinter");
    }

    /**
     * transformTimeSpeedTest
     */
    @Test
    void transformTimeSpeedTest() throws IOException {
        var bumblebeeController = new DTDLTransformationController();

        long measurements = 200;
        String toTransform = String.join("",
                Files.readAllLines(Path.of("./src/test/resources/source/My3dPrinter.json")));
        double avgTime;
        double totalTime = 0;
        long startTime;
        for (int i = 0; i < measurements; i++) {
            startTime = currentTimeMillis();
            bumblebeeController.transform(toTransform);
            totalTime += (currentTimeMillis() - startTime);
        }

        avgTime = totalTime / measurements;
        // transforming cannot consume more than 100 milliseconds
        LoggerFactory.getLogger(DTDLElementTransformerTest.class).info(String.valueOf(avgTime));
        assertTrue(avgTime <= 100);
    }

    private String removeFormatting(String formatted) {
        return formatted.replaceAll(" ", "").replaceAll("\\\r\\\n", "").replaceAll("\\\n", "");
    }
}
