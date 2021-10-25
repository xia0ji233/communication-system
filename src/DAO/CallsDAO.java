package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dbutil.SQLHelper;
import entity.*;
/**
 * 
 * @author: xiaoji233
 * @Description: TODO 通话记录数据库操作对象
 * 
 */
public class CallsDAO {
	/**
	 * 
	 * @author xiaoji233
	 * @Description: TODO 根据用户权限调取通话记录
	 * 
	 */
	public ArrayList<Calls> callQueryAll(String usertel,int pow) {
        String mysql = "select * from call_record";
        if(pow<2) {
        	mysql+=" where sender='"+usertel+"' or receiver='"+usertel+"' or pow<"+pow;
        }
        System.out.println(mysql);
        return getCall(mysql);
    }
	//获得用户信息数组列表
	private ArrayList<Calls> getCall(String mysql) {
        ArrayList<Calls> calls = new ArrayList<>();
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
            while (rs.next()) {
                Calls call = new Calls();
                call.setFrom(rs.getString(1));
                call.setTo(rs.getString(2));
                call.setTimes(Integer.parseInt(rs.getString(3)));
                call.setDate(rs.getTimestamp(4));
                call.setPow(Integer.parseInt(rs.getString(5)));
                calls.add(call);
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("获取通话记录失败");
        }
        return calls;
    }
	/**
	 * 
	 * @author xiaoji233
	 * @Description: TODO 添加记录
	 * 
	 */
	public boolean add(String from, String to,int times, long date,int pow) {
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String mysql = "insert into call_record values('" + from +
                "','" + to + "',"  +times+",'"+sformat.format(date) + "',"+pow+");";
        System.out.println(mysql);
        int rs = 0;//0表示插入失败
        try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("发送消息失败");
        }
        return rs != 0;
    }
	/**
	 * 
	 * @author xiaoji233
	 * @Description: TODO 修改电话号码
	 * 
	 */
	public void changenumber(String old_number,String new_number) {
		String mysql="update call_record set sender='"+new_number+"' where sender='"+old_number+"'";
		int rs=0;
		try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("更新失败");
        }
		mysql="update call_record set receiver='"+new_number+"' where receiver='"+old_number+"'";
		try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("更新失败");
        }
	}
}
