package exercise_five_json;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonRpcServer {

    static Map<Integer, String> db = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9092);
        System.out.println("Server läuft auf Port 9092");

        while (true) {
            Socket client = server.accept();
            new Thread(() -> handle(client)).start();
        }
    }

    private static void handle(Socket client) {
        try (client) {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);

            // eine JSON-Nachricht pro Zeile
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("→ " + request);
                String response = process(request);
                System.out.println("← " + response);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Request auswerten und Antwort bauen
    private static String process(String request) {
        String method = find(request, "\"method\"\\s*:\\s*\"(\\w+)\"");
        String id = find(request, "\"id\"\\s*:\\s*(\\d+)");
        String params = find(request, "\"params\"\\s*:\\s*\\[(.*?)\\]");
        String[] p = params == null ? new String[0] : params.split(",", 2);

        switch (method == null ? "" : method) {
            case "addRecord": {
                int key = Integer.parseInt(p[0].trim());
                String record = p[1].trim().replace("\"", "");
                db.put(key, record);
                return result(id, "true");
            }
            case "getRecord": {
                String record = db.get(Integer.parseInt(p[0].trim()));
                if (record == null) return error(id, 1, "Record not found");
                return result(id, "\"" + record + "\"");
            }
            case "getSize":
                return result(id, String.valueOf(db.size()));
            default:
                return error(id, -32601, "Method not found");
        }
    }

    // erstes Vorkommen eines Musters im Text finden
    private static String find(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private static String result(String id, String value) {
        return "{\"jsonrpc\": \"2.0\", \"result\": " + value + ", \"id\": " + id + "}";
    }

    private static String error(String id, int code, String message) {
        return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": " + code + ", \"message\": \"" + message + "\"}, \"id\": " + id + "}";
    }
}