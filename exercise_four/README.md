# Aufgabe 4: gRPC-Datenbank

Einfacher Datenbank-Server mit gRPC und Protocol Buffers (Java).
Der Server bietet `getRecord`, `addRecord` und `getSize` an, der Client speichert die 6 Orte, liest 4103 und 4107 und ruft `getSize` auf.

Dieser Ordner ist ein eigenes **Gradle-Projekt**. Gradle lädt die gRPC-Bibliotheken automatisch herunter und generiert den Java-Code aus der `.proto`-Datei. `protoc` muss **nicht** installiert sein.

## Dateien

| Datei | Inhalt |
|---|---|
| `build.gradle` | Bibliotheken (gRPC, Protobuf) und Code-Generierung |
| `src/main/proto/Database.proto` | Nachrichten und `service Database` mit den drei `rpc`-Methoden |
| `src/main/java/.../DatabaseServer.java` | Server (Port 9091) |
| `src/main/java/.../DatabaseClient.java` | Client mit den Testaufrufen |

Der generierte Code landet in `build/generated/sources/proto/main/` und wird **nicht** eingecheckt.

## Voraussetzungen

- **JDK 17 oder neuer** (getestet mit JDK 21)
  Prüfen im Terminal: `java -version`
- **VS Code** mit den Extensions:
  - **Extension Pack for Java** (Microsoft)
  - **Gradle for Java** (Microsoft)

## Einrichten in VS Code

1. Das Gruppen-Repo in VS Code öffnen (oder direkt den Ordner `exercise_four`).
2. Die Java-Extension erkennt die `build.gradle` und importiert das Projekt. Unten rechts erscheint eventuell eine Meldung dazu, einfach bestätigen. Das kann beim ersten Mal ein bis zwei Minuten dauern, weil die Bibliotheken heruntergeladen werden.
3. **Einmal bauen**, damit der Code aus der `.proto` generiert wird. Im Terminal im Ordner `exercise_four`:

   macOS / Linux:
   ```
   ./gradlew build
   ```
   Windows:
   ```
   gradlew.bat build
   ```
   Am Ende sollte `BUILD SUCCESSFUL` stehen.

   Alternativ in VS Code links auf das **Gradle-Symbol (Elefant)** → `exercise_four` → **Tasks → build → build**.

4. Falls danach noch rote Fehler im Editor sind (z.B. `DatabaseGrpc cannot be resolved`):
   `Strg/Cmd + Shift + P` → **"Java: Clean Java Language Server Workspace"** → Neustart bestätigen.

## Starten

Server und Client sind **zwei getrennte Programme**. Reihenfolge:

1. **`DatabaseServer.java`** öffnen → über der `main`-Methode auf **Run** klicken.
   Ausgabe: `Server läuft auf Port 9091`. Laufen lassen.
2. **`DatabaseClient.java`** öffnen → **Run**.

Erwartete Ausgabe des Clients:
```
4103 → Wedel
4107 → nicht gefunden
Größe: 6
```

## Wenn etwas nicht klappt

| Problem | Lösung |
|---|---|
| `./gradlew: Permission denied` (macOS/Linux) | einmal `chmod +x gradlew` ausführen |
| `gradlew` nicht gefunden | im richtigen Ordner? Terminal muss in `exercise_four` sein |
| `DatabaseGrpc` / `RpcResponse` rot | erst bauen (Schritt 3), dann ggf. Java-Workspace neu laden (Schritt 4) |
| `Address already in use` beim Server | Server läuft schon, alten Prozess stoppen |
| Client: `UNAVAILABLE` / `Connection refused` | Server wurde nicht gestartet |
| Fehler zu Java-Version | JDK 17 oder neuer installieren und in VS Code auswählen |

## Unterschied zu Aufgabe 3 (für die Diskussion)

| Aufgabe 3 (RPC von Hand) | Aufgabe 4 (gRPC) |
|---|---|
| `RpcRequest` mit `Operation`-Enum | `service Database { rpc ... }` |
| `switch` über die Operation im Server | eine eigene Methode pro `rpc` |
| Sockets, Threads, Nachrichtengrenzen selbst gebaut | übernimmt gRPC (HTTP/2) |
| eigene `call()`-Methode im Client | generierter Stub: `stub.getRecord(...)` |
