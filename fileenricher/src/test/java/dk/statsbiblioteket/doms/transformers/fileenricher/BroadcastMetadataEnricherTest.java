package dk.statsbiblioteket.doms.transformers.fileenricher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.CalendarUtils;
import dk.statsbiblioteket.doms.transformers.fileobjectcreator.FileNameParser;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import dk.statsbiblioteket.doms.transformers.common.autogenerated.BroadcastMetadata;
import dk.statsbiblioteket.doms.transformers.fileobjectcreator.MuxFileChannelCalculator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class BroadcastMetadataEnricherTest {
    String testObjectPid;
    BroadcastMetadataEnricher handler;
    ChecksumParser checksums;
    MuxFileChannelCalculator muxFileChannelCalculator;
    CentralWebservice webservice;

    @Before
    public void setUp() throws Exception {
        checksums = new ChecksumParser(
                new BufferedReader(
                        new InputStreamReader(
                                Thread.currentThread().getContextClassLoader().getResourceAsStream("checksumTestFile"))));

        muxFileChannelCalculator = new MuxFileChannelCalculator(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv"));


        webservice = new MockWebservice();
        handler = new BroadcastMetadataEnricher(null, webservice, checksums.getNameChecksumsMap(), null);
    }

    @Test
    @Ignore("Find a way to give the test ffprobe-data matching the filename.")
    public void testMuxFileNameTransform() throws Exception {
        String testMuxFileName = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";
        CentralWebservice webservice = new MockWebservice();

        String testObjectPid = webservice.newObject(null, null, null);
        webservice.addFileFromPermanentURL(testObjectPid,null,null,"http://bitfinder.statsbiblioteket.dk/bart/"+testMuxFileName,null,null);

        String filename = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";

        BroadcastMetadataEnricher handler = new BroadcastMetadataEnricher(null, webservice, checksums.getNameChecksumsMap(), null);
        handler.transform(testObjectPid);
    }

    @Test
    public void testAllFilenames() throws ParseException {
        Set<String> filenames = checksums.getNameChecksumsMap().keySet();
        for (String filename : filenames) {
            BroadcastMetadata metadata =
                    new FileNameParser(filename, checksums.getNameChecksumsMap().get(filename), muxFileChannelCalculator).getBroadCastMetadata();
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

        BroadcastMetadata metadata =
                new FileNameParser(testMuxFileName, checksums.getNameChecksumsMap().get(testMuxFileName), muxFileChannelCalculator).getBroadCastMetadata();
        assertThat(metadata.getRecorder(), is(testMuxRecorder));
        assertThat(metadata.getStartTime(), is(CalendarUtils.getXmlGregorianCalendar(testMuxStartTime)));
        assertThat(metadata.getStopTime(), is(CalendarUtils.getXmlGregorianCalendar(testMuxStopTime)));
        assertNotNull(metadata.getChecksum());
    }

    @Test
    public void testRadioFileNameTransform() throws Exception {
        String testRadioFileName = "drp1_88.100_DR-P1_pcm_20080509045602_20080510045501_encoder5-2.wav";
        String testRadioRecorder = "encoder5-2";
        String testRadioChannel = "drp1";
        String testRadioStartTime = "1210301762";
        String testRadioStopTime = "1210388101";
        String testRadioFormatName = "wav";

        BroadcastMetadata metadata =
                new FileNameParser(testRadioFileName, checksums.getNameChecksumsMap().get(testRadioFileName), muxFileChannelCalculator).getBroadCastMetadata();
        assertThat(metadata.getRecorder(), is(testRadioRecorder));
        assertThat(metadata.getStartTime(), is(CalendarUtils.getXmlGregorianCalendar(testRadioStartTime)));
        assertThat(metadata.getStopTime(), is(CalendarUtils.getXmlGregorianCalendar(testRadioStopTime)));
        assertThat(metadata.getChannels().getChannel().get(0).getChannelID(), is(testRadioChannel));
        assertThat(metadata.getFormat(), is(testRadioFormatName));
    }

    @Test
    public void testMPEGFileNameTransform() throws Exception {
        String testMPEGFileName = "tv2c_623.250_K40-TV2-Charlie_mpeg1_20080503121001_20080504030601_encoder3-2.mpeg";
        String testMPEGRecorder = "encoder3-2";
        String testMPEGChannel = "tv2c";
        String testMPEGStartTime = "1209809401";
        String testMPEGStopTime = "1209863161";
        String testMPEGFormatName = "mpeg1";

        BroadcastMetadata metadata =
                new FileNameParser(testMPEGFileName, checksums.getNameChecksumsMap().get(testMPEGFileName), muxFileChannelCalculator).getBroadCastMetadata();
        assertThat(metadata.getRecorder(), is(testMPEGRecorder));
        assertThat(metadata.getStartTime(), is(CalendarUtils.getXmlGregorianCalendar(testMPEGStartTime)));
        assertThat(metadata.getStopTime(), is(CalendarUtils.getXmlGregorianCalendar(testMPEGStopTime)));
        assertThat(metadata.getChannels().getChannel().get(0).getChannelID(), is(testMPEGChannel));
        assertThat(metadata.getFormat(), is(testMPEGFormatName));
    }
}
