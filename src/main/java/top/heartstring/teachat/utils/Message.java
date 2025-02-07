//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private static BufferedWriter WRITER;
    private static final Object LOCK = new Object();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static File LOG_FOLDER;

    public Message() {
    }

    public static void setRecord(File logFile) {
        LOG_FOLDER = logFile;
    }

    public static void record(String msg) {
        if (LOG_FOLDER != null) {
            synchronized(LOCK) {
                if (WRITER != null) {
                    try {
                        WRITER.write("[" + formatter.format(LocalDateTime.now()) + "]" + msg);
                        WRITER.newLine();
                        WRITER.flush();
                    } catch (IOException var7) {
                        IOException e = var7;
                        throw new RuntimeException(e);
                    }
                } else {
                    File logFolder = new File(LOG_FOLDER, "logs");

                    try {
                        if (!logFolder.exists()) {
                            if (!logFolder.mkdirs()) {
                                throw new RuntimeException("Failed to create log folder");
                            }
                        } else if (logFolder.isFile()) {
                            if (!logFolder.delete()) {
                                throw new RuntimeException("Failed to delete log file");
                            }

                            if (!logFolder.createNewFile()) {
                                throw new RuntimeException("Failed to create log file");
                            }
                        }

                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        File log = new File(logFolder, format.format(LocalDateTime.now()) + ".log");
                        if (!log.exists() && !log.createNewFile()) {
                            throw new RuntimeException("Could not create log file");
                        }

                        WRITER = new BufferedWriter(new FileWriter(log, true));
                        record(msg);
                    } catch (IOException var6) {
                        IOException e = var6;
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }
}
