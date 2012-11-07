package dk.statsbiblioteket.doms.transformers.fileobjectcreator;

import dk.statsbiblioteket.util.FileAlreadyExistsException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ResultWriter {
    private static final int STATUS_CHAR_PR_LINE = 100;
    private static final char SUCCESS_CHAR = '+';
    private static final char EXISTING_CHAR = ',';
    private static final char FAILURE_CHAR = '#';
    private static final char IGNORE_CHAR = '.';

    private String baseName;
    private ResultWriterErrorHandler errorHandler;
    private BufferedWriter newUuidWriter;
    private BufferedWriter existingUuidWriter;
    private BufferedWriter successWriter;
    private BufferedWriter failureWriter;
    private BufferedWriter badFFProbeWriter;
    private BufferedWriter ignoreWriter;

    private int logCounter = 0;

    private boolean printProgress = false;

    public ResultWriter(String baseName, ResultWriterErrorHandler errorHandler) throws FileAlreadyExistsException, IOException {
        this.baseName = baseName;
        this.errorHandler = errorHandler;

        File newUuidLog = new File(baseName + "new-uuids");
        File existingUuidLog = new File(baseName + "existing-uuids");
        File successLog = new File(baseName + "successful-files");
        File failureLog = new File(baseName + "failed-files");
        File badFFProbeLog = new File(baseName + "ffprobe-errors");
        File ignoreLog = new File(baseName + "ignored-files");

        List<File> logFiles = new LinkedList<File>();
        logFiles.add(newUuidLog);
        logFiles.add(existingUuidLog);
        logFiles.add(successLog);
        logFiles.add(failureLog);
        logFiles.add(badFFProbeLog);
        logFiles.add(ignoreLog);

        boolean logsCleared = true;

        for (File f : logFiles) {
            if (f.exists()) {
                logsCleared = false;
                System.out.println("File already exists: " + f);
            }
        }

        if (!logsCleared) {
            throw new FileAlreadyExistsException(""); // FIXME: Do something sensible here.
        } else {
            newUuidWriter = new BufferedWriter(new FileWriter(newUuidLog));
            existingUuidWriter = new BufferedWriter(new FileWriter(existingUuidLog));
            successWriter = new BufferedWriter(new FileWriter(successLog));
            failureWriter = new BufferedWriter(new FileWriter(failureLog));
            badFFProbeWriter = new BufferedWriter(new FileWriter(badFFProbeLog));
            ignoreWriter = new BufferedWriter(new FileWriter(ignoreLog));
        }
    }

    public void closeAll() throws IOException {
        newUuidWriter.close();
        existingUuidWriter.close();
        successWriter.close();
        failureWriter.close();
        badFFProbeWriter.close();
        ignoreWriter.close();
    }

    public String getHelpMessage() {
        return String.format(
                "'%c': added file, '%c': ignored file, '%c': existing file, '%c': failed file, %d files/line.",
                SUCCESS_CHAR,
                IGNORE_CHAR,
                EXISTING_CHAR,
                FAILURE_CHAR,
                STATUS_CHAR_PR_LINE);
    }

    public void setPrintProgress(boolean printProgress) {
        this.printProgress = printProgress;
    }

    public synchronized boolean logSuccess(String data) {
        try {
            successWriter.write(data + "\n");
            successWriter.flush();
            logChar(SUCCESS_CHAR);
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    public synchronized boolean logFailure(String data) {
        try {
            failureWriter.write(data + "\n");
            failureWriter.flush();
            logChar(FAILURE_CHAR);
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    public synchronized boolean logBadFFProbeData(DomsObject domsObject) {
        try {
            badFFProbeWriter.write(domsObject.formatAsInput() + "\n");
            badFFProbeWriter.flush();
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    public synchronized boolean logIgnored(String data) {
        try {
            ignoreWriter.write(data + "\n");
            ignoreWriter.flush();
            logChar(IGNORE_CHAR);
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    public synchronized boolean logExisting(String data) {
        try {
            existingUuidWriter.write(data + "\n");
            existingUuidWriter.flush();
            logChar(EXISTING_CHAR);
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    public synchronized boolean logNewUuid(String uuid) {
        try {
            newUuidWriter.write(uuid + "\n");
            newUuidWriter.flush();
            return true;
        } catch (IOException e) {
            errorHandler.handleError(e);
            return false;
        }
    }

    private synchronized void logChar(char c) {
        System.out.print(c + incrementLogCounter());
    }

    private synchronized String incrementLogCounter() {
        logCounter += 1;

        if (logCounter % 100 == 0) {
            return " " + logCounter + System.getProperty("line.separator");
        }

        return "";
    }
}
