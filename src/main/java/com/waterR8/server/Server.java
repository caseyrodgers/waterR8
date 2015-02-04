package com.waterR8.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.waterR8.dao.DataRecordDao;
import com.waterR8.dao.SpringManager;
import com.waterR8.util.DataRecordParser;


public class Server {

	public static final int PORT = 4444;

	public static final String SERVER_NAME = "ec2-54-215-106-72.us-west-1.compute.amazonaws.com";

	private static int port = PORT, maxConnections = 0;
	
	static Logger _logger = Logger.getLogger(Server.class);

	public static void main(String[] args) {
		int i = 0;
		ServerSocket listener = null;
		try {
			
			DataRecordDao.getInstance();
			
			listener = new ServerSocket(port);
			Socket server;

			_logger.info("WaterR8 Data Collector Server running on port " + port);
			while ((i++ < maxConnections) || (maxConnections == 0)) {
				_logger.info("WaterR8 Server listening for connection #" + maxConnections);
				server = listener.accept();
				DoComms conn_c = new DoComms(server);
				Thread t = new Thread(conn_c);
				t.start();
			}
		} catch (Exception e) {
			System.out.println("Exception on socket listen: " + e);
			e.printStackTrace();
		}
		finally {
			try {
				listener.close();
			}
			catch(IOException ie) {
				_logger.error("Closing server", ie);
			}
		}
		
		_logger.info("WaterR8 Data Collector closed");
	}

}

class DoComms implements Runnable {
	private Socket server;
	private String _line;

	static Logger _logger = Logger.getLogger(DoComms.class);
	
	DoComms(Socket server) {
		this.server = server;
	}

	public void run() {
		PrintWriter out = null;
		try {
			// Get input from the client
			BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream(),true);

			List<String> dataRecordStrings = new ArrayList<String>();
			
			_logger.info("Reading from socket: waiting");
			while ((_line = br.readLine()) != null) {
			
			    // _logger.info("Reading from socket: read line " + dataRecordStrings.size());
				 if(_line.equals(".") || _line.length() == 0) {
					 break;
				 }
				dataRecordStrings.add(_line);
			}
			_logger.info("read data record strings: " + dataRecordStrings.size());
			
			DataRecordDao.getInstance().addDataRecords(new DataRecordParser().parseDataRecords(dataRecordStrings));
			
			out.println("OK");
		} catch (Exception e) {
			_logger.error("Error processing data records", e);
			e.printStackTrace();
			if(out != null) {
				out.println("Error occurred: " + e);
			}
		}
		finally {
			try {
				server.close();
			}
			catch(IOException ioe) {
				_logger.error("Error closing thread server", ioe);
				ioe.printStackTrace();
			}
		}
	}
}