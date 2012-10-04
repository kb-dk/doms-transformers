package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

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
                        try {
                            DomsObject tmpDomsObject = DomsFileParser.parse(line, muxFileChannelCalculator);

                            if (tmpDomsObject != null) {
                                this.next = tmpDomsObject;
                                hit = true;
                            } else {
                                FileObjectCreator.logIgnored(line);
                            }

                        } catch (ParseException e) {
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
