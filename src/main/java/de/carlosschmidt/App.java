package de.carlosschmidt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.carlosschmidt.aas.AASModelValidator;
import de.carlosschmidt.transform.dtdl.DTDLTransformationController;

/**
 * Main class
 * 
 * @author Carlos Schmidt
 * @version 0.1
 */
public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
        LOGGER.info("DTDL-to-AAS-Transformer on version 1.0 running!");
    }

    private String transform(String inputJsonString, boolean validate)
            throws IOException {
        LOGGER.info("Transforming DTDL Element(s)...");
        var dtdlTFC = new DTDLTransformationController();
        var transformationResult = dtdlTFC.canHandle(inputJsonString)
                ? dtdlTFC.transform(inputJsonString).toPrettyString()
                : null;
        if (validate) {
            LOGGER.info("Transformed DTDL Element(s). Validating...");
            boolean valid = new AASModelValidator().validate(transformationResult);
            return valid ? transformationResult : "Invalid elements produced";
        } else {
            return transformationResult;
        }
    }

    /**
     * Says hello to the world.
     * 
     * @param args
     *            The arguments of the program.
     */
    public static void main(String[] args) {
        final App optimus = new App();

        // Command line options
        Options options = new Options();

        options.addOption(Option.builder("i")
                .longOpt("inputFile")
                .hasArg()
                .desc("input JSON file path (absolute path needed)")
                .required()
                .build());
        options.addOption(Option.builder("v")
                .longOpt("validateOutput")
                .hasArg(false)
                .desc("set flag if output should be validated")
                .required(false)
                .build());
        options.addOption(Option.builder("o")
                .longOpt("outputPath")
                .hasArg(true)
                .desc("where to put transformed object file, default \".\"")
                .required(false)
                .build());

        CommandLine cmd;

        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
            return;
        }

        Path inputFilePath;
        Path outputFilePath = null;

        try {
            inputFilePath = Path.of(cmd.getOptionValue("i"));
            Path outputPath = null;

            if (Objects.nonNull(cmd.getOptionValue("o"))) {
                outputPath = Path.of(cmd.getOptionValue("o"));
            }

            if (Objects.nonNull(outputPath) && outputPath.toFile().isDirectory()) {
                outputFilePath = outputPath;
            }
        } catch (InvalidPathException invalidPathException) {
            LOGGER.error("Invalid path, aborting.");
            return;
        }

        var validate = cmd.hasOption("v");

        // Read input file, transform
        String toParse;
        String transformed;

        try {
            toParse = readFileToString(inputFilePath);
            transformed = optimus.transform(toParse, validate);
            LOGGER.debug(transformed);
        } catch (IOException io) {
            LOGGER.error(io.getMessage());
            return;
        }

        if (Objects.nonNull(transformed)) {
            if (Objects.isNull(outputFilePath)) {
                LOGGER.info("Transformed model:\n{}", transformed);
                return;
            }
            try {
                Files.write(Path.of(outputFilePath.toString(), "output.json"), transformed.getBytes());
            } catch (IOException ioException) {
                LOGGER.error("An I/O error occurred writing to or creating the file: {}", ioException.getMessage());
            }
        }
    }

    /**
     * Read file provided by path into string
     * 
     * @param pathToFile
     *            Path to file to be read
     * @return Contents of file
     * @throws IOException
     *             if an I/O error occurs reading from the file or a malformed or
     *             unmappable byte sequence is read
     */
    public static String readFileToString(Path pathToFile) throws IOException {
        return String.join("", Files.readAllLines(pathToFile));
    }
}