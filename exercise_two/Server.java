import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class Server {
    public static void main(String[] args) {
        try {
            String currentDir = Paths.get("").toAbsolutePath().toString();
            new File(currentDir + "/uploads").mkdir();
            ServerSocket serversocket = new ServerSocket(4444);
            System.out.println("Server running on " + serversocket.getLocalSocketAddress());

            while (true) {
                Socket socket = serversocket.accept();
                System.out.println("New Connection from: " + socket.getInetAddress() + socket.getPort());
                new Thread(new ClientHandler(socket, currentDir)).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}



// Aufgabe 2.2 a) Öffne im Browser http://localhost:4444 und lade eine Datei hoch.

// Aufgabe 2.2 b) Öffne CMD -> telnet -> open localhost 4444 -> Eingabe folgender message:
    // Einfacher Verbindungstest:
        // GET /files HTTP/1.1
        // Host: localhost:4444
        // Connection: close

// Aufgabe 2.2 c) Öffne eine zweite VS Code Instanz -> Änderungen in HttpSocketClient.java (so wie dokumentiert) -> Starte Server und dann Client

// Aufgabe 2.4 d) 
    // Beide im selben Netzwerk einloggen
    // Server-Node nach IP-Adresse fragen (ipconfig)
    // Ip-Adresse + Port in Browser aufrufen z.B. http://192.168.178.42:4444 oder mit telnet