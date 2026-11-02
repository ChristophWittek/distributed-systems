import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class DbSocketClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4444)) {
            socket.setSoTimeout(10_000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            addRecord(in, out, 4101, "Appen");
            addRecord(in, out, 4102, "Ahrensburg");
            addRecord(in, out, 4103, "Wedel");
            addRecord(in, out, 4104, "Aumühle");
            addRecord(in, out, 4105, "Seevetal");
            addRecord(in, out, 4106, "Quickborn");

            getRecord(in, out, 4103);
            getRecord(in, out, 4107);

            getSize(in, out);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void addRecord(BufferedReader reader, PrintWriter writer, int index, String record) throws IOException {
        MessageLite message = DbProtos.RPC_Request.newBuilder()
                .setOperation(DbProtos.RPC_Request.Operation.ADD_RECORD)
                .setAddRecordArg(DbProtos.AddRecordRequest.newBuilder().setIndex(index).setRecord(record))
                .build();
        send(writer, message);

        DbProtos.AddRecordResponse resp = DbProtos.AddRecordResponse.parseFrom(receive(reader));
        System.out.println("Client sends 'addRecord(" + index + ", " + record + ")' -> " + (resp.getSuccess() ? "OK" : "Fehler"));
    }

    private static void getRecord(BufferedReader in, PrintWriter out, int index) throws IOException {
        MessageLite message =  DbProtos.RPC_Request.newBuilder()
                .setOperation(DbProtos.RPC_Request.Operation.GET_RECORD)
                .setGetRecordArg(DbProtos.GetRecordRequest.newBuilder().setIndex(index))
                .build();
        send(out, message);

        DbProtos.GetRecordResponse resp = DbProtos.GetRecordResponse.parseFrom(receive(in));
        System.out.println("Client sends 'getRecord(" + index + ")' -> " + (resp.getFound() ? resp.getRecord() : "nicht gefunden"));
    }

    private static void getSize(BufferedReader in, PrintWriter out) throws IOException {
        MessageLite message = DbProtos.RPC_Request.newBuilder()
                .setOperation(DbProtos.RPC_Request.Operation.GET_SIZE)
                .setGetSizeArg(DbProtos.GetSizeRequest.newBuilder())
                .build();
        send(out, message);

        DbProtos.GetSizeResponse resp = DbProtos.GetSizeResponse.parseFrom(receive(in));
        System.out.println("Client sends 'getSize()' -> " + resp.getSize());
    }

    private static void send(PrintWriter out, MessageLite message) {
        out.println(Base64.getEncoder().encodeToString(message.toByteArray()));
    }

    private static byte[] receive(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Server closed connection");
        }
        return Base64.getDecoder().decode(line);
    }
}