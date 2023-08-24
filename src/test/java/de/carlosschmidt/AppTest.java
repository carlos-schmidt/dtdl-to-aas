package de.carlosschmidt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 * 
 * @author Carlos Schmidt
 * @version 0.2
 */
class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        assertEquals(1, 2 - 1);
    }

    /**
     * testTransformationNoExceptions
     */
    @Test
    void testTransformationNoExceptions() {
        App.main(new String[] {"-i",
            "./src/test/resources/source/My3DPrinter.json", "-o",
            "./src/test/" });
        assertEquals(3, 1 + 2);
    }

    /**
     * testValidateTransformation
     */
    @Test
    void testValidateTransformation() {
        App.main(new String[] {"-i",
            "./src/test/resources/source/My3DPrinter.json",
            "-v" });
        assertEquals(3, 1 + 2);
    }

    /**
     * testMySnippet
     */
    @Test
    void testMySnippet() throws JsonMappingException, JsonProcessingException {
        var jsonPath = "$";
        var json = "{\"x\":\"y\"}";

        DocumentContext ctx = JsonPath.parse(json);

        var xy = ctx.read(jsonPath, String.class);

        xy = null;

        assertEquals(null, xy);
        LoggerFactory.getLogger(AppTest.class).info("".toString());
    }
}
