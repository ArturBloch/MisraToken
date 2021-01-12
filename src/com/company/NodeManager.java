package com.company;

import java.rmi.RemoteException;

public class NodeManager{

    public static double chanceToLosePing = 0;
    public static double chanceToLosePong = 0;

    public NodeManager() {}

    public static void main(String[] args) throws RemoteException {
        int currentNodeId = Integer.parseInt(args[0]);
        int numberOfNodes = Integer.parseInt(args[1]);
        chanceToLosePing = Double.parseDouble(args[2]);
        chanceToLosePing = Double.parseDouble(args[3]);
        Node node = new Node(currentNodeId, numberOfNodes, chanceToLosePing, chanceToLosePong);
        node.setupNode();
    }
}
