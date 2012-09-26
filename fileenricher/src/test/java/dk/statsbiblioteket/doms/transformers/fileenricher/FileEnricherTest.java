package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.*;
import dk.statsbiblioteket.doms.transformers.common.checksums.ChecksumParser;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileEnricherTest {

    private String testMuxFileName = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";
    private String testObjectPid;
    CentralWebservice webservice;
    FileEnricherConfig config;
    File ffprobeFile;
    @Before
    public void setUp() throws Exception {

        webservice = new MockWebservice();

        testObjectPid = webservice.newObject(null, null, null);
        webservice.addFileFromPermanentURL(testObjectPid,null,null,"http://bitfinder.statsbiblioteket.dk/bart/"+testMuxFileName,null,null);

        config = new FFProbeLocationPropertyBasedDomsConfig(new File(Thread.currentThread().getContextClassLoader().getResource("fileenricher.properties").toURI()));
        File ffprobeFile;
        ffprobeFile = new File(config.getFFprobeFilesLocation(), testObjectPid + ".stdout");
        File ffprobeContents = new File(Thread.currentThread().getContextClassLoader().getResource("ffprobeContents.xml").toURI());
        ffprobeFile.getParentFile().mkdirs();
        FileUtils.copyFile(ffprobeContents,ffprobeFile);

    }

    @After
    public void tearDown() throws Exception {
        if (ffprobeFile != null){
            ffprobeFile.delete();
        }
    }


    @Test
    public void testMain() throws Exception {
        DomsFFProbeFileEnricherObjectHandler delegate = new DomsFFProbeFileEnricherObjectHandler(config,webservice);
        ChecksumParser checksums = new ChecksumParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("md5s.zip"));
        ObjectHandler objectHandler = new DomsFileEnricherObjectHandler(config, webservice, checksums, delegate);

        ObjectListHandler objectListHandler = new FileRecordingObjectListHandler(config, objectHandler);

        List<String> uuids = new ArrayList<String>();
        uuids.add(testObjectPid);

        objectListHandler.transform(uuids);
    }
}
