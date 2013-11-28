package com.michalsznajder.android.master.tiltmouse;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.microedition.io.StreamConnection;

public class ControlThread implements Runnable {
	
	private StreamConnection mConnection;
	
	//touchpad
	public static final int MESSAGE_MOVE = 7;
    public static final int MESSAGE_LEFT_CLICK = 8;
    public static final int MESSAGE_LEFT_CLICK_DOWN = 9;
    public static final int MESSAGE_LEFT_CLICK_UP = 10;
    public static final int MESSAGE_RIGHT_CLICK = 11;
    
    //tilt mouse
    public static final int MESSAGE_MOVE_ACC_GRAVITY = 15;
    
    //gyro mouse
    public static final int MESSAGE_MOVE_GYRO_RAW_XYZ = 19;
    
    //sensor fusion mouse
    public static final int MESSAGE_MOVE_SFMOUSE = 21;
        
    private Robot mRobot;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    
	public ControlThread(StreamConnection connection)
	{
		mConnection = connection;
	}
	
	/**
	 * Inicjalizacja pól 
	 * 
	 */
    private void init() throws AWTException, IOException {
        mRobot = new Robot();
        System.out.println("Inicjalizacja java.awt.robot");
		inputStream = mConnection.openInputStream();
		objectInputStream = new ObjectInputStream(inputStream);
		System.out.println("Oczekiwanie na dyspozycje");    	
    }
    
    /**
	 * Uruchomienie inicjalizacji serwera oraz wykonywania dyspozycji 
	 * 
	 */
	@Override
	public void run() {
		try {
			init(); 
			while (true) {	
			Motion move = (Motion) objectInputStream.readObject();
			processCommand(move);
			}
        } catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	
	/**
	 * Przetworzenie otrzymanej informacji o ruchu
	 * @param informacja o ruchu z klienta systemu
	 */
	private void processCommand(Motion move) {
		try {
			switch (move.type) {
			
			//touchpad
			case MESSAGE_MOVE:
		    	PointerInfo pointerInfo = MouseInfo.getPointerInfo();
	        		
	        		if (pointerInfo != null)
	        		{
	        			Point point = pointerInfo.getLocation();
	        			
	        			if (point != null)
	        			{
	        				int x = point.x + move.x;
	        				int y = point.y + move.y;
	        				mRobot.mouseMove(x, y);
	        			}
	        		}
		            
		    		break;
		    		
		    	case MESSAGE_LEFT_CLICK:
		    		mRobot.mousePress(InputEvent.BUTTON1_MASK);
		    		mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
		    		
		    		break;
		    		
		    	case MESSAGE_LEFT_CLICK_DOWN:
		    		mRobot.mousePress(InputEvent.BUTTON1_MASK);
		    		
		    		break;
		    	
		    	case MESSAGE_LEFT_CLICK_UP:    		
		    		mRobot.mouseRelease(InputEvent.BUTTON1_MASK);
		    		
		    		break;
		    	
		    	case MESSAGE_RIGHT_CLICK:
		    		mRobot.mousePress(InputEvent.BUTTON3_MASK);
		    		mRobot.mouseRelease(InputEvent.BUTTON3_MASK);
		    		
		    		break;
				
		    		
		    	//tilt mouse
		    	case MESSAGE_MOVE_ACC_GRAVITY:
			    	PointerInfo pointerInfo2 = MouseInfo.getPointerInfo();
		        		
		        		if (pointerInfo2 != null)
		        		{
		        			Point point = pointerInfo2.getLocation();
		        			
		        			if (point != null)
		        			{
		        				int x = point.x - move.x * 3;
		        				int y = point.y - move.y * 3;
		        				mRobot.mouseMove(x, y);
		        			}
		        		}
			            
			    		break;
			    
			    //gyro mouse
		    	case MESSAGE_MOVE_GYRO_RAW_XYZ:
			    	PointerInfo pointerInfo3 = MouseInfo.getPointerInfo();
		        		
		        		if (pointerInfo3 != null)
		        		{
		        			Point point = pointerInfo3.getLocation();
		        			
		        			if (point != null)
		        			{
		        				int x = point.x + move.y * (-14);
		        				int y = point.y + move.x * (-14);
		        				mRobot.mouseMove(x, y);
		        			}
		        		}
			            
		    		break;
			
		    	//sensor fusion mouse
				case MESSAGE_MOVE_SFMOUSE:
			    	PointerInfo pointerInfo4 = MouseInfo.getPointerInfo();
		        		
		        		if (pointerInfo4 != null)
		        		{
		        			Point point = pointerInfo4.getLocation();
		        			
		        			if (point != null)
		        			{
		        				        				 	                       
		        				 int x = move.x;
		        				 int y = move.y;
		        				 mRobot.mouseMove(x, y);
		        					
		        			}
		        		}        
		        		break;
		    		
	    	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
