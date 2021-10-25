package DAO;

import dbutil.SQLHelper;
import entity.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author: xiaoji233
 * @Description: TODO 用户数据库操作对象
 */
public class UsersDAO {
    public boolean userValidate(String sign,String pwd, int type) {
        boolean existed = false;
        String mysql = "select username,password from users ";
        mysql += "where username='" + sign + "'" + " and password='" + pwd + "'";
        mysql += " and user_type=" + type + "";
        if(sign.charAt(0)>='0'&&sign.charAt(0)<='9') {
        	mysql=mysql.replace("username", "phone_number");
        }
        System.out.println(mysql);
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
         
            if (rs.next()) {
                existed = true;
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("验证用户方法中报错");
        }
        return existed;
    }
    //通过手机号寻找用户
    public Users userQueryByTel(String usertel) {
        String mysql = "select * from users where phone_number='" + usertel + "'";
        System.out.println(mysql);
        ArrayList<Users> users=getUsers(mysql);
        if(users.size()==0)return null;
        return users.get(0);
    }
    //根据电话号码查找余额
    public double userQuerybalance(String usertel) {
        String mysql = "select * from users where phone_number='" + usertel + "'";
        System.out.println(mysql);
        return getUsers(mysql).get(0).getBalance();
    }
    //获得用户信息数组列表
    public Users userQueryByName(String username) {
        String mysql = "select * from users where username='" + username + "'";
        System.out.println(mysql);
        return getUsers(mysql).get(0);
    }
    //获得用户信息数组列表
    private ArrayList<Users> getUsers(String mysql) {
        ArrayList<Users> users = new ArrayList<>();
        try {
            ResultSet rs = SQLHelper.executeQuery(mysql);
            while (rs.next()) {
                Users user = new Users();
                user.setUsername(rs.getString(1));
                user.setPassword(rs.getString(2));
                user.setId_number(rs.getString(3));
                user.setId_type(rs.getInt(4));
                user.setUser_type(rs.getInt(5));
                user.setPhone_number(rs.getString(6));
                user.setBalance(rs.getDouble(7));
                user.setSex(rs.getInt(8));
                users.add(user);
            }
            SQLHelper.closeConnection();
        } catch (SQLException e) {
            System.out.println("获取用户数组列表方法中报错");
        }
        return users;
    }
    //查询用户是否存在，不存在返回false
    public boolean userExisted(String usertel,String username) {
        String mysql = "select * from users where phone_number='" + usertel + "'"+" or "+"username='"+username+"'";
        System.out.println(mysql+"test");
        Object obj = SQLHelper.executeSingleQuery(mysql);
        if (obj==null){
            return false;
        }
        else {
            return true;
        }
    }
    //更改余额
    public boolean updateBalance(String tel,double balance) {
    	String mysql="update users set balance="+String.valueOf(balance)+" where phone_number='"+tel+"'";
    	System.out.println(mysql);
		return SQLHelper.executeUpdate(mysql)!=0;
    }
    //注册用户信息，返回注册是否成功
    public boolean register(String tel, String password, String username, int sex, int usertype, String id, int idtype) {
        String mysql = "insert into users values('" + username +
                "','" + password + "','" + id + "'," + idtype +","+usertype+",'" +
                tel + "'," + "0.00" + "," + sex + ");";
        System.out.println(mysql);
        int rs = 0;//0表示插入失败
        try {
            rs = SQLHelper.executeUpdate(mysql); //如果更新成功，则rs=1
        } catch (Exception e) {
            System.out.println("注册用户信息失败");
        }
        return rs != 0;
    }
    
    //获得全部用户信息
    public ArrayList<Users> userQueryAll(int pow,String user) {
        String mysql = "select * from users where user_type<"+pow+" or username='"+user+"'";
        if(user.charAt(0)>='0'&&user.charAt(0)<='9') {
        	mysql=mysql.replace("username","phone_number" );
        }
        System.out.println(mysql);
        return getUsers(mysql);
    }
    
    //删除用户信息(用于管理员)
    public int deleteUser(String username) {
        String mysql = "delete from users where username='" + username + "'";
        System.out.println(mysql);
        return SQLHelper.executeUpdate(mysql);
    }
    public int updateUser(String username, String password, String id,int id_type, int usertype, String tel,double balance,int sex) {
    	if(!username.equals("root")&&usertype==2) {//判断设置权限为2的人name是否为root
    		return 2;
    	}
    	//判断手机号是否有重复
    	Users user=userQueryByTel(tel);
    	if(user!=null&&!user.getUsername().equals(username)) {
    		return 3;
    	}
    	
		String mysql = "update users set ";//+"password='" + password + "',";
		if(!password.equals("d41d8cd98f00b204e9800998ecf8427e")) {//空字符串的md5
			mysql+="password='" + password + "',";
		}
		String mysql1 = "phone_number='" + tel + "',sex=" + sex + ",balance="+balance+",";
		String mysql2 = "user_type=" + usertype + ",id_type="+id_type+",sex="+sex+",id_number='"+id+"'";
		String mysql3 = " where username='" + username + "'";
		mysql = mysql + mysql1 + mysql2 + mysql3;
		System.out.println(mysql);
		return SQLHelper.executeUpdate(mysql);
    }

}
