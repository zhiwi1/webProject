package com.epam.webproject;

import com.epam.webproject.model.entity.RatesType;
import com.epam.webproject.model.entity.RoleType;
import com.epam.webproject.model.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class Main {
    public static void main(String args[]) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
//try {
//   connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/test" +
//           "","root","Qwe123okA123");
//} catch (SQLException throwables) {
//    throwables.printStackTrace();
//}
        String url = "jdbc:mysql://localhost:3306/first_project";
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "Qwe123okA123");
        properties.put("autoReconnect", "true");
        properties.put("characterEncoding", "UTF-8");
        properties.put("useUnicode", "true");
        try (
                Connection connection = DriverManager.getConnection(url, properties);
                Statement statement = connection.createStatement()) {
            String sql = "SELECT id,login FROM users";
            ResultSet resultSet = statement.executeQuery(sql);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String login = resultSet.getString(2);
                users.add(new User(id,login,"",0,RoleType.valueOf("user".toUpperCase()), RatesType.valueOf("newbie".toUpperCase())));
            }
            System.out.println(users.get(0));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}