package com.synergix.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {

    public static Connection getConnection() {
        Connection con = null;
        String url = "jdbc:postgresql://localhost:5432/jsfStudent";
//        String url = "jdbc:postgresql://localhost:5432/demo";
        String user = "postgres";
        String password = "password";

        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return con;
    }
}
