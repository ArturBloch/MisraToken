package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllNodeManager {

	static String[] nodeColors = new String[]{"\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m",
	                                          "\u001B" + "[35m", "\u001B[36m", "\u001B[37m"};

	public AllNodeManager() {}

	public static void main(String[] args) {
		int numberOfNodes = Integer.parseInt(args[0]);
		double chanceToLosePing = Double.parseDouble(args[1]);
		double chanceToLosePong = Double.parseDouble(args[2]);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfNodes);
		for (int i = 0; i < numberOfNodes; i++) {
			executorService.execute(new Node(i, numberOfNodes, chanceToLosePing, chanceToLosePong, nodeColors[i % nodeColors.length]));
		}
	}
}
