package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class DomsFileParser implements Iterable<DomsObject> {
    private BufferedReader reader;
    MuxFileChannelCalculator muxFileChannelCalculator;

    public DomsFileParser(BufferedReader reader,
                          MuxFileChannelCalculator muxFileChannelCalculator) {

        this.reader = reader;
        this.muxFileChannelCalculator = muxFileChannelCalculator;
    }

    @Override
    public Iterator<DomsObject> iterator() {
        try {
            return new DomsFileParserIterator(reader, muxFileChannelCalculator);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static DomsObject parse(String line, MuxFileChannelCalculator muxFileChannelCalculator) throws ParseException {
        String[] parts = line.split(" ", 3);
        if (line != null && !line.isEmpty()) {
            if (parts.length == 3) {
                String fileName = parts[2];
                String checksum = parts[0];
                String fileSize = parts[1];

                if (!fileName.endsWith(".log") && !fileName.contains("_digivid_")) {
                    return new DomsObject(fileName, checksum, fileSize, muxFileChannelCalculator);
                }
            }
        }
        return null;
    }
}
