package com.hiepnh.chatserver.connection;

import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.model.MessageModel;
import com.hiepnh.chatserver.utils.AppUtils;

import java.sql.*;

public class GetConnection {

    private final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private Connection connection;

    public GetConnection() {
        connection = getConnection();
    }

    private Connection getConnection(){
        final String URL = "jdbc:mysql://localhost:3306/chat_application?autoReconnect=true&useSSL=false"
                + "&useUnicode=yes&characterEncoding=UTF-8";
        final String USERNAME = "root";
        final String PASSWORD = "1302";
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
        }
        return null;
    }

    public UserEntity findUserByUsername(String username){
        UserEntity user = new UserEntity();
        String sql = "Select * from user where username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
            return null;
        }
        return user;
    }

    public void saveMessage(MessageModel message){
        UserEntity sender = findUserByUsername(message.getSender());
        UserEntity receiver = findUserByUsername(message.getReceiver());

        String sql = "insert into message(sender_id, receiver_id,content,type,status,time) "
                + " values(?,?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, sender.getId());
            preparedStatement.setInt(2, receiver.getId());
            preparedStatement.setString(3, message.getContent());
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, 1);
            preparedStatement.setLong(6, System.currentTimeMillis());
            int rs = preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex);
        }

        String sql1 = "select * from interaction_user where user_id = ? and interaction_id = ?";
        int userId = 0;
        try {
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setInt(1, sender.getId());
            ps1.setInt(2, receiver.getId());

            ResultSet resultSet = ps1.executeQuery();
            while (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        }catch (Exception ex){
            System.err.println(ex);
        }

        if(userId == 0){
            String sql2 = "insert into interaction_user (user_id, interaction_id, content, time) " +
                    " values(?,?,?,?) ";
            try {
                PreparedStatement ps1 = connection.prepareStatement(sql2);
                ps1.setInt(1, sender.getId());
                ps1.setInt(2, receiver.getId());
                ps1.setString(3, message.getContent());
                ps1.setLong(4, System.currentTimeMillis());
                int rs2 = ps1.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(ex);
            }

            try {
                PreparedStatement ps2 = connection.prepareStatement(sql2);
                ps2.setInt(1, receiver.getId());
                ps2.setInt(2, sender.getId());
                ps2.setString(3, message.getContent());
                ps2.setLong(4, System.currentTimeMillis());
                int rs3 = ps2.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }else {
            String sql2 = "update interaction_user set content = ?, " +
                    " time = ? where " +
                    " user_id = ? and interaction_id = ?";
            try {
                PreparedStatement ps1 = connection.prepareStatement(sql2);
                ps1.setString(1, message.getContent());
                ps1.setLong(2, System.currentTimeMillis());
                ps1.setInt(3, sender.getId());
                ps1.setInt(4, receiver.getId());
                int rs2 = ps1.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(ex);
            }

            try {
                PreparedStatement ps2 = connection.prepareStatement(sql2);
                ps2.setString(1, message.getContent());
                ps2.setLong(2, System.currentTimeMillis());
                ps2.setInt(3, receiver.getId());
                ps2.setInt(4, sender.getId());
                int rs3 = ps2.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }

    }

}
