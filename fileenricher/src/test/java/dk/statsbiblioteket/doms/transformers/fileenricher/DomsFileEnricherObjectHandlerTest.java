package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.CalendarUtils;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import dk.statsbiblioteket.doms.transformers.fileenricher.checksums.ChecksumParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.transformers.fileenricher.autogenerated.BroadcastFileDescriptiveMetadataType;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsFileEnricherObjectHandlerTest {

    String testObjectPid;
    
    DomsFileEnricherObjectHandler handler;
    ChecksumParser checksums;
    
    @Before
    public void setUp() throws Exception {
        String testMuxFileName = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";
        CentralWebservice webservice = new MockWebservice();

        testObjectPid = webservice.newObject(null, null, null);
        webservice.addFileFromPermanentURL(testObjectPid,null,null,"http://bitfinder.statsbiblioteket.dk/bart/"+testMuxFileName,null,null);

        checksums = new ChecksumParser(new File(Thread.currentThread().getContextClassLoader().getResource("md5s.zip").toURI()));

        handler = new DomsFileEnricherObjectHandler(null, webservice, checksums, null);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMuxFileNameTransform() throws Exception {
        handler.transform(testObjectPid);
    }

    @Test
    public void testAllFilenames() throws ParseException {
        Set<String> filenames = checksums.getNameChecksumsMap().keySet();
        for (String filename : filenames) {
            BroadcastFileDescriptiveMetadataType metadata = handler.decodeFilename(filename);
            if (metadata == null){
                System.out.println("failed to parse file '"+filename+"'");

            }
        }
    }

    @Test
    public void testDecodeMuxFilename() throws Exception {
        String testMuxFileName = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";
        String testMuxStartTime = "1287514800";
        String testMuxStopTime = "1287518400";
        String testMuxRecorder = "dvb1-1";
        String muxName = testMuxFileName.split("\\.")[0];
        int muxID = Integer.parseInt(muxName.split("mux")[1]);
        assertThat(muxID, is(Integer.parseInt("1")));
        
        BroadcastFileDescriptiveMetadataType metadata = handler.decodeFilename(testMuxFileName);
        assertThat(metadata.getRecorder(), is(testMuxRecorder));
        assertThat(metadata.getStartTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testMuxStartTime)));
        assertThat(metadata.getEndTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testMuxStopTime)));
        assertNotNull(metadata.getChecksum());
    }
    
    @Test
    public void testRadioFileNameTransform() throws Exception {
      String testRadioFileName = "drp1_88.100_DR-P1_pcm_20080509045602_20080510045501_encoder5-2.wav";
      String testRadioRecorder = "encoder5-2";
      String testRadioChannel = "drp1";
      String testRadioStartTime = "1210301762";
      String testRadioStopTime = "1210388101";
      String testRadioFormatUri = "info:pronom/fmt/6";
      
      BroadcastFileDescriptiveMetadataType metadata = handler.decodeFilename(testRadioFileName);
      assertThat(metadata.getRecorder(), is(testRadioRecorder));
      assertThat(metadata.getStartTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testRadioStartTime)));
      assertThat(metadata.getEndTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testRadioStopTime)));
      assertThat(metadata.getChannelIDs().getChannel().get(0), is(testRadioChannel));
      assertThat(metadata.getFormat(), is(testRadioFormatUri));

      
    }
    
    @Test
    public void testMPEGFileNameTransform() throws Exception {
        String testMPEGFileName = "tv2c_623.250_K40-TV2-Charlie_mpeg1_20080503121001_20080504030601_encoder3-2.mpeg";
        String testMPEGRecorder = "encoder3-2";
        String testMPEGChannel = "tv2c";
        String testMPEGStartTime = "1209809401";
        String testMPEGStopTime = "1209863161";
        String testMPEGFormatUri = "info:pronom/x-fmt/385";
        
        BroadcastFileDescriptiveMetadataType metadata = handler.decodeFilename(testMPEGFileName);
        assertThat(metadata.getRecorder(), is(testMPEGRecorder));
        assertThat(metadata.getStartTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testMPEGStartTime)));
        assertThat(metadata.getEndTimeDate(), is(CalendarUtils.getXmlGregorianCalendar(testMPEGStopTime)));
        assertThat(metadata.getChannelIDs().getChannel().get(0), is(testMPEGChannel));
        assertThat(metadata.getFormat(), is(testMPEGFormatUri));
        
    }
}
