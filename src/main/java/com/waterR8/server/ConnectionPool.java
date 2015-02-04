package com.waterR8.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import com.waterR8.util.WaterR8Properties;

public class ConnectionPool {
	
	
	    static private Logger __logger = Logger.getLogger(ConnectionPool.class.getName());

	    private static final int MAX_ERRORS = 5;
	    private static int MAX_ACTIVE=-1;

	    private int numberOfErrors;

	    private Date initDate;

	    static private ConnectionPool __instance;

	    public long _connectionCreateTime=0;

	    static synchronized public ConnectionPool getInstance() {
	        if (__instance == null) {
	            __instance = new ConnectionPool();
	        }
	        return __instance;
	    }


	    /**
	     * Return a connection from pool
	     *
	     * NOTE: make sure you close your connections
	     *
	     * @return
	     * @throws SQLException
	     */
	    static public Connection getConnection() throws SQLException {
	        try {
	            return getInstance().getConnectionAux();
	        } catch (SQLException se) {
	            __logger.log(Level.SEVERE, "Could not get DB connection: " + se.getMessage());
	            throw new SQLException("Could not get DB connection", se);
	        }
	    }

	    BasicDataSource _dataSource;

	    private ConnectionPool() {
	        try {

	        	WaterR8Properties props = WaterR8Properties.getInstance();
	        	
	            __logger.info("Creating ConnectionPool");

	            _dataSource = new BasicDataSource();

	            // _dataSource.setLogAbandoned(true);
	            // _dataSource.setRemoveAbandoned(true);
	            // _dataSource.setRemoveAbandonedTimeout(300);

	            // setup our custom configuration
	            // _dataSource.setPoolPreparedStatements(true);
	            // _dataSource.setMaxOpenPreparedStatements(5);


	            // _dataSource.setMaxWait(props.getPropertyInt("cpool.maxWait","60000")); // break any deadlock
	            // _dataSource.setInitialSize(hp.getPropertyInt("cpool.initialSize","10"));
	            // _dataSource.setMaxIdle(hp.getPropertyInt("cpool.maxIdle", "3"));
	            MAX_ACTIVE = 10;
	            _dataSource.setMaxActive(MAX_ACTIVE);
	            _dataSource.setValidationQuery("select 1");
	            _dataSource.setTestOnBorrow(true);
	            _dataSource.setTestOnReturn(true);
	            _dataSource.setTestWhileIdle(true);
	            _dataSource.setTimeBetweenEvictionRunsMillis(60000);
	            _dataSource.setMinEvictableIdleTimeMillis(300000);
	            _dataSource.setLogAbandoned(false);
	            _dataSource.setRemoveAbandoned(false);
	            //_dataSource.setRemoveAbandonedTimeout(hp.getPropertyInt("cpool.removeAbandonedTimeout", "45000"));

	            String jdbcURL = props.getProperty("db.url");
	            String u = props.getProperty("db.user");
	            String p = props.getProperty("db.password");
	            String c = props.getProperty("db.driver.class");
	            
	            if(jdbcURL == null || u == null || p == null || c == null) {
	            	throw new Exception("wr8.properties need db.url, db.user, db.password and db.driver.class specified");
	            }
	            
	            _dataSource.setDriverClassName(c);
	            _dataSource.setUsername(u);
	            _dataSource.setPassword(p);
	            _dataSource.setUrl(jdbcURL);

	            initDate = new Date();

	            __logger.info(String.format(
	                    "Initializing HmConnectionPool: max=%d, max_wait=%d, testOnBorrow=%s,testOnReturn=%s", MAX_ACTIVE,
	                    _dataSource.getMaxWait(), _dataSource.getTestOnBorrow(), _dataSource.getTestOnReturn()));
	        } catch (Exception e) {
	            __logger.log(Level.SEVERE, "Error setting up DB Connection pool", e);
	        }
	    }

	    
	    /**
	     * Get connection from pool
	     *
	     * Client should close connection when complete which will add back to pool.
	     *
	     * @return
	     * @throws Exception
	     */
	    private Connection getConnectionAux() throws SQLException {
	        __logger.log(Level.FINE, "Getting JDBC Connection");
	        /**
	         * attempt to create Connection. Provide auto retry at most
	         * 'attempts' times.
	         */
	        long startTime = System.currentTimeMillis();
	        try {
	            Connection conn = _dataSource.getConnection();
	            if (conn == null) {
	                throw new SQLException("DBPool returned null connection!");
	            }
	            return conn;
	        } catch (SQLException e) {
	            numberOfErrors++;
	            throw e;
	        }
	        finally {
	                _connectionCreateTime += (System.currentTimeMillis() - startTime);
	        }
	    }

	    public long getConnectionCreateTime() {
	        return _connectionCreateTime;
	    }

	    /** Release any connections back to database */
	    public void flushAux() {
	        try {
	            if (_dataSource != null)
	                _dataSource.close();
	        } catch (SQLException sqe) {
	        	 __logger.log(Level.SEVERE, "Error flushing DB Connection pool", sqe);
	        }
	        _dataSource = null;
	    }

	    public int getErrorCount() {
	        return numberOfErrors;
	    }

	    public int getConnectionCount() {
	        return _dataSource.getNumActive();
	    }

	    public String getStatus() {
	        String ret = "";
	        try {
	            if (_dataSource == null) {
	                return "Pool is null";
	            } else {
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
	                String initDateTime = sdf.format(initDate);
	                ret = String
	                        .format(
	                                "HmConnectionPool status: created: %s   max=%d   numActive=%d   numIdle=%d   numberOfErrors: %d   isOK=%s",
	                                initDateTime, MAX_ACTIVE, _dataSource.getNumActive(), _dataSource.getNumIdle(),
	                                numberOfErrors, getIsOK());
	            }
	        } catch (Exception e) {
	        	 __logger.log(Level.SEVERE, "Error getting DB connection pool status", e);
	            ret = e.getMessage();
	        }
	        return ret;
	    }

	    public boolean getIsOK() {
	        if (_dataSource == null)
	            return false;
	        if (_dataSource.getNumActive() > (MAX_ACTIVE - 1))
	            return false;
	        if (numberOfErrors > MAX_ERRORS)
	            return false;
	        return true;
	    }

	    private static void stressTestIt() throws Exception {
	        List<Connection> conns = new ArrayList<Connection>();
	        long startTotal = System.currentTimeMillis();
	        System.out.println("Testing start: " + startTotal);

	        for (int i = 0; i < 400; i++) {
	            long start = System.currentTimeMillis();
	            Connection conn = ConnectionPool.getConnection();
	            // Connection conn =
	            // HotMathProperties.getInstance().getDataSourceObject().getConnection();

	            if (conns.contains(conn))
	                System.out.println("CONNECTION REUSE!");
	            else
	                conns.add(conn);

	            // do something with connection;
	            Statement stmt = conn.createStatement();
	            ResultSet rs = stmt.executeQuery("select * from messages");
	            int dl = 0;
	            while (rs.next()) {
	                dl += rs.getString(1).length();
	            }
	            conn.close();
	            System.out.println(String.format("Test  %d done. Time: %d Length: %d", i,
	                    (System.currentTimeMillis() - start), dl));
	        }
	        System.out.println("Testing complete: " + (System.currentTimeMillis() - startTotal));
	    }

	    public static void main(String as[]) {
	        try {
	            stressTestIt();
	            Thread.sleep(1000 * 10);
	            stressTestIt();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	

}
