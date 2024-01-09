package com.changenode;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class PreLaunchHandler {
    private JSONObject json;
    private File file;
    private String PATH = "src/main/resources/data/userdata.JSON";
    boolean notMyFirstTimeAroundHere() throws IOException, ParseException {

        //check if datafile exists
        //if so, check if valid address

        if (!checkFileAvailibility()) {
            return false;
        }
        json = getJSON();

        if (!json.containsKey("SavedLink")) {
            return false;
        }
        return true;
    }

    private JSONObject getJSON() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(PATH));
    }
    private boolean checkFileAvailibility() throws IOException {
        file = new File(PATH);
        File parent = file.getParentFile();
        if (!parent.exists()) { //TODO: check this, added in haste
            parent.mkdirs();
        }

        if (!file.exists()) {
            boolean res = file.createNewFile();
            System.out.println("PRELAUNCH: I haven't been here before - create result: ["+res+"]");

            FileWriter fw = new FileWriter(PATH);
            fw.write(new JSONObject().toJSONString());
            fw.flush();
            fw.close();

            return false;
        } else {
            return true;
        }
    }
}
