
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final String currentDir;

    ClientHandler(Socket socket, String currentDir) {
        this.socket = socket;
        this.currentDir = currentDir;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
            var writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            String requestLine = reader.readLine();
            if (requestLine == null) return;
            String[] req = requestLine.split(" ");
            String method = req[0];
            String path = req[1];

            int length = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    length = Integer.parseInt(line.substring(15).trim());
                }
            }

            // Routes
            if (method.equals("GET") && path.equals("/")) {
                String html = Files.readString(Paths.get(this.currentDir + "/exercise_two/index.html"));
                send(writer, "200 OK", "text/html; charset=utf-8", html);

            } else if (method.equals("GET") && path.equals("/files")) {
                String[] names = new File(this.currentDir + "/uploads").list();
                send(writer, "200 OK", "text/plain; charset=utf-8", "Bereits hochgeladene Dateien: \n" + String.join("\n", names));

            } else if (method.equals("PUT")) {
                String name = new File(URLDecoder.decode(path.substring(1), "UTF-8")).getName();
                FileOutputStream file = new FileOutputStream(this.currentDir + "/uploads/" + name);
                for (int i = 0; i < length; i++) {
                    file.write(reader.read());
                }
                file.close();
                System.out.println("Gespeichert: " + name);
                send(writer, "201 Created", "text/plain; charset=utf-8", "Gespeichert als " + name);

            } else {
                send(writer, "404 Not Found", "text/plain; charset=utf-8", "Nicht gefunden");
            }

            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void send(PrintWriter writer, String status, String type, String body) {
        int bytes = body.getBytes(StandardCharsets.UTF_8).length;
        writer.print("HTTP/1.1 " + status + "\r\n");
        writer.print("Content-Type: " + type + "\r\n");
        writer.print("Content-Length: " + bytes + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.print(body);
        writer.flush();
    }
}