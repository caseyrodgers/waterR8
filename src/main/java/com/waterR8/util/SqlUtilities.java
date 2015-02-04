package com.waterR8.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlUtilities {
    public static void releaseResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (Exception e) {e.printStackTrace();} 
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (Exception e) {e.printStackTrace();} 
        }
        if (conn != null) {
            try {
                conn.close();
            }
            catch (Exception e) {e.printStackTrace();} 
        }        
    }
    
    static public int getLastInsertId(final Connection conn) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            // Note: this is MySQL specific
            rs = stmt.executeQuery("select LAST_INSERT_ID()");
            if (rs.next()) {
                int val = rs.getInt(1);
                return val;
            } else {
                throw new Exception("Unable to obtain last auto-increment id");
            }
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }
    
}
