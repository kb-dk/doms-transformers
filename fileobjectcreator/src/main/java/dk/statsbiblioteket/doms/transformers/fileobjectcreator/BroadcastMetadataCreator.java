package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class BroadcastMetadataCreator {
    public static void main(String... args) throws IOException, ParseException {
        switch (args.length) {
            case 0:
                System.out.println("Reading data from stdin..");
                new BroadcastMetadataCreator(new BufferedReader(new InputStreamReader(System.in)));
                break;
            case 1:
                System.out.println("Input file: " + args[0]);
                try {
                    new BroadcastMetadataCreator(new BufferedReader(new FileReader(new File(args[0]))));
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + args[1]);
                    System.exit(1);
                }
                break;
            default:
                System.out.println("Usage: bin/fileobjectcreator.sh config-file [input-file]");
                System.exit(1);
        }
    }

    public BroadcastMetadataCreator(BufferedReader reader) throws IOException, ParseException {
        MuxFileChannelCalculator muxChannelCalculator = new MuxFileChannelCalculator(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("muxChannels.csv"));

        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(new FileNameParser(line.trim(), muxChannelCalculator).getBroadCastMetadata());
        }
    }
}
