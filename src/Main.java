import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main {

    static public Document website(String url) {
        Document raw = null;
        try {
            raw = Jsoup.connect(url).timeout(9001).get();
        } catch (IOException ioe) {
            System.out.println("Connection Timeout");
            System.exit(1);
        }
        return  raw;
    }


    public static void main(String[] args) {
        System.console();
        System.out.println("DataCrawler gestartet");
        Document raw = website("https://weather.com/de-DE/wetter/10tage/l/GMXX5318:1:GM");
        HashMap<String, HashMap<String, String>> year = new HashMap<>();

        System.out.println("Checking for existing File");
        if (new File("temperaturen.json").exists()) {
            year = (HashMap) rJSON("temperaturen.json");
            System.out.println("Parse Data from File");
        }

        System.out.println("Adding Infos");
        for (int i = 0; i < raw.getElementsByClass("day-detail").size(); i++) {
            HashMap<String, String> temps = new HashMap();

            String temperatur = new StringBuilder(raw.select(".temp").get(i+1).text()).reverse().insert(3, "/").reverse().toString();
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyy"));

            temps.put(today,temperatur);


            String key = raw.select(".day-detail").get(i).text().replace(". ", "") + LocalDateTime.now().format((DateTimeFormatter.ofPattern("yyy")));

            if (!year.containsKey(key)) {
                year.put(key,temps);
            } else if (year.containsKey(key)) {
                HashMap newEntry = year.get(key);
                newEntry.put(today, temperatur);
                year.replace(key, newEntry);
            }
        }
        System.out.println("Save Data in File");
        wJSON(year);
    }

    static void wJSON(HashMap map) {
        JSONObject jsonfile = new JSONObject();
        jsonfile.putAll(map);

        try {
            PrintWriter fileWriter = new PrintWriter(new File("temperaturen.json"), "UTF-8");
            fileWriter.println(jsonfile);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static JSONObject rJSON(String file) {
        JSONObject json = null;

        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
