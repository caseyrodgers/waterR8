package com.waterR8.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ServerTester {
	
	private static final int TEST_REC_COUNT = 3;
	static Logger _logger = Logger.getLogger(ServerTester.class);
	
	public ServerTester(String host, int port) throws Exception {
		_logger.info("Starting test");
		Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        


            /** Print out random number of random records */
            int recordsToInclude = getRandomNumber(1,30);
            _logger.info("Test records to send: " + recordsToInclude);

            for(int i=0;i<recordsToInclude;i++) {
            	int testRec = getRandomNumber(0,  testEvents.length-1);
	            out.println(testEvents[testRec]);
            }
            
            /** end record */
            out.println("");
            out.flush();

            String line="";
            while((line = in.readLine())!=null) {
            	_logger.info("output: " + line);
            }
            
        } catch (IOException e) {
            return;
        }
        finally {
            out.close();
            in.close();
            socket.close();		
        }

	}
	
	private int getRandomNumber(int min, int max) {
		return min + (int)(Math.random() * (((max) - min) + 1));		
	}

	public static void main(final String as[]) {
		try {
			
			if(as.length < 2) {
				throw new Exception("usage: ServerTester server port");
			}
			
			for(int i=0;i<5;i++) {
				
				new Thread() {
					public void run() {
						try {
							new ServerTester(as[0], Integer.parseInt(as[1]));
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			
			_logger.error("Error testing server", e);
		}
	}
	
	static String testEvents[] = {
		"0c0a1c0f270e0be081f80017007c0b96b7",
		"15011d081b342087f04707e1029f0a5e9b",
		"15011d0906262087f04707e3029b0a5e9b",
		"15011d0914272087f04707e4029d0a7f9b",
		"15011d0b17292087f04707e5009e0a249b",
		"15011d0f252d2087f04707e6009f0a5d9b",
		"15011d10332d2087f04707e7029a0a539c",
		"15011e0b22242087f04707f9009c0a719a",
		"15011e1124072087f04707fa00990a0a9a",
		"1502011213062087f047081f02a80a419a",
		"150201161d2f2087f047082200940a1599",
		"1502020315022087f0470823008d0a179a",
		"BAD_RECORD"
	};

}
