import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class LogServer {
    public static void main(String[] args) {
        try {
            String currentDir = Paths.get("").toAbsolutePath().toString();
            new File(currentDir + "/logs").mkdir();
            ServerSocket serversocket = new ServerSocket(4444);
            System.out.println("Server running on " + serversocket.getLocalSocketAddress());

            while (true) {
                Socket socket = serversocket.accept();
                System.out.println("New Connection from: " + socket.getInetAddress() + socket.getPort());
                new Thread(new LogClientHandler(socket, currentDir + "/logs/logfile.txt")).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}