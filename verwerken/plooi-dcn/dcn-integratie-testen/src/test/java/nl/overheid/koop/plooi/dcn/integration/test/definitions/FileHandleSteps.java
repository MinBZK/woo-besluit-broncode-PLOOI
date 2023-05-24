package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import io.cucumber.java.en.Given;
import java.io.IOException;

public class FileHandleSteps {

    @Given("documents are available in the {string} directory")
    public void documents_are_available_in_the_directory(String directory) throws IOException {
        // Noop; the container directly mounts the directory with test data read-only
    }
}
