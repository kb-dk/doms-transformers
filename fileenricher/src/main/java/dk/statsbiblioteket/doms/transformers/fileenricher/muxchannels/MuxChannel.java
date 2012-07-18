package dk.statsbiblioteket.doms.transformers.fileenricher.muxchannels;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MuxChannel {

    private int muxNumber;
    private String channelID;
    private Date start;
    private Date end;

    public MuxChannel(int muxNumber, String channelID, Date start, Date end) {
        this.muxNumber = muxNumber;
        this.channelID = channelID;
        this.start = start;
        this.end = end;
    }

    public int getMuxNumber() {
        return muxNumber;
    }

    public String getChannelID() {
        return channelID;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
