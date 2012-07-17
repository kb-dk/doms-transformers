package dk.statsbiblioteket.doms.transformers.fileenricher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsFileEnricherObjectHandlerTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testTransform() throws Exception {
        assertThat("Thing",is("Thang"));
    }
}
