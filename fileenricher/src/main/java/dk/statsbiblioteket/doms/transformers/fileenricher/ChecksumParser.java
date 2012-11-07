package dk.statsbiblioteket.doms.transformers.fileenricher;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/18/12
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChecksumParser {

    private Map<String,String> nameChecksumsMap = new HashMap<String, String>();

    public ChecksumParser(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null){
            String[] splits = line.split(" ");
            String name = splits[2].trim();
            String checksum = splits[0].trim();
            if (name.endsWith(".log")){
                continue;
            }
            nameChecksumsMap.put(name, checksum);
        }
    }

    public Map<String, String> getNameChecksumsMap() {
        return Collections.unmodifiableMap(nameChecksumsMap);
    }
}
