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
 * @Description: TODO 用户数据库操作对象
 * 
 */
public class ConsumptionsDAO {
	/**
	 * 
	 * @author xiaoji233
	 * @Description: TODO 根据用户权限调取消费记录
	 * 
	 */
	public ArrayList<Consumptions> consumptionQueryAll(String userTel,int pow) {
        String mysql = "select * from consumption_record";
        mysql+=" where phone_number='"+userTel+"' or pow<"+pow;
        System.out.println(mysql);
        return getConsumption(mysql);
    }
	//根据sql语句获取消费记录
	private ArrayList<Consumptions> getConsumption(String mysql) {
        ArrayList<Consumptions> consumptions = new ArrayList<>();
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
            while (rs.next()) {
                Consumptions consumption = new Consumptions();
                consumption.setTel(rs.getString(1));
                consumption.setConsumption(Double.valueOf(rs.getString(2)));
                consumption.setType(Integer.parseInt(rs.getString(3)));
                consumption.setCount(Integer.parseInt(rs.getString(4)));
                consumption.setDate(rs.getTimestamp(5));
               // System.out.println(rs.getTimestamp(5));
                consumption.setUsertype(Integer.parseInt(rs.getString(6)));
                consumptions.add(consumption);
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("获取短信失败");
        }
        return consumptions;
    }
	/**
	 * 
	 * @author xiaoji233
	 * @Description: TODO 添加消费记录
	 * 
	 **/
	public boolean add(String tel,double consumption,int type,int count,long date,int pow) {
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if(type!=3) {
			consumption*=-1;
		}
        String mysql = "insert into consumption_record values('" + tel +"',"+String.valueOf(consumption)+
                "," + type +","+count+",'"  + sformat.format(date) + "',"+pow+");";
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
	 * @Description: TODO 改电话号码
	 * 
	 **/
	public void changenumber(String old_number,String new_number) {
		String mysql="update consumption_record set phone_number='"+new_number+"' where phone_number='"+old_number+"'";
		int rs=0;
		try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("更新失败");
        }
		
	}
}
