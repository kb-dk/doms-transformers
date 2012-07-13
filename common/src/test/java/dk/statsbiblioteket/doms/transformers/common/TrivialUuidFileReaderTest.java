package dk.statsbiblioteket.doms.transformers.common;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test file reader.
 */
public class TrivialUuidFileReaderTest extends TestCase {
    @Test
    public void testReadUuids() throws IOException {
        List<String> uuids = new TrivialUuidFileReader().readUuids(new File("src/test/resources/uuids.txt"));
        assertEquals(2, uuids.size());
        assertEquals("uuid:cb8da856-fae8-473f-9070-8d24b5a84cfc", uuids.get(0));
        assertEquals("uuid:99c1b516-3ea9-49ce-bfbc-ae1ea8faf0e3", uuids.get(1));
    }
}
