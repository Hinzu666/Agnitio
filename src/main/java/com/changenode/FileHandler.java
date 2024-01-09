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

    private File file;
    private String PATH;
    public FileHandler (String filePath) {
        file = new File(filePath);

    }
    public Exception saveLinkToJSON(String link) {
        Exception res = checkFileExists();

        JSONObject json;
        try {
            json = getJSON();
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }

        json.put("SavedLink", link);

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(json.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return e;
        }

        return null;
    }

    public String getLinkFromJSON(LogHandler logger) throws IOException, ParseException{

        Exception res = checkFileExists();
        if (res != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error\n"+res.toString());
                alert.showAndWait();
            });
        }

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

    private JSONObject getJSON() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(file));
    }
    private Exception checkFileExists() {

        try {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
        } catch (Exception e) { //TODO: check this, added in haste
            return e;
        }


        if (!file.exists()) {
            try {
                file.createNewFile();

                FileWriter fw = new FileWriter(file);
                fw.write(new JSONObject().toJSONString());
                fw.flush();
                fw.close();

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }
        return null;
    }

}
