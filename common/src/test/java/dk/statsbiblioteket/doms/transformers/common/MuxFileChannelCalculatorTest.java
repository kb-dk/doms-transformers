package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MuxFileChannelCalculatorTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetChannelIDsForMux() throws Exception {
        InputStream channelStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv");
        MuxFileChannelCalculator calc = new MuxFileChannelCalculator(channelStream);
        List<String> channelIDs1 = calc.getChannelIDsForMux(1, new Date());
        assertThat(channelIDs1.size(), is(5));
        assertTrue(channelIDs1.contains("dr1"));

        List<String> channelIDs2 = calc.getChannelIDsForMux(2, new Date());
        assertThat(channelIDs2.size(), is(5));
        assertFalse(channelIDs2.contains("dr1"));
    }
}
