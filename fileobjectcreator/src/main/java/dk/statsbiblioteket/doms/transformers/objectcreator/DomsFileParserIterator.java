package dk.statsbiblioteket.doms.transformers.objectcreator;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

public class DomsFileParserIterator implements Iterator<DomsObject> {
    private BufferedReader reader;
    private MuxFileChannelCalculator muxFileChannelCalculator;
    private DomsObject next = null;

    public DomsFileParserIterator(BufferedReader reader,
                                  MuxFileChannelCalculator muxFileChannelCalculator)
            throws FileNotFoundException {

        this.reader = reader;
        this.muxFileChannelCalculator = muxFileChannelCalculator;
    }

    private synchronized DomsObject fetchNext() {
        if (this.next == null) {
            try {
                String line;
                Boolean hit = false;
                while (!hit && (line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        String[] parts = line.split(" ", 3);
                        if (parts.length == 3) {
                            try {
                                String fileName = parts[2];
                                String checksum = parts[0];
                                String fileSize = parts[1];

                                if (fileName.endsWith(".log") || fileName.contains("_digivid_")) {
                                    continue;
                                }

                                this.next = new DomsObject(fileName, checksum, fileSize, muxFileChannelCalculator);
                                hit = true;
                            } catch (ParseException e) {
                            }
                        }
                    }
                }
            } catch (IOException e) {
                this.next = null;
            }
        }

        return this.next;
    }

    @Override
    public boolean hasNext() {
        return (fetchNext() != null);
    }

    @Override
    public synchronized DomsObject next() {
        DomsObject domsObject = fetchNext();
        this.next = null;

        return domsObject;
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }
}
