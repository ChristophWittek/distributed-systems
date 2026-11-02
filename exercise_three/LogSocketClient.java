import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.net.ssl.SSLSocketFactory;

public class LogSocketClient {
    public static void main(String[] args) {
        try {
            String host = "localhost";
            int port = 4444;
            //String path = "/";

            Socket socket = new Socket(host, port);
            // Socket socket = SSLSocketFactory.getDefault().createSocket(host, port);

            socket.setSoTimeout(10_000);
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            send(writer, LogProtos.Severity.INFO, "Testinfo");
            send(writer, LogProtos.Severity.WARN, "Testwarnung");
            send(writer, LogProtos.Severity.ERROR, "Testerror");
            writer.flush();
            socket.shutdownOutput();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Server said: " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void send(PrintWriter writer, LogProtos.Severity severity, String text) throws Exception {
        LogProtos.LogMessage msg = LogProtos.LogMessage.newBuilder()
                .setTimestampMillis(System.currentTimeMillis())
                .setSource("HttpSocketClientBuffer@" + InetAddress.getLocalHost().getHostAddress())
                .setSeverity(severity)
                .setText(text)
                .build();
        writer.println(Base64.getEncoder().encodeToString(msg.toByteArray()));
    }
}