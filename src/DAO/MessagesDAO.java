package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import dbutil.SQLHelper;
import entity.Messages;
/**
 * @author: xiaoji233
 * @Description: TODO 短信数据库操作对象
 */
public class MessagesDAO {
	public ArrayList<Messages> messageQueryAll(String userTel,int pow) {
        String mysql = "select * from message_record";
        if(pow<2) {//非root只能查询自己相关的短信
        	mysql+=" where sender='"+userTel+"' or receiver='"+userTel+"'";
        }
        System.out.println(mysql);
        return getMessage(mysql);
    }
	private ArrayList<Messages> getMessage(String mysql) {
        ArrayList<Messages> messages = new ArrayList<>();
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
            while (rs.next()) {
                Messages message = new Messages();
                message.setFrom(rs.getString(1));
                message.setTo(rs.getString(2));
                message.setMessage(rs.getString(3));
                message.setDate(rs.getTimestamp(4));
                //System.out.println(rs.getTimestamp(4));
               
                messages.add(message);
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("获取短信失败");
        }
        return messages;
    }
	public boolean add(String from, String to, String message, long date) {
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String mysql = "insert into message_record values('" + from +
                "','" + to + "','"  + message+"','"+sformat.format(date) + "');";
        System.out.println(mysql);
        int rs = 0;//0表示插入失败
        try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("发送消息失败");
        }
        return rs != 0;
    }
	public void changenumber(String old_number,String new_number) {
		String mysql="update message_record set sender='"+new_number+"' where sender='"+old_number+"'";
		int rs=0;
		try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("更新失败");
        }
		mysql="update message_record set receiver='"+new_number+"' where receiver='"+old_number+"'";
		try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("更新失败");
        }
	}
	
}
