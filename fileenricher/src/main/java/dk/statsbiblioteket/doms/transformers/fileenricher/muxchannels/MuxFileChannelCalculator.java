package dk.statsbiblioteket.doms.transformers.fileenricher.muxchannels;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MuxFileChannelCalculator {

    Set<MuxChannel> muxChannels = new HashSet<MuxChannel>();

    public MuxFileChannelCalculator(File muxChannelsFile) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(muxChannelsFile)));
        String line;
        //2009-10-30 10:50:00.0
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        while ((line = reader.readLine()) != null){
            line = line.trim();
            if (line.length() == 0){
                continue;
            }
            String[] splits = line.split(",");
            int muxNumber = Integer.parseInt(splits[0]);
            Date start = format.parse(splits[3]);
            Date end = format.parse(splits[4]);
            muxChannels.add(new MuxChannel(muxNumber,splits[1],start,end));
        }
    }


    public List<String> getChannelIDsForMux(int muxNumber, Date startDate){
        List<String> results = new ArrayList<String>();
        for (MuxChannel muxChannel : muxChannels) {
            if (muxChannel.getMuxNumber() == muxNumber){
                if (muxChannel.getStart().before(startDate) && muxChannel.getEnd().after(startDate)){
                    results.add(muxChannel.getChannelID());
                }
            }
        }
        return results;
    }
}
