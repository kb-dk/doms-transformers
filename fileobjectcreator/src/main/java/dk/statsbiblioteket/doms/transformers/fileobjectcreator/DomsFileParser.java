package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.doms.transformers.fileobjectcreator.MuxFileChannelCalculator;

import java.text.ParseException;

public class DomsFileParser {
    public static DomsObject parse(FFProbeLocationPropertyBasedDomsConfig config, String baseName, String line, MuxFileChannelCalculator muxFileChannelCalculator) throws ParseException, FileIgnoredException {
        String[] parts = line.split(" ", 3);
        if (line != null && !line.isEmpty()) {
            if (parts.length == 3) {
                String fileName = parts[2];
                String checksum = parts[0];
                String fileSize = parts[1];

                if (!fileName.endsWith(".log") && !fileName.contains("_digivid_")) {
                    return new DomsObject(config, baseName, fileName, checksum, fileSize, muxFileChannelCalculator);
                } else {
                    throw new FileIgnoredException(fileName);
                }
            }
        }
        throw new ParseException("Couldn't parse data: " + line, 0);
    }
}
