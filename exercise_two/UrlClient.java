import java.io.*;
import java.net.*;

public class UrlClient {
    public static void main(String[] args) {
        try {
            URL url = URI.create("https://www.fh-wedel.de").toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5_000);
            con.setReadTimeout(10_000);

            System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
            //con.getHeaderFields().forEach((k, v) -> System.out.println(k + ": " + v));

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}