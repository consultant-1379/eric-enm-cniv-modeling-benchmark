/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.containerisation.modeling.benchmark.io.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RandomFileGeneratorTest {

    private static final int NUMBER_OF_FILES = 5;
    private static final int FILE_SIZE_IN_KB = 25;
    private static final int FILE_SIZE_IN_BYTES = FILE_SIZE_IN_KB * 1024;

    @TempDir
    private File testDir;

    @Test
    void GIVEN_outputDirectoryExists_WHEN_generatingFiveFilesOfTwentyFiveKB_THEN_fiveFilesGeneratedOfExactSizeWithExpectedNaming() {
        assertTrue(testDir.exists());

        final File layoutFolder = RandomFileGenerator.fileLayout(NUMBER_OF_FILES, testDir.getAbsolutePath(), FILE_SIZE_IN_KB);
        assertTrue(layoutFolder.getAbsolutePath().startsWith(testDir.getAbsolutePath() + File.separator));

        final List<File> allFilesCreated = getSortedListOfFilesUnderDir(testDir);
        assertEquals(NUMBER_OF_FILES, allFilesCreated.size());
        for (int i = 0; i < NUMBER_OF_FILES; i++) {
            final File generatedFile = allFilesCreated.get(i);
            assertEquals("model-" + i + ".xml", generatedFile.getName());
            assertEquals(FILE_SIZE_IN_BYTES, generatedFile.length());
        }
    }

    @Test
    void GIVEN_outputDirectoryDoesNotExist_WHEN_generatingRandomFiles_THEN_exceptionThrown() {
        final File outputDir = new File(testDir + "/" + "output");
        final String outputDirPath = outputDir.getAbsolutePath();
        assertFalse(outputDir.exists());

        final Exception exception = assertThrows(IllegalArgumentException.class, () ->
                RandomFileGenerator.fileLayout(NUMBER_OF_FILES, outputDirPath, FILE_SIZE_IN_KB));
        assertTrue(exception.getMessage().contains("Invalid directory provided:"));
    }

    private List<File> getSortedListOfFilesUnderDir(final File directory) {
        final List<File> files = new ArrayList<>();
        final File[] directoryFiles = directory.listFiles();

        if (directoryFiles == null) {
            fail("Directory should contain generated content");
        }
        for (final File file: directoryFiles) {
            if (file.isDirectory()) {
                files.addAll(getSortedListOfFilesUnderDir(file));
            } else if (file.isFile()) {
                files.add(file);
            }
        }

        Collections.sort(files);
        return files;
    }
}
