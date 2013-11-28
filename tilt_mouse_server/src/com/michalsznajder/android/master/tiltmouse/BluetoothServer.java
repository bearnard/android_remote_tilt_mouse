package com.michalsznajder.android.master.tiltmouse;

public class BluetoothServer{
	
	public static void main(String[] args) {
		Thread serverThread = new Thread(new ServerThread());
		serverThread.start();
	}
}
