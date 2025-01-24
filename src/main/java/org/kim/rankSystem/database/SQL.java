package org.kim.rankSystem.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SQL {
    private Connection con;
    private  String HOST = "";
    private  String DATABASE = "";
    private  String USER = "";
    private  String PASSWORD = "";
    private  String AUTO_CONNECT = "";

    public SQL(String host, String database, String user, String password, String autoConnect) {
        this.HOST = host;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;
        this.AUTO_CONNECT = autoConnect;
        connect();
    }

    public void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE + "?autoReconnect=" + AUTO_CONNECT, USER, PASSWORD);
                System.out.println("[MySQL] Connection established");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[MySQL] Error occurred while connecting!");
            }
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                con.close();
                System.out.println("[MySQL] Connection to database closed.");
            } catch (SQLException e) {
                System.out.println("[MySQL] Could not close connection to database.");
            }
        }
    }

    public void update(String qry) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return con.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isConnected() {
        return con != null;
    }
}