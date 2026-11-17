package exercise_five_json;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonRpcClient {

    static BufferedReader in;
    static PrintWriter out;
    static int nextId = 1;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9092)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // Records schreiben
            Map<Integer, String> cities = Map.of(4101, "Appen", 4102, "Ahrensburg", 4103, "Wedel",
                    4104, "Aumühle", 4105, "Seevetal", 4106, "Quickborn");
            for (var e : cities.entrySet()) {
                call("addRecord", e.getKey() + ", \"" + e.getValue() + "\"");
            }

            // Records lesen
            call("getRecord", "4103");
            call("getRecord", "4107");

            // Größe
            call("getSize", "");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Request als eine Zeile senden, Antwort als eine Zeile lesen
    static String call(String method, String params) throws IOException {
        String request = "{\"jsonrpc\": \"2.0\", \"method\": \"" + method + "\", \"params\": [" + params + "], \"id\": " + nextId++ + "}";
        out.println(request);
        String response = in.readLine();
        System.out.println("→ " + request);
        System.out.println("← " + response);
        return response;
    }
}