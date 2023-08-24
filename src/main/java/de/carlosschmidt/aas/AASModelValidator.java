package de.carlosschmidt.aas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.json.JsonDeserializer;
import io.adminshell.aas.v3.model.Referable;
import io.adminshell.aas.v3.model.impl.DefaultAnnotatedRelationshipElement;
import io.adminshell.aas.v3.model.impl.DefaultOperation;
import io.adminshell.aas.v3.model.impl.DefaultProperty;
import io.adminshell.aas.v3.model.impl.DefaultReferenceElement;
import io.adminshell.aas.v3.model.impl.DefaultSubmodel;
import io.adminshell.aas.v3.model.impl.DefaultSubmodelElementCollection;

/**
 * Validate JSON String using AAS java model @see
 * https://github.com/admin-shell-io/java-model
 * 
 * @author Carlos Schmidt
 * @version 0.4
 */
public class AASModelValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AASModelValidator.class);

    private JsonDeserializer aasModelDeserializer;

    /**
     * Validate json string input by deserialization and serialization into and out
     * of AAS java model.
     * 
     * @param jsonInput
     *            aasModelJson
     * @return if deserialization and serialization worked
     */
    public boolean validate(String jsonInput) {
        if (Objects.isNull(aasModelDeserializer)) {
            aasModelDeserializer = new JsonDeserializer();
        }
        List<Class<? extends Referable>> aasModelElements = List.of(DefaultSubmodel.class, DefaultProperty.class,
                DefaultOperation.class, DefaultAnnotatedRelationshipElement.class,
                DefaultReferenceElement.class, DefaultSubmodelElementCollection.class);

        List<Throwable> errorList = new ArrayList<>();

        for (Class<? extends Referable> aasModelElement : aasModelElements) {
            try {
                aasModelDeserializer.readReferable(jsonInput, aasModelElement);
                return true;
            } catch (DeserializationException deserializationException) {
                errorList.add(deserializationException);
            }
        }

        LOGGER.error("Could not deserialize model. Tried with multiple AAS model elements.");

        for (Throwable throwable : errorList) {
            LOGGER.error(throwable.getMessage());
        }

        return false;
    }

}
