import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;

import com.google.protobuf.MessageLite;

public class DbClientHandler implements Runnable {
    private final Socket socket;
    private final Map<Integer, String> db;

    DbClientHandler(Socket socket, Map<Integer, String> db) {
        this.socket = socket;
        this.db = db;
    }
    public void run() {
        try (this.socket) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = reader.readLine()) != null) {
                DbProtos.RPC_Request req = DbProtos.RPC_Request.parseFrom(Base64.getDecoder().decode(line));
                switch (req.getOperation()) {
                    case GET_RECORD -> {
                        int index = req.getGetRecordArg().getIndex();
                        String record = db.get(index);
                        MessageLite message = DbProtos.GetRecordResponse.newBuilder()
                                .setFound(record != null)
                                .setRecord(record != null ? record : "")
                                .build();
                        send(writer, message, this.socket);
                    }
                    case ADD_RECORD -> {
                        MessageLite message = DbProtos.AddRecordResponse.newBuilder()
                                .setSuccess(true)
                                .build();
                        DbProtos.AddRecordRequest arg = req.getAddRecordArg();
                        db.put(arg.getIndex(), arg.getRecord());
                        send(writer, message, this.socket);
                    }
                    case GET_SIZE -> {
                        MessageLite message = DbProtos.GetSizeResponse.newBuilder()
                                .setSize(db.size())
                                .build();
                        send(writer, message, this.socket);
                    }
                    default -> System.out.println("Unknown Operation: " + req.getOperation());
                }
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void send(PrintWriter writer, MessageLite message, Socket socket) {
        writer.println(Base64.getEncoder().encodeToString(message.toByteArray()));
        System.out.println("Message sent to Client " + socket.getInetAddress() + socket.getPort() + ": " + Base64.getEncoder().encodeToString(message.toByteArray()));
    }
}
