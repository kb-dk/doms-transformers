package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.transformers.common.muxchannels.MuxFileChannelCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class DomsFileParser {
    private static Logger log = LoggerFactory.getLogger(DomsFileParser.class);

    public static DomsObject parse(String line, MuxFileChannelCalculator muxFileChannelCalculator) throws ParseException {
        String[] parts = line.split(" ", 3);
        if (line != null && !line.isEmpty()) {
            if (parts.length == 3) {
                String fileName = parts[2];
                String checksum = parts[0];
                String fileSize = parts[1];

                if (!fileName.endsWith(".log") && !fileName.contains("_digivid_")) {
                    return new DomsObject(fileName, checksum, fileSize, muxFileChannelCalculator);
                } else {
                    log.debug("Ignored file: " + fileName);
                    return null;
                }
            }
        }
        log.info("Couldn't parse data: " + line);
        return null;
    }
}
