package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MisraToken extends Remote{
	void take(int token)throws RemoteException;
}