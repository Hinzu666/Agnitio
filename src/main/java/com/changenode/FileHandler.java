package com.changenode;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    private File file;
    private String PATH;
    public FileHandler (String filePath) {
        file = new File(filePath);

    }

    int triesTimeout = 0;
    public int saveLinkToJSON(String link) {
        int res = checkFileExists();
        if (res == 401) {
            if (triesTimeout > 3) {
                return res;
            }
            triesTimeout++;
            saveLinkToJSON(link);
        }
        triesTimeout = 0;

        JSONObject json;
        try {
            json = getJSON();
        } catch (Exception e) {
            e.printStackTrace();
            return 402;
        }

        json.put("SavedLink", link);

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(json.toJSONString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 403;
        }

        return 200;
    }

    public String getLinkFromJSON() throws IOException, ParseException{

        checkFileExists();

        JSONObject json = getJSON();
        if (json.containsKey("SavedLink")) {
            return (String)json.get("SavedLink");
        }
        return "404"; //TODO: make sure this value is checked for
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

    //404: no value
    //403: couldn't write to file
    //402: JSON parser error
    //401: couldn't create file
    //201: new file created
    //202: file exists
    private JSONObject getJSON() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(file));
    }
    private int checkFileExists() {
        if (!file.exists()) {
            try {
                file.createNewFile();

                FileWriter fw = new FileWriter(file);
                fw.write(new JSONObject().toJSONString());
                fw.flush();
                fw.close();

                return 201;
            } catch (Exception e) {
                e.printStackTrace();
                return 401;
            }
        }
        return 202;
    }

}
