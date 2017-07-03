import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            ArrayList listTemps = new ArrayList();
            ArrayList listDates = new ArrayList();

            //Datum Heute
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyy");
            String date = LocalDateTime.now().format(dtf);

            File f = new File(date + ".txt");
            if (!f.exists()) {
                //parse website
                Document rawWebsite = Jsoup.connect("https://weather.com/de-DE/wetter/10tage/l/GMXX5318:1:GM").timeout(9001).get();
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
