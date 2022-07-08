package com.utilities;

import java.sql.*;

public class DatabaseUtil {
    static Connection conn = null;
    Statement stmt = null;
    String DB_URL;
    String driver = null;
    ResultSet results = null;

    private String dbHostName, dbServiceName,dbUserName,dbPassword, dbPort;

    public DatabaseUtil(String dbType, String host, String port, String serviceName, String user, String pass){

        this.dbHostName = host;
        this.dbServiceName = serviceName;
        this.dbUserName = user;
        this.dbPassword = pass;
        this.dbPort = port;
        if(dbType.equalsIgnoreCase("Oracle")) {
            this.DB_URL = "jdbc:oracle:thin:@" + dbHostName + ":" + port + "/" + dbServiceName;
            driver = "oracle.jdbc.driver.OracleDriver";
        }
        if(dbType.equalsIgnoreCase("Mysql")) {
            this.DB_URL = "jdbc:mysql://" + dbHostName + ":" + port + "/" + dbServiceName;
            driver = "com.mariadb.jdbc.Driver";
        }
        if(dbType.equalsIgnoreCase("Maria")) {
            this.DB_URL = "jdbc:mariadb://" + dbHostName + ":" + port + "/" + dbServiceName;
            driver = "org.mariadb.jdbc.Driver";
        }
        if(dbType.equalsIgnoreCase("Postgres")) {
            this.DB_URL = "jdbc:postgresql://" + dbHostName + ":" + port + "/" + dbServiceName;
            driver = "org.postgresql.Driver";
        }

        try {
            //STEP 1 : Register JDBC driver
            Class.forName(driver);

            //STEP 2: Get connection to DB
            conn = DriverManager.getConnection(DB_URL, dbUserName, dbPassword);
            System.out.println("Connected to database successfully........!");

            // STEP 3: Statement object to send the SQL statement to the Database
            //System.out.println("Creating Statement ......!");
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQueryGetResultSet(String queryWithReturn)
    {
        try {
            results = stmt.executeQuery(queryWithReturn);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return results;
    }

    public void executeUpdateInsertQuery(String query) {
        int rowsUpdated = 0;
        try {
            rowsUpdated = stmt.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        System.out.println(rowsUpdated+ "   rows impacted");
    }

    public void commitQuery() {
        try {
            stmt.executeQuery("commit");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        System.out.println("Commit query executed ...!");
    }


    public void closeDBConnection() {
        try {
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        System.out.println("Database Connection closed ...!");
    }

    public String getDataByColumnIndex(ResultSet rs, int index) {
        String result = null;
        try {
            rs.next();
            result = rs.getString(index);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getDataByColumnName(ResultSet rs, String columnName) {
        String result = null;
        try {
            rs.next();
            result = rs.getString(columnName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



}
