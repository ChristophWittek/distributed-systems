import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class HttpSocketClient {
    public static void main(String[] args) {
        try {
            // // Für Aufgabe 2.1 a)
            // String host = "www.fh-wedel.de";
            // int port = 443;
            // String path = "/";

            // // Für Aufgabe 2.1 c) Proxy
            // String host = "proxy.fh-wedel.de";
            // int port = 8080;
            // String path = "/";

            // Für Aufgabe 2.2 c)
            String host = "localhost";
            int port = 4444;
            String path = "/files";

            Socket socket = new Socket(host, port);
            //Socket socket = SSLSocketFactory.getDefault().createSocket(host, port);

            socket.setSoTimeout(10_000);

            String request = "GET " + path + " HTTP/1.1\r\n"
                            + "Host: " + host + "\r\n"
                            + "Connection: close\r\n"
                            + "\r\n";
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.print(request);
            writer.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Server said: " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}