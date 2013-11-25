package dk.statsbiblioteket.doms.transformers.faultyvhstimestampsfixer;

import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FaultyVhsTimestampsFixerTest {
    private PropertyBasedDomsConfig config;
    private MockWebservice webservice;
    private String testObjectPid;
    private String originalContents;

    @Before
    public void setUp() throws Exception {

        webservice = new MockWebservice();
        testObjectPid = webservice.newObject(null,null,null);

        originalContents = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.xml"));

        webservice.modifyDatastream(testObjectPid,"VHS_METADATA", originalContents,"sdf");

        config = new PropertyBasedDomsConfig(new File(Thread.currentThread().getContextClassLoader().getResource(
                "faultyvhstimestampsfixer.properties").toURI()));

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMain() throws Exception {

        List<String> originalCalls = webservice.getCalls();
        assertFalse(originalContents.contains("start_time"));
        ObjectHandler objectHandler = new DomsFaultyVhsTimestampsFixerObjectHandler(config, webservice);

        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = new ArrayList<String>();
        uuids.add(testObjectPid);

        objectListHandler.transform(uuids);

        assertEquals("By here, there should have been one get, one set inactive, one modify and one set active",originalCalls.size(), webservice.getCalls().size() - 4);

        String pbcore = webservice.getDatastreamContents(testObjectPid, "VHS_METADATA");
        assertTrue(pbcore.contains("start_time"));
        assertEquals("As previous, but one more get",originalCalls.size(), webservice.getCalls().size() - 5);

        objectListHandler.transform(uuids);
        pbcore = webservice.getDatastreamContents(testObjectPid, "VHS_METADATA");
        assertTrue(pbcore.contains("start_time"));

        assertEquals("As before, but two more gets, one for the transform which should not modify or change state and one here",originalCalls.size(), webservice.getCalls().size() - 7);

    }


}
