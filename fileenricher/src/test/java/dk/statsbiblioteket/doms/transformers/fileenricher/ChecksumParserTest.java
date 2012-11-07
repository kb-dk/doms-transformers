package dk.statsbiblioteket.doms.transformers.fileenricher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChecksumParserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void parseFile() throws URISyntaxException, IOException {
        ChecksumParser checksums =
                new ChecksumParser(
                    new BufferedReader(
                            new InputStreamReader(
                                    Thread.currentThread().getContextClassLoader().getResourceAsStream("checksumTestFile"))));
        assertThat(checksums.getNameChecksumsMap().size(), is(3));
    }
}
