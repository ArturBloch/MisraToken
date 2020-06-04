package com.company;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	private Client() {}

	public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[0];
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			Counter stub = (Counter) registry.lookup("Counter");
			for (int i = 0; i < 5; i++) {
				stub.add();
				int response = stub.get();
				System.out.println("response: " + i + " " + response);
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}