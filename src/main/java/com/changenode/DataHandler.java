package com.changenode;

import com.changenode.interfaces.ErrorInterface;
import com.changenode.interfaces.ThreadInterface;
import javafx.application.Platform;
import javafx.scene.control.Label;
import jdk.jfr.DataAmount;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DataHandler {
    private final LogHandler logger;
    private WorkBookHandler wbh;
    public static class Process {
        public static final int LOAD = 10001;
        public static final int GET_LINK_FROM_JSON = 10002;
    }
    private FileHandler fileHandler;
    private ErrorInterface errorInterface;
    private ThreadInterface threadInterface;
    public DataHandler(LogHandler logger) {
        this.logger = logger;
    }
    public void setErrorInterface(ErrorInterface ei) {
        errorInterface = ei;
    }
    public void setThreadInterface(ThreadInterface ti) {threadInterface = ti;}
    public DataPackage getPackage() {
        return wbh.dataPackage;
    }
    public void load(Label addr) {
        Thread t = new Thread(() -> {
            try {
                String PATH_TO_JSON = "src/main/resources/data/userdata.JSON";
                String PATH_TO_TEMP = "src/main/resources/data/";

                logger.log("Initializing FH");

                fileHandler = new FileHandler(PATH_TO_JSON);

                logger.log("Retrieving JSON-data");

                String link = fileHandler.getLinkFromJSON(logger);
                if (link.equals("404")) {
                    errorInterface.onCatch(new IOException("getLinkFromJSON() reported: 404"),Process.GET_LINK_FROM_JSON);
                }

                if (addr != null) {
                    String addrHint;

                    logger.log("Updating hint");

                    if (link.length() > 46) {
                        addrHint = "..." + link.substring(link.length() - 46);
                    } else {
                        addrHint = link;
                    }
                    Platform.runLater(() -> {
                        addr.setText(addrHint);
                    });
                }

                logger.log("Opening URL connection");

                URL url = new URL(link);
                Path destination = Path.of(PATH_TO_TEMP, "temp.xlsx");

                logger.log("Creating directories");

                Files.createDirectories(destination.getParent());

                logger.log("Downloading updates");
                long startTime = System.currentTimeMillis();
                Files.copy(url.openStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                logger.log("Download completed time elapsed (ms): "+(System.currentTimeMillis()-startTime)+", size (bytes): "+Files.size(destination));

                logger.log("Formatting data");

                wbh = new WorkBookHandler(PATH_TO_TEMP+"temp.xlsx");
                wbh.extract();

                threadInterface.onNotify();

            } catch (IOException | NotOfficeXmlFileException | ParseException ioe) {
                errorInterface.onCatch(ioe, Process.LOAD);
            }
        });
        t.start();
    }
}
