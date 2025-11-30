package com.ptit.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {

    public static Connection getConnection() {
        Connection con = null;
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "host.docker.internal";
        String port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
        String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "jsfstudent";
        String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "postgres";
        
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return con;
    }
}
