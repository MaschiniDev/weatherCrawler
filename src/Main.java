import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            //Dates
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyy"));
            String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy"));

            ArrayList listTemps = new ArrayList();
            ArrayList listDates = new ArrayList();
            ArrayList listMonths = new ArrayList();

            File jFile = new File(year + ".json");

            HashMap<String, HashMap> months = new HashMap<>();
            HashMap<String, Object> days = new HashMap<>();

            //TODO Maps
            try {
                if (jFile.exists()) {
                    Object obj = new JSONParser().parse(new FileReader(year + ".json"));
                    JSONObject jMonths = (JSONObject) obj;
                    JSONObject jDays;

                    listMonths.addAll(jMonths.keySet());

                    for (int i = 0; i < jMonths.size(); i++) {
                        jDays = (JSONObject) jMonths.get(listMonths.get(i));
                        System.out.println(listMonths.get(i));

                        for (int j = 0; j < jDays.size(); j++) {
                            days.put(Integer.toString(j + 1), jDays.get(Integer.toString(j + 1)));
                            System.out.println((j + 1 + ". ") + days.get(Integer.toString(j + 1)));
                        }
                        months.put(listMonths.get(i).toString(), days);

                    }
                    //System.out.println(months.get("FEB").get("1"));

                    if (months.get("FEB") != null)
                        System.out.println("found");

                } else {
                    PrintWriter writer = new PrintWriter(year + ".json", "UTF-8");
                    writer.print("{\"JAN\":{\"1\":{\"01.01.17\":\"10\"}},\"FEB\":{\"1\":{\"01.02.17\":\"25\"}},\"MAR\":{\"1\":{\"01.03.17\":\"30\"}},\"APR\":{\"1\":{\"01.02.17\":\"25\"}},\"MAY\":{\"1\":{\"01.03.17\":\"30\"}},\"JUN\":{\"1\":{\"01.02.17\":\"25\"}},\"JUL\":{\"1\":{\"01.03.17\":\"30\"}},\"AUG\":{\"1\":{\"01.02.17\":\"25\"}},\"SEP\":{\"1\":{\"01.03.17\":\"30\"}},\"OCT\":{\"1\":{\"01.02.17\":\"25\"}},\"NOV\":{\"1\":{\"01.03.17\":\"30\"}},\"DEC\":{\"1\":{\"01.02.17\":\"25\"}}}");
                }
            } catch (ParseException pe) {
                pe.printStackTrace();
            }



            File f = new File(date + ".txt");
            if (!f.exists()) {
                //parse website
                Document rawWebsite = Jsoup.connect("https://weather.com/de-DE/wetter/10tage/l/GMXX5318:1:GM").timeout(9001).get();
                //TODO Generate new File if no exist
                PrintWriter file = new PrintWriter(date + ".txt", "UTF-8");

                //parse temps
                for (int i = 0; i < 29; i++) {
                    listTemps.add(rawWebsite.select(".temp").select("span[class=\"\"]").get(i).text());
                }
                for (int i = 0; i < 29; i++) {
                    if (i < 15) {
                        listTemps.remove(i);
                    }
                }

                //parse dates
                for (int i = 0; i < rawWebsite.getElementsByClass("day-detail").size(); i++) {
                    listDates.add(rawWebsite.getElementsByClass("day-detail").get(i).text());
                }
                listDates.remove(0);

                String headline = "Daten vom: " + date;
                file.println(headline);
                System.out.println(headline);

                for (int i = 0; i < listDates.size(); i++) {
                    String body = listDates.get(i) + ": " + listTemps.get(i);
                    file.println(body);
                    System.out.println(body);
                }

                file.close();
            } else {
                System.out.println("File already exist!");
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
