import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    static void welcome() {
        System.out.println("########################");
        System.out.println("#    by Malte Schink   #");
        System.out.println("#       (c) 2017       #");
        System.out.println("########################");
        System.out.println("\n Choose Action:");
        System.out.println("[U]pdate Data \n" +
                           "[E]xport Data");
    }

    static Document website(String url) {
        Document raw = null;
        try {
            raw = Jsoup.connect(url).timeout(9001).get();
        } catch (IOException ioe) {
            System.out.println("> Connection Timeout \n> Please Restart");
            System.exit(0);
        }
        return  raw;
    }


    public static void main(String[] args) {
        //start
        welcome();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String action = reader.readLine();

            if (action.contains("u") || action.contains("U")) {
                update();
            } else if (action.contains("e") || action.contains("E")) {
                System.out.println("Choose a day \n Example: 5. AUG 2017");
                export(reader.readLine().replace(".", "").replace(" ", "").toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void export(String key) {
        System.out.println("> Export started");
        HashMap<String, HashMap<String, String>> year = new HashMap<>();
        HashMap<String, String> day = new HashMap<>();

        if (new File("temperaturen.json").exists()) {
            year = (HashMap) rJSON("temperaturen.json");
        } else {
            System.out.println("> No availabale Data (File Not Found)");
        }

        if (year.containsKey(key)) {
            try {
                day = year.get(key);
                List<String> keys = Arrays.asList(day.keySet().toArray(new String[day.keySet().size()]));
                System.out.println(keys);
                BufferedWriter writer = new BufferedWriter(new FileWriter(key + ".csv"));

                for (int i = 0; i < day.keySet().size(); i++) {
                    String[] temps = day.get(keys.get(i)).replace("°", "").split("/");
                    writer.write(keys.get(i) + ";" + temps[0] + ";"+ temps[1] + ";");
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (!year.containsKey(key)) {
            boolean workingKey = false;
            System.out.println("> No availabale Data (date is Wrong)\n" + key);
            while (!workingKey) {
                try {
                    System.out.println("Enter new date: ");
                    key = new BufferedReader(new InputStreamReader(System.in)).readLine().replace(".", "").replace(" ", "").toUpperCase();
                    if (year.containsKey(key)) {
                        workingKey = true;
                    } else if (!year.containsKey(key)) {
                        System.out.println("Try again!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            export(key);
        }
    }

    static void update() {
        System.out.println("> Update started");
        Document raw = website("https://weather.com/de-DE/wetter/10tage/l/GMXX5318:1:GM");
        HashMap<String, HashMap<String, String>> year = new HashMap<>();

        System.out.println("> Checking for existing File");
        if (new File("temperaturen.json").exists()) {
            year = (HashMap) rJSON("temperaturen.json");
            System.out.println("> Parse Data from File");
        }

        System.out.println("> Adding Infos");
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
        System.out.println("> Save Data in File");
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
