package it.pagopa.atmlayer.wf.process.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.atmlayer.wf.process.util.Utility;

@QuarkusTest
public class UtilityTest {
    
    @Test
    public void testDeleteFileIfExists() throws IOException {

        String testFilePath = "test.txt";

        Files.createFile(Paths.get(testFilePath));

        Utility.deleteFileIfExists(testFilePath);

        // Assert that the file does not exist after deletion
        assert !Files.exists(Paths.get(testFilePath));
    }

}
