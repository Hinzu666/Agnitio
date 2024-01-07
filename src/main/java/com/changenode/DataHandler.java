package com.changenode;

import com.changenode.interfaces.ErrorInterface;
import com.changenode.interfaces.ThreadInterface;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.json.simple.parser.ParseException;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DataHandler {
    private LogHandler logger;
    public static class Process {
        public static int LOAD = 10001;
    }
    private FileHandler fileHandler;
    private ErrorInterface errorInterface;
    private ThreadInterface threadInterface;
    DataHandler (LogHandler logger) {
        this.logger = logger;
    }

    public void setErrorInterface(ErrorInterface ei) {
        errorInterface = ei;
    }
    public void setThreadInterface(ThreadInterface ti) {threadInterface = ti;}
    public void load(Label addr) {
        Thread t = new Thread(() -> {
            try {
                String PATH_TO_JSON = "src/main/resources/data/userdata.JSON";
                String PATH_TO_TEMP = "src/main/resources/data/";

                fileHandler = new FileHandler(PATH_TO_JSON);

                String link = fileHandler.getLinkFromJSON();
                String addrHint;
                if (link.length() > 46) {
                    addrHint = "..." + link.substring(link.length() - 46);
                } else {
                    addrHint = link;
                }
                Platform.runLater(() -> {
                    addr.setText(addrHint);
                });

                URL url = new URL(link);
                Path destination = Path.of(PATH_TO_TEMP, "temp.xlsx");

                Files.createDirectories(destination.getParent());

                logger.log("Downloading updates");
                Files.copy(url.openStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                logger.log("Formatting data");

                WorkBookHandler wbh = new WorkBookHandler(PATH_TO_TEMP+"temp.xlsx");
                wbh.extract();


                threadInterface.onNotify();

            } catch (IOException ioe) {
                ioe.printStackTrace(); //TODO: Remove later
                errorInterface.onCatch(ioe, Process.LOAD);
            } catch (ParseException pe) {
                pe.printStackTrace(); //TODO: Remove later
                errorInterface.onCatch(pe, Process.LOAD);
            } catch (NotOfficeXmlFileException noxfe) {
                noxfe.printStackTrace(); //TODO: Remove later
                errorInterface.onCatch(noxfe, Process.LOAD);
            }
        });
        t.start();
    }
}
