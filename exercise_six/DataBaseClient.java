//package de.fhwedel.distributedSystems;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class DataBaseClient {

    private static DataBase readStubFromFile(String fileName)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fis);
        DataBase remoteObj = (DataBase) in.readObject();
        in.close();
        return remoteObj;
    }

    public static void main(String[] args) throws Exception {
        DataBase db = readStubFromFile("database.stub");

        // 2b: Tabelle aus Übung 4 eintragen (Platzhalter!)
        db.addRecord(4101, "Appen");
        db.addRecord(4102, "Ahrensburg");
        db.addRecord(4103, "Wedel");
        db.addRecord(4104, "Aumühle");
        db.addRecord(4105, "Seevetal");
        db.addRecord(4106, "Quickborn");

        System.out.println("4103: " + db.getRecord(4103));
        System.out.println("4107: " + db.getRecord(4107));

        // 2c
        System.out.println("Size: " + db.getSize());

        // 3a
        System.out.println("Index von 'Wedel': " + db.getIndex("Wedel"));

        // 3b
        DBResult r = db.getRecordObj(4103);
        if (r != null) {
            System.out.println("Obj: " + r.getKey() + " -> " + r.getValue());
        }
    }
}