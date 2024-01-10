package com.changenode;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class FileHandler {

    private final File file;
    private String PATH;
    public FileHandler (String filePath) {
        file = new File(filePath);
    }
    public void saveLinkToJSON(String link) throws IOException, ParseException {
        checkFileExists();

        JSONObject json;
        json = getJSON();

        json.put("SavedLink", link);

        FileWriter fw = new FileWriter(file);
        fw.write(json.toJSONString());
        fw.flush();
        fw.close();
    }

    public String getLinkFromJSON(LogHandler logger) throws IOException, ParseException{

        checkFileExists();

        logger.log("File created");

        JSONObject json = getJSON();
        if (json.containsKey("SavedLink")) {
            return (String)json.get("SavedLink");
        }
        return "404";
    }
    public double[] getDimensionsFromJSON() throws IOException, ParseException {
        checkFileExists();

        JSONObject json = getJSON();

        double[] ret = new double[2];

        if (json.containsKey("preferredWidth")) {
            ret[0] = (double) json.get("preferredWidth");
        }
        if (json.containsKey("preferredHeight")) {
            ret[1] = (double) json.get("preferredHeight");
        }

        return ret;
    }
    public void saveDimensionsToJSON(double width, double height) throws IOException, ParseException {
        checkFileExists();

        JSONObject json = getJSON();
        json.put("preferredWidth", width);
        json.put("preferredHeight", height);

        FileWriter fw = new FileWriter(file);
        fw.write(json.toJSONString());
        fw.flush();
        fw.close();
    }

    public void deleteEverything() {
        //TODO: this
    }

    private JSONObject getJSON() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(file));
    }
    private void checkFileExists() throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean res = parent.mkdirs();
        }
        if (!file.exists()) {
            boolean res = file.createNewFile();

            FileWriter fw = new FileWriter(file);
            fw.write(new JSONObject().toJSONString());
            fw.flush();
            fw.close();
        }
    }

}
