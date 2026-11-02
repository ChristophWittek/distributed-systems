
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Base64;

class LogClientHandler implements Runnable {
    private final Socket socket;
    private final Path logFile;

    LogClientHandler(Socket socket, String logFile) {
        this.socket = socket;
        this.logFile = Path.of(logFile);
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String line;
            while ((line = reader.readLine()) != null) {
                LogProtos.LogMessage msg = LogProtos.LogMessage.parseFrom(Base64.getDecoder().decode(line));
                writeToFile(format(msg));
                writer.println("OK: " + msg.getSeverity() + " gespeichert");
            }

            this.socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String format(LogProtos.LogMessage m) {
        String source = m.hasSource() ? m.getSource() : "unknown";
        String text = m.getText().replaceAll("[\\r\\n]+", " ");
        return Instant.ofEpochMilli(m.getTimestampMillis()) + " [" + m.getSeverity() + "] " + source + ": " + text;
    }

    private void writeToFile(String line) throws IOException {
        Files.writeString(logFile, line + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println("Logged: " + line);
    }
}