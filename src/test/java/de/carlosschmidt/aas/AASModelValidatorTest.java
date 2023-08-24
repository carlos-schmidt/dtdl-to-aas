package de.carlosschmidt.aas;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * AASModelValidator test.
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public class AASModelValidatorTest {

    @Test
    void failValidationTest() {
        assertFalse(new AASModelValidator().validate(""));
    }
}
