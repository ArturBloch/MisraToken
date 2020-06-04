package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Counter extends Remote{
	public void add()throws RemoteException;
	public void subtract()throws RemoteException;
	public int get()throws RemoteException;
}