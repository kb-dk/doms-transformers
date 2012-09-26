package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

public class DomsFileParser implements Iterable<DomsObject> {
    private BufferedReader reader;
    private Map<String, String> checksums;
    MuxFileChannelCalculator muxFileChannelCalculator;

    public DomsFileParser(BufferedReader reader,
                          Map<String,String> checksums,
                          MuxFileChannelCalculator muxFileChannelCalculator) {

        this.reader = reader;
        this.checksums = checksums;
        this.muxFileChannelCalculator = muxFileChannelCalculator;
    }

    @Override
    public Iterator<DomsObject> iterator() {
        try {
            return new DomsFileParserIterator(reader, checksums, muxFileChannelCalculator);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }
}
