package com.company;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Node implements MisraToken, Runnable {

	int numberOfNodes;

	int nodeId;
	int nextNodeId;
	int nextNodePort;
	int port;

	MisraToken nextNodeObj;
	boolean holdingPing;
	boolean holdingPong;

	double chanceToLosePing = 0;
	double chanceToLosePong = 0;

	int ping = 1;
	int pong = -1; // ping + pong should equal 1
	int m; // last token passed through

	private String color = "\u001B[37m";

	private final Random random = new Random();

	public Node(int nodeId, int numberOfNodes, double chanceToLosePing, double chanceToLosePong) {
		if (nodeId == 0) {
			holdingPing = true;
			holdingPong = true;
		}
		this.numberOfNodes    = numberOfNodes;
		this.nodeId           = nodeId;
		this.nextNodeId       = (nodeId + 1) % this.numberOfNodes;
		this.port             = 5000 + nodeId;
		this.nextNodePort     = 5000 + this.nextNodeId;
		this.chanceToLosePing = chanceToLosePing;
		this.chanceToLosePong = chanceToLosePong;
		System.out.println("chance to lose pong " + chanceToLosePong + " chance to lose ping " + chanceToLosePing);
	}

	public Node(int nodeId, int numberOfNodes, double chanceToLosePing, double chanceToLosePong, String color) {
		this(nodeId, numberOfNodes, chanceToLosePing, chanceToLosePong);
		this.color = color;
	}

	public void setupNode() throws RemoteException {
		exportCurrentNode();
		connectToNextNode();
		while(true){
			passToken();
		}
	}

	@Override public void take(int incToken) throws RemoteException {
		if (incToken > 0) {
			recvPing(incToken);
		} else if (incToken < 0) {
			recvPong(incToken);
		}
	}

	private synchronized void recvPing(int incToken) throws RemoteException {
		boolean oldPing = Math.abs(incToken) < Math.abs(m);
		if (oldPing) {
			printMessage("Got old ping " + incToken);
			return;
		}
		printMessage("Got ping " + incToken);
		holdingPing = true;
		ping        = incToken;
		if (holdingPong) {
			printMessage("---- Ping met Pong ----");
			incarnate();
		} else if (m == incToken) {
			printMessage("----Detected pong loss-----");
			regenerate(ping);
		}
	}

	private synchronized void recvPong(int incToken) throws RemoteException {
		boolean oldPong = Math.abs(incToken) < Math.abs(m);
		if (oldPong) {
			printMessage("Got old pong " + incToken);
			return;
		}

		printMessage("Got pong " + incToken);
		holdingPong = true;
		pong        = incToken;

		if (holdingPing) {
			printMessage("---- Pong met Ping ----");
			incarnate();
		} else if (incToken == m) { // this pong made the whole circle
			printMessage("----Detected ping loss----");
			regenerate(pong);
		}
	}

	private synchronized void passToken() throws RemoteException {
		if (holdingPong) {
			double rollTokenLoss = random.nextDouble();
			holdingPong = false;
			if (rollTokenLoss < chanceToLosePong) {
				printMessage("---- Simulated PONG loss ----");
				m = pong;
			} else {
				m = pong;
				printMessage("Sent pong " + pong);
				nextNodeObj.take(pong);
			}
		}

		if (holdingPing) {
			double rollTokenLoss = random.nextDouble();
			holdingPing = false;
			if (rollTokenLoss < chanceToLosePing) {
				printMessage("---- Simulated PING loss ----");
			} else {
				m = ping;
				printMessage("Sent ping " + ping);
				nextNodeObj.take(ping);
			}
		}
	}

	private synchronized void incarnate() {
		printMessage("------Tokens met incarnating-----");
		ping        = Math.abs(ping + 1);
		pong        = -ping;
		holdingPong = true;
		holdingPing = true;
	}

	private synchronized void regenerate(int value) {
		printMessage("------Regenerating token-----");
		ping        = Math.abs(value);
		pong        = -ping;
		holdingPong = true;
		holdingPing = true;
		incarnate();
	}

	private void exportCurrentNode() {
		boolean connected = false;
		while (!connected) {
			try {
				MisraToken stub = (MisraToken) UnicastRemoteObject.exportObject(this, 0);
				// Bind the remote object's stub in the registry
				Registry registry = LocateRegistry.createRegistry(port);
				registry.bind("MisraToken", stub);
				connected = true;
			} catch (Exception e) {
				System.err.println(nodeId + " couldn't export itself ");
			}
		}
		printMessage("Ready at port " + port);
	}

	private void connectToNextNode() {
		boolean connected = false;
		while (!connected) {
			try {
				Registry registry = LocateRegistry.getRegistry("localhost", nextNodePort);
				nextNodeObj = (MisraToken) registry.lookup("MisraToken");
				connected   = true;
			} catch (Exception e) {
				System.err.println(nodeId + " couldn't connect to " + nextNodeId);
			}
		}
		printMessage(String.format("Connected to next node %d at port %d\n", nextNodeId, nextNodePort));
	}

	private void printMessage(String msg) {
		System.out.printf(color + "Node %d:%d: %s\n", nodeId, port, msg);
	}

	@Override public void run() {
		exportCurrentNode();
		connectToNextNode();
		while(true){
			try {
				passToken();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
