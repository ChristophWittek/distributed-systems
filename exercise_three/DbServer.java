import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DbServer {
    static final Map<Integer, String> DB = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        try {
            // String currentDir = Paths.get("").toAbsolutePath().toString();
            // new File(currentDir + "/database").mkdir();
             Map<Integer, String> db = new ConcurrentHashMap<>();
            ServerSocket serversocket = new ServerSocket(4444);
            System.out.println("Server running on " + serversocket.getLocalSocketAddress());

            while (true) {
                Socket socket = serversocket.accept();
                System.out.println("New Connection from: " + socket.getInetAddress() + socket.getPort());
                new Thread(new DbClientHandler(socket, db)).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}