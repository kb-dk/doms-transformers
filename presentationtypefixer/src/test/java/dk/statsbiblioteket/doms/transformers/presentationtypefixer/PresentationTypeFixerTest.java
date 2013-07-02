package dk.statsbiblioteket.doms.transformers.presentationtypefixer;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.FileRecordingObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.ObjectListHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 7/2/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationTypeFixerTest {
    private PropertyBasedDomsConfig config;
    private MockWebservice webservice;
    private String testObjectPid;

    @Before
    public void setUp() throws Exception {

        webservice = new MockWebservice();
        testObjectPid = webservice.newObject(null,null,null);

        String contents = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.xml"));

        webservice.modifyDatastream(testObjectPid,"PBCORE",contents,"sdf");

        config = new PropertyBasedDomsConfig(new File(Thread.currentThread().getContextClassLoader().getResource("presentationtypefixer.properties").toURI()));

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMain() throws Exception {

        ObjectHandler objectHandler = new DomsPresentationTypeFixerObjectHandler(config, webservice);

        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = new ArrayList<String>();
        uuids.add(testObjectPid);

        objectListHandler.transform(uuids);

        String pbcore = webservice.getDatastreamContents(testObjectPid, "PBCORE");
        assertTrue(pbcore.contains("formatMediaType"));

    }


}
