package ru.geekbrains.nvgostev.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Db {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psIns;
    private static PreparedStatement psDel;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to db");
        }
    }

    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void prepareInsert() throws SQLException {
        psIns = connection.prepareStatement("INSERT INTO students (name, age, email) VALUES (?, ?, ?);");
    }

    private static void prepareDelete() throws SQLException {
        psDel = connection.prepareStatement("DELETE FROM students WHERE name = ?");
    }

    private static void closePreparedStatement(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getAll(String condition) {
        StringBuilder sb = new StringBuilder();
        if (connection != null) {
            try (ResultSet rs = statement.executeQuery(String.format("SELECT * FROM students %s;", condition))) {
                while (rs.next()) {
                    sb.append(rs.getInt("id")).append("\t");
                    sb.append(rs.getString("name")).append("\t");
                    sb.append(rs.getInt("age")).append("\t");
                    sb.append(rs.getString("email")).append("\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getAllUsers() {
        return getAll("");
    }

    public static String getUsersByAge(int min, int max) {
        if (min >= max) {
            throw  new RuntimeException("Min must be less then max");
        }
        return getAll(String.format("WHERE (age >= %d) and (age <= %d)", min, max));
    }

    public static void delUserByName(String name) {
        delUserByName(new ArrayList<>(Arrays.asList(name)));
    }

    public static void delUserByName(List<String> names) {
        if (names.isEmpty() || connection == null) {
            return;
        }
        try {
            prepareDelete();
            connection.setAutoCommit(false);
            for (String name:names) {
                psDel.setString(1, name);
                psDel.executeUpdate();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(psDel);
        }
    }

    public static void add(String userData) {
        add(new ArrayList<>(Arrays.asList(userData)));
    }

    public static void add(List<String> usersData) {
        if (usersData.isEmpty() || connection == null) {
            return;
        }
        try {
            prepareInsert();
            connection.setAutoCommit(false);
            for (String userData:usersData) {
                String[] data = userData.split("\\s");
                psIns.setString(1, data[0]);
                psIns.setInt(2, Integer.parseInt(data[1]));
                psIns.setString(3, data[2]);
                psIns.executeUpdate();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(psIns);
        }
    }

}
