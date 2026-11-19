// package de.fhwedel.distributedSystems;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class DataBaseImpl implements DataBase {

    private final Map<Integer, String> records = new HashMap<>();

    @Override
    public synchronized String getRecord(int index) throws RemoteException {
        return records.get(index);
    }

    @Override
    public synchronized void addRecord(int index, String record) throws RemoteException {
        records.put(index, record);
    }

    @Override
    public synchronized int getSize() throws RemoteException {
        return records.size();
    }

    @Override
    public synchronized int getIndex(String record) throws RemoteException {
        for (Map.Entry<Integer, String> e : records.entrySet()) {
            if (e.getValue().equals(record)) {
                return e.getKey();
            }
        }
        return -1;
    }

    @Override
    public synchronized DBResult getRecordObj(int index) throws RemoteException {
        String value = records.get(index);
        if (value == null) {
            return null;
        }
        DBResult result = new DBResult();
        result.setKey(index);
        result.setValue(value);
        return result;
    }

    private static void writeStubToFile(String fileName, Remote stub)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(stub);
        out.close();
    }

    public static void main(String[] args) throws Exception {
        DataBaseImpl db = new DataBaseImpl();
        DataBase stub = (DataBase) UnicastRemoteObject.exportObject(db, 0);
        writeStubToFile("database.stub", stub);
        System.out.println("DataBase-Server läuft, Stub in database.stub geschrieben.");
    }
}