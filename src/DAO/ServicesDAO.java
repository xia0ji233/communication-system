package DAO;

import dbutil.SQLHelper;
import entity.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author: xiaoji233
 * @Description: TODO 业务数据库操作对象
 */
public class ServicesDAO {
	
	
	
    public ArrayList<Services> serviceQueryAll() {
        String mysql = "select * from price;";
        System.out.println(mysql);
        return getServices(mysql);
    }
    private ArrayList<Services> getServices(String mysql) {
        ArrayList<Services> services = new ArrayList<>();
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
            while (rs.next()) {
                Services service = new Services();
                service.setName(Integer.parseInt(rs.getString(1)));
                
                service.setPrice(Double.valueOf(rs.getString(2)));
                services.add(service);
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("获取业务类型失败");
        }
        return services;
    }
    
    public int updatePrice(String name,double price) {
    	int type=0;
    	if(name.equals("电话")) {
    		type=1;
    	}
    	else if(name.equals("流量")) {
    		type=2;
    	}
		String mysql = "update price set "+"unite_price=" + price + " where name="+type+";";
		System.out.println(mysql);
		return SQLHelper.executeUpdate(mysql);
    }
    
    public double QueryPrice(int type) {
    	ArrayList<Services> services = serviceQueryAll();
    	for(Services service:services) {
    		if(service.getName()==type) {
    			return service.getPrice();
    		}
    	}
    	return 8848;
    }
}
