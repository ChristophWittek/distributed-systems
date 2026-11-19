//package de.fhwedel.distributedSystems;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataBase extends Remote {
    String getRecord(int index) throws RemoteException;
    void addRecord(int index, String record) throws RemoteException;
    int getSize() throws RemoteException;

    // 3a
    int getIndex(String record) throws RemoteException;

    // 3b
    DBResult getRecordObj(int index) throws RemoteException;
}