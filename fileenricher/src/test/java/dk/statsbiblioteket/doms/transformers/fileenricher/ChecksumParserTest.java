package dk.statsbiblioteket.doms.transformers.fileenricher;

import org.hamcrest.CoreMatchers;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/18/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChecksumParserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void parseFile() throws URISyntaxException, IOException {
        ChecksumParser checksums = new ChecksumParser(new File(Thread.currentThread().getContextClassLoader().getResource("md5s.zip").toURI()));
        assertThat(checksums.getNameChecksumsMap().size(), is(135673));

    }
}
