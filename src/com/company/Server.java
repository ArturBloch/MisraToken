package com.company;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Counter {

    int counter = 0;

    public Server() {}

    public void add() throws RemoteException {
        counter++;
    }

    public void subtract() throws RemoteException {
        counter--;
    }

    public int get() throws RemoteException {
        return counter;
    }

    public static void main(String args[]) {

        try {
            Server obj = new Server();
            Counter stub = (Counter) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Counter", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
