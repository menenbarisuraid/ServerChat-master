package com.example.chat;


import java.sql.*;

public class AuthServer {
    private static Connection connection;
    private static Statement statement;
    static String URL = "jdbc:mysql://localhost:3306/mydbtest";
    static String name = "root";
    static String password = "password";
    public static void connect(){
        try {
            connection = DriverManager.getConnection(URL,name,password);

            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getNickByLoginPass(String login, String password){
        String sql = String.format("select nick from mydbtest.`chat.db` where name = '%s' and password = '%s'",login,password);
        try {
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                return resultSet.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void changenick(String msg,ClientHandler cl) throws SQLException {
        String sql = String.format("INSERT INTO mydbtest.`chat.db`(name,password,nick) VALUES (" + cl.getName() + "," + cl.getPassword() + "," + msg + ");");
        statement.executeUpdate(sql);
    }

}