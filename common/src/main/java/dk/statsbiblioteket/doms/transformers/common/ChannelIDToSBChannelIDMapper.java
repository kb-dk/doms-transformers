package dk.statsbiblioteket.doms.transformers.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelIDToSBChannelIDMapper {

    private static ChannelIDToSBChannelIDMapper instance;

    public synchronized static ChannelIDToSBChannelIDMapper getInstance(){
        if (instance == null){
            instance = new ChannelIDToSBChannelIDMapper();
        }
        return instance;

    }

    public String mapToSBChannelID(String otherChannelID){
        //TODO not mockup
        return otherChannelID;
    }

    public List<String> mapToSBChannelIDs(List<String> otherChannelIDs){
        List<String> result = new ArrayList<String>(otherChannelIDs.size());
        for (String otherChannelID : otherChannelIDs) {
            result.add(mapToSBChannelID(otherChannelID));
        }
        return result;
    }
}
