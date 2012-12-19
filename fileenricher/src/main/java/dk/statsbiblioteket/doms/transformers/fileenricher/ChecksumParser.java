package dk.statsbiblioteket.doms.transformers.fileenricher;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChecksumParser {
    private Map<String, String> nameChecksumsMap = new HashMap<String, String>();
    private Map<String, String> sizeMap = new HashMap<String, String>();

    public ChecksumParser(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null){
            String[] splits = line.split(" ");
            String name = splits[2].trim();
            String checksum = splits[0].trim();
            String size = splits[1].trim();
            if (!name.endsWith(".log")){
                nameChecksumsMap.put(name, checksum);
                sizeMap.put(name, size);
            }
        }
    }

    public Map<String, String> getNameChecksumsMap() {
        return Collections.unmodifiableMap(nameChecksumsMap);
    }

    public Map<String, String> getSizeMap() {
        return Collections.unmodifiableMap(sizeMap);
    }
}
