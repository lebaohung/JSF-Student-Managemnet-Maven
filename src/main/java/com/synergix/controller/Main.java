package net.javaguides.postgresql.tutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBC Transaction Management Example
 * @author Ramesh Fadatare
 *
 */
public class Main {
    private static final String INSERT_USERS_SQL = "INSERT INTO users " +
            "  (id, name, email, country, password) VALUES " + " (?, ?, ?, ?, ?);";
    private static final String UPDATE_USERS_SQL = "update users set name = ? where id = ?;";

    private final static String url = "jdbc:postgresql://localhost/mydb";
    private final static String user = "postgres";
    private final static String password = "root";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(url, user, password);) {

            // STEP 1 - Disable auto commit mode
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_USERS_SQL);
                 PreparedStatement updateStmt = conn.prepareStatement(UPDATE_USERS_SQL);) {

                // Create insert statement
                insertStmt.setInt(1, 200);
                insertStmt.setString(2, "Tony");
                insertStmt.setString(3, "tony123@gmail.com");
                insertStmt.setString(4, "US");
                insertStmt.setString(5, "secret");
                insertStmt.executeUpdate();

                // Create update statement
                updateStmt.setString(1, "Ram");
                updateStmt.setInt(2, 200);
                updateStmt.executeUpdate();

                // STEP 2 - Commit insert and update statement
                conn.commit();
                System.out.println("Transaction is commited successfully.");
            } catch (SQLException e) {
                printSQLException(e);
                if (conn != null) {
                    try {
                        // STEP 3 - Roll back transaction
                        System.out.println("Transaction is being rolled back.");
                        conn.rollback();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}