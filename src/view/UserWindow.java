package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;


import DAO.*;
import entity.*;

/**
 * @author: xiaoji233
 * @Description: TODO 用户界面类
 */

public class UserWindow extends JFrame implements ActionListener {
	private int pow=0;
    //菜单项目
	private String old_tel;
	private String self_user,tel;
	private boolean is_in_call=false;
	private long time;
	private JLabel lbl_tonghua=new JLabel("                   通话中……");
    private JMenuItem menuitem_ViewMessage, menuitem_setPrice,menuitem_SendMessage,menuitem_ViewCall,
    menuitem_SendCall,menuitem_ViewConsumption,menuitem_AddMoney;
    private JMenuItem menuitem_usersInfo, menuitem_exit;
    private JTable serviceTable, userTable,messageTable,consumptionTable,callTable;
    private JPanel jp_singleServiceInfo, jp_singleUserInfo, jp_singleMessageInfo, jp_singleSendMessage, jp_singleSendCall,
    jp_singleConsumptionInfo, jp_singleCallInfo, jp_singleAddMoney, jp_main;
    private JTextField txt_from, txt_to, txt_date;
    private JTextArea txt_message,txt_info;
    //用到的下拉框
    private JComboBox<String>  com_sex, com_userType,com_idType;
    private ServicesDAO ServiceDao=new ServicesDAO();
    private MessagesDAO MessageDao=new MessagesDAO();
    private ConsumptionsDAO ConsumptionDao=new ConsumptionsDAO();
    private CallsDAO CallDao=new CallsDAO();
    private ArrayList<Services> services = new ArrayList<>();
    private ArrayList<Messages> messages = new ArrayList<>();
    private ArrayList<Consumptions> consumptions = new ArrayList<>();
    private ArrayList<Calls> calls = new ArrayList<>();
    private UsersDAO UsersDao = new UsersDAO();
    //用户信息表头
    private Object[] cols_user = {"手机号", "密码", "用户名", "性别", "用户类型", "证件类型", "ID","余额"};
    private Object[] cols_service = {"业务类型", "价格"};
    private Object[] cols_message = {"发送者", "接收者", "短信内容", "发送日期"};
    private Object[] cols_consumption = {"手机号", "消费金额", "消费业务", "单位计数", "消费日期", "用户类型"};
    private Object[] cols_call = {"发送者", "接收者", "通话时间", "通话日期","权限等级"};
    private Object[][] rows_user;
    private Object[][] rows_service;
    private Object[][] rows_message;
    private Object[][] rows_consumption;
    private Object[][] rows_call;
    
    //单个用户信息显示框及功能按钮
    private JTextField txt_tel, txt_password, txt_name, txt_id,txt_balance,txt_service,txt_price;
    private JTextField txt_send,txt_recv,txt_money,txt_telnumber, txt_consumption, txt_type, txt_count,txt_cdate,txt_usertype;
    private JTextField txt_recv1,txt_sender,txt_recver,txt_times,txt_dat,txt_level,txt_add,txt_moneys,txt_numbers,txt_moneyc;
    private JButton btn_confirm, btn_deleteUser,btn_yes,btn_send,btn_call, btn_add;
    

    public UserWindow(String s,int type,String sign) {
    	this.pow=type;
    	this.self_user=sign;
    	tel=self_user;
    	if(self_user.charAt(0)<'0'||self_user.charAt(0)>'9') {
    		tel=UsersDao.userQueryByName(self_user).getPhone_number();
    	}
    	
        //设置界面题头和符号
        setTitle(s);
        ImageIcon icon = new ImageIcon("picture/logo.jpg");
        setIconImage(icon.getImage());

        //设置系统界面菜单条
        this.setMenuBar();

        //初始化所有用户信息表格
        userTable = new JTable(rows_user, cols_user){//防止单元格直接编辑
        	public boolean isCellEditable(int row, int column){
        		return false;
        	}
        };
        //为用户信息表格添加监视器
        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSelectedUserInfo();
            }
        });
        
        serviceTable = new JTable(rows_service, cols_service) {
        	public boolean isCellEditable(int row, int column){
        		return false;
        	}
        };
		serviceTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
                showSelectedServiceInfo();
            }
		});
		messageTable = new JTable(rows_message, cols_message) {
        	public boolean isCellEditable(int row, int column){
        		return false;
        	}
        };
        messageTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
               showSelectedMessageInfo();
            }
		});
        consumptionTable = new JTable(rows_consumption, cols_consumption) {
        	public boolean isCellEditable(int row, int column){
        		return false;
        	}
        };
        consumptionTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
               showSelectedConsumptionInfo();
            }
		});
        callTable = new JTable(rows_call, cols_call) {
        	public boolean isCellEditable(int row, int column){
        		return false;
        	}
        };
        callTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
               showSelectedCallInfo();
            }
		});
		initSingleMessageInfoModule();
		
        initSingleServiceInfoModule();
        //初始化显示单个用户信息的模块
        initSingleUserInfoModule();
        
        initSingleSendMessageModule();
        
        initSingleConsumptionInfoModule();
        
        initSingleSendCallModule();
        
        initSingleCallInfoModule();
        
        initSingleAddMoneyModule();
        //主面板
        jp_main = new JPanel();
        jp_main.setLayout(new BorderLayout());
        this.add(jp_main);
        this.validate();
        this.setVisible(true);
        this.setSize(1000, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
  

    //修改用户信息
    private void updateUser() {
    	String username = txt_name.getText().trim();
        String tel = txt_tel.getText().trim();
        String pwd = txt_password.getText().trim();
        String id = txt_id.getText().trim();
        int sex = com_sex.getSelectedIndex();
        int userType = com_userType.getSelectedIndex();
        int id_type=com_idType.getSelectedIndex();
        double balance=Double.valueOf(txt_balance.getText().trim());
        if(!CheckString.Checkallnumber(tel)) {
        	JOptionPane.showMessageDialog(this, "电话号码只能由数字组成", "提醒", JOptionPane.WARNING_MESSAGE);
        	return;
        }
        
        /*此处不判断id是否符合规定
        String info=Id.IDCardValidate(id);
    	if (!info.equals("身份证验证通过")) {
    		JOptionPane.showMessageDialog(this, info, "注意", JOptionPane.WARNING_MESSAGE);
    		return ;
    	}
        else {
        	if(CheckString.CheckSQL(id)) {
        		JOptionPane.showMessageDialog(this, "id不允许包含以下特殊字符(',#)", "提醒", JOptionPane.WARNING_MESSAGE);
            	return;
        	}
        }*/
        if(CheckString.CheckSQL(id)) {
    		JOptionPane.showMessageDialog(this, "id不允许包含以下特殊字符(',#)", "提醒", JOptionPane.WARNING_MESSAGE);
        	return;
    	}
        int cost=1;
        if(this.old_tel.length()>tel.length()) {
        	for(int i=tel.length();i<11;i++){
        		cost*=10;
        	}
        }
        
        String info="本次修改手机号需要花费"+cost+"元";
        int r=0;
        if(!this.old_tel.equals(tel))r=JOptionPane.showConfirmDialog(this, info, "提醒", JOptionPane.OK_CANCEL_OPTION);
        else cost=0;
        
        if(r!=0) return ;
        r = UsersDao.updateUser(username, md5.getMD5(pwd), id,id_type,userType,tel,balance-cost,sex);
        
        if (r == 0) {
            JOptionPane.showMessageDialog(this, "更新失败", "提醒", JOptionPane.WARNING_MESSAGE);
            return;
        }
        else if(r==2) {
        	JOptionPane.showMessageDialog(this, "无法将非root用户设置为超级管理员", "提醒", JOptionPane.WARNING_MESSAGE);
        	return;
        }
        else if(r==3) {
        	JOptionPane.showMessageDialog(this, "数据库中存在重复的手机号，请重新设置手机号！", "提醒", JOptionPane.WARNING_MESSAGE);
        	return;
        }
        if(!this.old_tel.equals(tel)) {
	        ConsumptionDao.add(tel, cost, 4, cost, System.currentTimeMillis(), userType);
	        CallDao.changenumber(this.old_tel, tel);
	        ConsumptionDao.changenumber(this.old_tel, tel);
	        MessageDao.changenumber(this.old_tel, tel);
        }
        JOptionPane.showMessageDialog(this, "修改成功", "提醒", JOptionPane.PLAIN_MESSAGE);
    }

    //删除用户
    private void deleteUser(boolean flag) {
        String username = txt_name.getText().trim();
        if(username.equals("root")) {
        	JOptionPane.showMessageDialog(this, "root用户无法删除", "提醒", JOptionPane.PLAIN_MESSAGE);
        	return;
        }
        int k = UsersDao.deleteUser(username);
        
        if (k == 1) {
        	if(flag) {
        		JOptionPane.showMessageDialog(this, "注销成功，如需再次使用本系统，请重新注册账号！", "提醒", JOptionPane.PLAIN_MESSAGE);
        		System.exit(0);
        	}
            JOptionPane.showMessageDialog(this, "删除成功", "提醒", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "删除失败", "提醒", JOptionPane.PLAIN_MESSAGE);
        }
    }
    private void updatePrice() {
    	String service_name=txt_service.getText();
    	double price=Double.valueOf(txt_price.getText());
    	ServiceDao.updatePrice(service_name, price);
    }
    private void setMenuBar() {
        //设置菜单条
        JMenuBar menubar = new JMenuBar();
        JMenu menu_tongxin, menu_price, menu_users, menu_exit;
        menu_tongxin = new JMenu("通信业务");
        menu_price = new JMenu("价格管理");
        menu_users = new JMenu("用户服务");
        menu_exit = new JMenu("退出系统");
        menuitem_ViewMessage = new JMenuItem("查看短信");
        menuitem_ViewMessage.addActionListener(this);
        menuitem_SendMessage = new JMenuItem("发送短信");
        menuitem_SendMessage.addActionListener(this);
        menuitem_ViewCall = new JMenuItem("通话记录");
        menuitem_ViewCall.addActionListener(this);
        menuitem_SendCall = new JMenuItem("发起通话");
        menuitem_SendCall.addActionListener(this);
        menuitem_AddMoney = new JMenuItem("话费充值");
        menuitem_AddMoney.addActionListener(this);
        menuitem_ViewConsumption = new JMenuItem("消费记录");
        menuitem_ViewConsumption.addActionListener(this);
        //menuitem_ViewCall = new JMenuItem("通话记录");
        
        menuitem_setPrice = new JMenuItem("价格设置");
        if(pow<2)menuitem_setPrice.setText("查看价格");//如果权限不为root则更改信息
        
        menuitem_setPrice.addActionListener(this);
        menuitem_usersInfo = new JMenuItem("修改用户信息");
        menuitem_usersInfo.addActionListener(this);
        menuitem_exit = new JMenuItem("退出系统");
        menuitem_exit.addActionListener(this);
        menu_tongxin.add(menuitem_ViewMessage);
        menu_tongxin.add(menuitem_SendMessage);
        menu_tongxin.add(menuitem_ViewCall);
        menu_tongxin.add(menuitem_SendCall);
        menu_tongxin.add(menuitem_AddMoney);
        menu_tongxin.add(menuitem_ViewConsumption);
        menu_price.add(menuitem_setPrice);
        menu_users.add(menuitem_usersInfo);
        menu_exit.add(menuitem_exit);
        menubar.add(menu_tongxin);
        menubar.add(menu_price);
        menubar.add(menu_users);
        menubar.add(menu_exit);
        this.setJMenuBar(menubar);
    }
    //初始化业务价格信息模块
    private void initSingleServiceInfoModule() {
    	JLabel lbl_name, lbl_price;
    	JPanel jp_showServiceInfo=new JPanel(),jp_confirm=new JPanel();
    	
    	
    	
    	lbl_name = new JLabel("业务名称:");
        lbl_name.setHorizontalAlignment(JLabel.CENTER);
        lbl_price = new JLabel("价格:");
        lbl_price.setHorizontalAlignment(JLabel.CENTER);
        txt_service = new JTextField(20);
        txt_service.setEditable(false);
        txt_price = new JTextField(20);
        if(pow<2)txt_price.setEditable(false);
        jp_showServiceInfo.add(lbl_name);
        jp_showServiceInfo.add(txt_service);
        jp_showServiceInfo.add(lbl_price);
        jp_showServiceInfo.add(txt_price);
        
        btn_yes = new JButton("确认修改");
        btn_yes.addActionListener(this);
    	
        if(pow==2)jp_confirm.add(btn_yes);//root管理员才能看到这个按钮
        
        this.jp_singleServiceInfo = new JPanel();
        this.jp_singleServiceInfo.setLayout(new BorderLayout());
        this.jp_singleServiceInfo.add(jp_showServiceInfo, BorderLayout.CENTER);
        this.jp_singleServiceInfo.add(jp_confirm, BorderLayout.SOUTH);
        
    }
    //初始化单个用户信息显示模块
    
    private void initSingleUserInfoModule() {
        JLabel lbl_tel, lbl_password, lbl_name, lbl_sex, lbl_id, lbl_type,lbl_balance,lbl_idType;
        lbl_tel = new JLabel("手机号:");
        lbl_tel.setHorizontalAlignment(JLabel.CENTER);
        lbl_password = new JLabel("密码:");
        lbl_password.setHorizontalAlignment(JLabel.CENTER);
        lbl_name = new JLabel("用户名:");
        lbl_name.setHorizontalAlignment(JLabel.CENTER);
        lbl_balance = new JLabel("余额:");
        lbl_balance.setHorizontalAlignment(JLabel.CENTER);
        lbl_sex = new JLabel("性别:");
        lbl_sex.setHorizontalAlignment(JLabel.CENTER);
        lbl_type = new JLabel("类型:");
        lbl_type.setHorizontalAlignment(JLabel.CENTER);
        lbl_idType = new JLabel("证件类型:");
        lbl_idType.setHorizontalAlignment(JLabel.CENTER);
        lbl_id = new JLabel("ID:");
        lbl_id.setHorizontalAlignment(JLabel.CENTER);
        //个人信息录入框
        txt_tel = new JTextField(20);
        txt_password = new JTextField(20);
        txt_name = new JTextField(20);
        txt_name.setEditable(false);//用户名任何人无法更改，作为识别用户的唯一标识符
        txt_balance = new JTextField(20); 
        com_sex = new JComboBox<>(new String[]{"男", "女"});
        com_userType = new JComboBox<>(new String[]{"用户", "管理员","超级管理员"});
        
        com_idType = new JComboBox<>(new String[]{"中华人民共和国居民身份证", "港澳台居民身份证","护照"});
        txt_id = new JTextField(20);
        //设置权限
        if(pow<2) {//root的额外权限
        	com_userType.enable(false);
        	txt_balance.setEditable(false);
        	txt_tel.setEditable(false);
        }
        
        if(pow<1) {//管理员权限
        	com_sex.enable(false);
        	com_idType.enable(false);
        	txt_id.setEditable(false);
        }
        //个人信息显示面板
        JPanel jp_showUserInfo = new JPanel();
        jp_showUserInfo.setLayout(new GridLayout(2, 6));
        jp_showUserInfo.add(lbl_tel);
        jp_showUserInfo.add(txt_tel);
        jp_showUserInfo.add(lbl_password);
        jp_showUserInfo.add(txt_password);
        jp_showUserInfo.add(lbl_name);
        jp_showUserInfo.add(txt_name);
        jp_showUserInfo.add(lbl_balance);
        jp_showUserInfo.add(txt_balance);
        
        
        jp_showUserInfo.add(lbl_sex);
        jp_showUserInfo.add(com_sex);
        jp_showUserInfo.add(lbl_type);
        jp_showUserInfo.add(com_userType);
        
        jp_showUserInfo.add(lbl_idType);
        jp_showUserInfo.add(com_idType);
        jp_showUserInfo.add(lbl_id);
        jp_showUserInfo.add(txt_id);

        //功能按钮
        btn_confirm = new JButton("确认修改");
        btn_confirm.addActionListener(this);
        btn_deleteUser = new JButton("删除用户");
        btn_deleteUser.addActionListener(this);
        JPanel jp_confirm_delete = new JPanel();
        jp_confirm_delete.add(btn_confirm);
        jp_confirm_delete.add(btn_deleteUser);

        //总面板
        this.jp_singleUserInfo = new JPanel();
        this.jp_singleUserInfo.setLayout(new BorderLayout());
        this.jp_singleUserInfo.add(jp_showUserInfo, BorderLayout.CENTER);
        this.jp_singleUserInfo.add(jp_confirm_delete, BorderLayout.SOUTH);
    }
    private void initSingleMessageInfoModule() {
    	JLabel lbl_from, lbl_to, lbl_message, lbl_date;
    	
    	
    	JPanel jp_showMessageInfo=new JPanel();
    	jp_showMessageInfo.setLayout(new GridLayout(2, 4));
    	lbl_from = new JLabel("发送者:");
        lbl_from.setHorizontalAlignment(JLabel.CENTER);
        lbl_from.setFont(new java.awt.Font("Serif",0,20));
        lbl_to = new JLabel("接收者:");
        lbl_to.setHorizontalAlignment(JLabel.CENTER);
        lbl_to.setFont(new java.awt.Font("Serif",0,20));
        lbl_message = new JLabel("短信:");
        lbl_message.setHorizontalAlignment(JLabel.CENTER);
        lbl_message.setFont(new java.awt.Font("Serif",0,20));
        lbl_date = new JLabel("日期:");
        lbl_date.setHorizontalAlignment(JLabel.CENTER);
        lbl_date.setFont(new java.awt.Font("Serif",0,20));
        
        txt_from = new JTextField(18);
        txt_from.setEditable(false);
        txt_from.setFont(new java.awt.Font("Serif",0,30));
        txt_to = new JTextField(18);
        txt_to.setFont(new java.awt.Font("Serif",0,30));
        txt_to.setEditable(false); 
        txt_date = new JTextField(18);
        txt_date.setFont(new java.awt.Font("Serif",0,25));
        txt_date.setEditable(false);
        txt_message = new JTextArea(4,0);
        txt_message.setColumns(18);
        txt_message.setLineWrap(true);        //激活自动换行功能 
        txt_message.setEditable(false);
       // txt_message.setWrapStyleWord(true);            // 激活断行不断字功能
        JScrollPane scroll = new JScrollPane(txt_message); 
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        
        jp_showMessageInfo.add(lbl_from);
        jp_showMessageInfo.add(txt_from);
        jp_showMessageInfo.add(lbl_to);
        jp_showMessageInfo.add(txt_to);
        
        jp_showMessageInfo.add(lbl_date);
        jp_showMessageInfo.add(txt_date);
        jp_showMessageInfo.add(lbl_message);
        jp_showMessageInfo.add(scroll);

        this.jp_singleMessageInfo = new JPanel();
        this.jp_singleMessageInfo.setLayout(new BorderLayout());
        this.jp_singleMessageInfo.add(jp_showMessageInfo, BorderLayout.CENTER);
        
    }
    private void initSingleSendMessageModule() {
    	JLabel lbl_send, lbl_recv, lbl_message, lbl_money;
    	
    	JPanel jp_showSendMessage=new JPanel();
    	jp_showSendMessage.setLayout(new GridLayout(2, 4));
    	lbl_send = new JLabel("发件人:");
    	lbl_send.setHorizontalAlignment(JLabel.CENTER);
    	lbl_send.setFont(new java.awt.Font("Serif",0,20));
    	lbl_recv = new JLabel("收件人:");
    	lbl_recv.setHorizontalAlignment(JLabel.CENTER);
    	lbl_recv.setFont(new java.awt.Font("Serif",0,20));
        lbl_message = new JLabel("信息:");
        lbl_message.setHorizontalAlignment(JLabel.CENTER);
        lbl_message.setFont(new java.awt.Font("Serif",0,20));
        lbl_money = new JLabel("余额:");
        lbl_money.setHorizontalAlignment(JLabel.CENTER);
        lbl_money.setFont(new java.awt.Font("Serif",0,20));
        
        txt_send = new JTextField(18);
        txt_send.setFont(new java.awt.Font("Serif",0,30));
        txt_send.setEditable(false);
        txt_send.setText(tel);
        txt_recv = new JTextField(18);
        txt_recv.setFont(new java.awt.Font("Serif",0,30));
        txt_money = new JTextField(18);
        txt_money.setFont(new java.awt.Font("Serif",0,30));
        txt_money.setEditable(false);
        
        txt_info = new JTextArea(4,0);
        txt_info.setColumns(18);
        txt_info.setLineWrap(true);        //激活自动换行功能 
       // txt_message.setWrapStyleWord(true);            // 激活断行不断字功能
        JScrollPane scroll = new JScrollPane(txt_info); 
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        
        jp_showSendMessage.add(lbl_send);
        jp_showSendMessage.add(txt_send);
        jp_showSendMessage.add(lbl_recv);
        jp_showSendMessage.add(txt_recv);
        jp_showSendMessage.add(lbl_money);
        jp_showSendMessage.add(txt_money);
        jp_showSendMessage.add(lbl_message);
        jp_showSendMessage.add(scroll);
        
        btn_send = new JButton("发送");
        btn_send.addActionListener(this);
        JPanel jp_send = new JPanel();
        jp_send.add(btn_send);
        
        this.jp_singleSendMessage = new JPanel();
        this.jp_singleSendMessage.setLayout(new BorderLayout());
        this.jp_singleSendMessage.add(jp_showSendMessage, BorderLayout.CENTER);
        this.jp_singleSendMessage.add(jp_send, BorderLayout.SOUTH);
    }
    private void initSingleConsumptionInfoModule() {
    	JLabel lbl_tel, lbl_consumption, lbl_type, lbl_count,lbl_date,lbl_usertype;
    	
    	JPanel jp_showViewConsumption=new JPanel();
    	jp_showViewConsumption.setLayout(new GridLayout(2, 4));
    	lbl_tel = new JLabel("电话号码:");
    	lbl_tel.setHorizontalAlignment(JLabel.CENTER);
    	lbl_consumption = new JLabel("消费:");
    	lbl_consumption.setHorizontalAlignment(JLabel.CENTER);
    	lbl_type = new JLabel("业务类型:");
    	lbl_type.setHorizontalAlignment(JLabel.CENTER);
    	lbl_count = new JLabel("计数:");
    	lbl_count.setHorizontalAlignment(JLabel.CENTER);
    	lbl_date = new JLabel("消费日期:");
    	lbl_date.setHorizontalAlignment(JLabel.CENTER);
    	lbl_usertype = new JLabel("用户类型:");
    	lbl_usertype.setHorizontalAlignment(JLabel.CENTER);
    	
    	txt_telnumber = new JTextField(20);
    	txt_telnumber.setEditable(false);
    	txt_consumption = new JTextField(20);
    	txt_consumption.setEditable(false);
    	txt_type = new JTextField(20);
    	txt_type.setEditable(false);
    	txt_count = new JTextField(20);
    	txt_count.setEditable(false);
    	txt_cdate = new JTextField(20);
    	txt_cdate.setEditable(false);
    	txt_usertype = new JTextField(20);
    	txt_usertype.setEditable(false);
        
    	jp_showViewConsumption.add(lbl_tel);
    	jp_showViewConsumption.add(txt_telnumber);
    	jp_showViewConsumption.add(lbl_consumption);
    	jp_showViewConsumption.add(txt_consumption);
    	jp_showViewConsumption.add(lbl_type);
    	jp_showViewConsumption.add(txt_type);
    	jp_showViewConsumption.add(lbl_count);
    	jp_showViewConsumption.add(txt_count);
    	jp_showViewConsumption.add(lbl_date);
    	jp_showViewConsumption.add(txt_cdate);
    	jp_showViewConsumption.add(lbl_usertype);
    	jp_showViewConsumption.add(txt_usertype);
    	
        this.jp_singleConsumptionInfo = new JPanel();
        this.jp_singleConsumptionInfo.setLayout(new BorderLayout());
        this.jp_singleConsumptionInfo.add(jp_showViewConsumption, BorderLayout.CENTER);
    }
    private void initSingleSendCallModule(){
    	JLabel lbl_send, lbl_recv, lbl_money,lbl_space;
    	
    	JPanel jp_showSendCall=new JPanel();
    	jp_showSendCall.setLayout(new GridLayout(2, 4));
    	lbl_send = new JLabel("发送者:");
    	lbl_send.setHorizontalAlignment(JLabel.CENTER);
    	lbl_send.setFont(new java.awt.Font("Serif",0,20));
    	lbl_recv = new JLabel("接收者:");
    	lbl_recv.setHorizontalAlignment(JLabel.CENTER);
    	lbl_recv.setFont(new java.awt.Font("Serif",0,20));
        lbl_money = new JLabel("余额:");
        lbl_money.setHorizontalAlignment(JLabel.CENTER);
        lbl_money.setFont(new java.awt.Font("Serif",0,20));
        lbl_space=new JLabel();
        
        txt_sender = new JTextField(18);
        txt_sender.setFont(new java.awt.Font("Serif",0,30));
        txt_sender.setEditable(false);
        txt_sender.setText(tel);
        txt_recv1 = new JTextField(18);
        txt_recv1.setFont(new java.awt.Font("Serif",0,30));
        txt_moneys = new JTextField(18);
        txt_moneys.setFont(new java.awt.Font("Serif",0,30));
        txt_moneys.setEditable(false);
        
        lbl_tonghua.setFont(new java.awt.Font("Serif",1,20));
        lbl_tonghua.setForeground(Color.red);
        lbl_tonghua.setVisible(false);
        
        jp_showSendCall.add(lbl_send);
        jp_showSendCall.add(txt_sender);
        //jp_showSendCall.add(txt_send);
        jp_showSendCall.add(lbl_recv);
        jp_showSendCall.add(txt_recv1);
        jp_showSendCall.add(lbl_money);
        jp_showSendCall.add(txt_moneys);

        jp_showSendCall.add(lbl_tonghua);
        jp_showSendCall.add(lbl_space);
        
        
        btn_call = new JButton("拨号");
        btn_call.addActionListener(this);
        JPanel jp_send = new JPanel();
        jp_send.add(btn_call);
        
        this.jp_singleSendCall = new JPanel();
        this.jp_singleSendCall.setLayout(new BorderLayout());
        this.jp_singleSendCall.add(jp_showSendCall, BorderLayout.CENTER);
        this.jp_singleSendCall.add(jp_send, BorderLayout.SOUTH);
    }
    //选中用户信息后显示在单个用户信息模块中
    private void initSingleCallInfoModule() {
    	JLabel lbl_from, lbl_to, lbl_times, lbl_date,lbl_type,lbl_space;
    	
    	JPanel jp_showViewConsumption=new JPanel();
    	jp_showViewConsumption.setLayout(new GridLayout(2, 4));
    	lbl_from = new JLabel("发送者:");
    	lbl_from.setHorizontalAlignment(JLabel.CENTER);
    	lbl_to = new JLabel("接收者:");
    	lbl_to.setHorizontalAlignment(JLabel.CENTER);
    	lbl_times = new JLabel("通话时间:");
    	lbl_times.setHorizontalAlignment(JLabel.CENTER);
    	lbl_date = new JLabel("通话日期:");
    	lbl_date.setHorizontalAlignment(JLabel.CENTER);
    	lbl_type = new JLabel("通话等级:");
    	lbl_type.setHorizontalAlignment(JLabel.CENTER);
    	lbl_space = new JLabel();
    	
    	txt_sender = new JTextField(20);
    	txt_sender.setEditable(false);
    	txt_recver = new JTextField(20);
    	txt_recver.setEditable(false);
    	txt_times = new JTextField(20);
    	txt_times.setEditable(false);
    	txt_dat = new JTextField(20);
    	txt_dat.setEditable(false);
    	txt_level = new JTextField(20);
    	txt_level.setEditable(false);
        
    	
    	jp_showViewConsumption.add(lbl_from);
    	jp_showViewConsumption.add(txt_sender);
    	jp_showViewConsumption.add(lbl_to);
    	jp_showViewConsumption.add(txt_recver);
    	jp_showViewConsumption.add(lbl_times);
    	jp_showViewConsumption.add(txt_times);
    	jp_showViewConsumption.add(lbl_date);
    	jp_showViewConsumption.add(txt_dat);
    	jp_showViewConsumption.add(lbl_type);
    	jp_showViewConsumption.add(txt_level);
    	jp_showViewConsumption.add(lbl_space);
    	jp_showViewConsumption.add(lbl_space);
    	
        this.jp_singleCallInfo = new JPanel();
        this.jp_singleCallInfo.setLayout(new BorderLayout());
        this.jp_singleCallInfo.add(jp_showViewConsumption, BorderLayout.CENTER);
    }
    private void initSingleAddMoneyModule() {
    	
        
    	JLabel lbl_send, lbl_money,lbl_add, lbl_space;
    	
    	JPanel jp_showAddMoney=new JPanel();
    	jp_showAddMoney.setLayout(new GridLayout(2, 4));
    	lbl_send = new JLabel("手机号:");
    	lbl_send.setHorizontalAlignment(JLabel.CENTER);
    	lbl_send.setFont(new java.awt.Font("Serif",0,20));
    	
        lbl_money = new JLabel("余额:");
        lbl_money.setHorizontalAlignment(JLabel.CENTER);
        lbl_money.setFont(new java.awt.Font("Serif",0,20));
        
        lbl_add = new JLabel("充值金额:");
        lbl_add.setHorizontalAlignment(JLabel.CENTER);
        lbl_add.setFont(new java.awt.Font("Serif",0,20));
        
        lbl_space=new JLabel();
        
        txt_numbers = new JTextField(18);
        txt_numbers.setFont(new java.awt.Font("Serif",0,30));
        txt_numbers.setEditable(false);
        txt_numbers.setText(tel);
        txt_moneyc = new JTextField(18);
        txt_moneyc.setFont(new java.awt.Font("Serif",0,30));
        txt_moneyc.setEditable(false);
        txt_add = new JTextField(18);
        txt_add.setFont(new java.awt.Font("Serif",0,30));
        
        jp_showAddMoney.add(lbl_send);
        jp_showAddMoney.add(txt_numbers);
        jp_showAddMoney.add(lbl_money);
        jp_showAddMoney.add(txt_moneyc);
        jp_showAddMoney.add(lbl_add);
        jp_showAddMoney.add(txt_add);
        jp_showAddMoney.add(lbl_space);
        jp_showAddMoney.add(lbl_space);
        
        this.updateBalance();
        btn_add = new JButton("确认充值");
        btn_add.addActionListener(this);
        JPanel jp_add = new JPanel();
        jp_add.add(btn_add);
        
        this.jp_singleAddMoney = new JPanel();
        this.jp_singleAddMoney.setLayout(new BorderLayout());
        this.jp_singleAddMoney.add(jp_showAddMoney, BorderLayout.CENTER);
        this.jp_singleAddMoney.add(jp_add, BorderLayout.SOUTH);
        this.updateBalance();
        
    }
    private void showSelectedUserInfo() {
        int row = userTable.getSelectedRow();
        Object[] value = new Object[cols_user.length];
        for (int i = 0; i < cols_user.length; i++) {
            value[i] = userTable.getModel().getValueAt(row, i);
        }
        txt_tel.setText(value[0].toString());
        txt_password.setText("");
        txt_name.setText(value[2].toString());
        this.old_tel=value[0].toString();
        com_sex.setSelectedItem(value[3].toString());
        com_userType.setSelectedItem(value[4].toString());
        if(com_userType.getSelectedIndex()==2) {
        	com_userType.enable(false);
        }
        else if(pow<2) {
        	com_userType.enable(false);
        }
        else {
        	com_userType.enable(true);
        }
        com_idType.setSelectedItem(value[5].toString());
        txt_id.setText(value[6].toString());
        txt_balance.setText(value[7].toString());
       
    }
    
    private void showSelectedServiceInfo() {
        int row = serviceTable.getSelectedRow();
        Object[] value = new Object[cols_service.length];
        for (int i = 0; i < cols_service.length; i++) {
            value[i] = serviceTable.getModel().getValueAt(row, i);
        }
        txt_service.setText((String) value[0]);
        txt_price.setText(String.valueOf(value[1]));
        
    }
    private void showSelectedMessageInfo() {
        int row = messageTable.getSelectedRow();
        Object[] value = new Object[cols_message.length];
        for (int i = 0; i < cols_message.length; i++) {
            value[i] = messageTable.getModel().getValueAt(row, i);
        }
        txt_from.setText((String) value[0]);
        txt_to.setText((String) value[1]);
        txt_message.setText((String) value[2]);
        
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//日期格式
        txt_date.setText(sformat.format(value[3]));
    }
    private void showSelectedConsumptionInfo() {
    	int row = consumptionTable.getSelectedRow();
        Object[] value = new Object[cols_consumption.length];
        for (int i = 0; i < cols_consumption.length; i++) {
            value[i] = consumptionTable.getModel().getValueAt(row, i);
        }
        txt_telnumber.setText((String) value[0]);
        txt_consumption.setText((String.valueOf(value[1])));
        txt_type.setText((String) value[2]);
        txt_count.setText(String.valueOf(value[3]));
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//日期格式
        txt_cdate.setText(sformat.format(value[4]));
        txt_usertype.setText((String)value[5]);
    }
    private void showSelectedCallInfo(){
    	int row = callTable.getSelectedRow();
        Object[] value = new Object[cols_call.length];
        for (int i = 0; i < cols_call.length; i++) {
            value[i] = callTable.getModel().getValueAt(row, i);
        }
        txt_sender.setText((String) value[0]);
        txt_recver.setText((String.valueOf(value[1])));
        txt_times.setText(String.valueOf(value[2]));
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//日期格式
        txt_dat.setText(sformat.format(value[3]));
        txt_level.setText(String.valueOf(value[4]));
    }
    public void actionPerformed(ActionEvent e) {
        
        //用户服务事件
        if (e.getSource() == menuitem_usersInfo) {
            this.updateUserTable();
            jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(new JScrollPane(userTable), BorderLayout.CENTER);
            jp_main.add(jp_singleUserInfo, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        }
        else if(e.getSource()==menuitem_setPrice) {
        	//services = ServiceDao.serviceQueryAll();
            this.updateServiceTable();
            jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
            jp_main.add(jp_singleServiceInfo, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        	
        }
        else if(e.getSource()==menuitem_ViewMessage) {
        	//messages = MessageDao.messageQueryAll(tel,pow);
            this.updateMessageTable();
            jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(new JScrollPane(messageTable), BorderLayout.CENTER);
            jp_main.add(jp_singleMessageInfo, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        }
        else if(e.getSource()==menuitem_SendMessage) {
        	this.updateBalance();
        	jp_main.removeAll();
        	jp_main.setLayout(new BorderLayout());
        	jp_main.add(jp_singleSendMessage, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        }
        else if(e.getSource()==menuitem_ViewConsumption) {
        	//consumptions = ConsumptionDao.consumptionQueryAll(tel, pow);
        	this.updateComsumptionTable();
            jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(new JScrollPane(consumptionTable), BorderLayout.CENTER);
            jp_main.add(jp_singleConsumptionInfo, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        }
        else if(e.getSource()==menuitem_SendCall) {
        	jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(jp_singleSendCall, BorderLayout.SOUTH);
            jp_main.updateUI();
        } else if(e.getSource()==menuitem_ViewCall) {
        	this.updateCallTable();
            jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
            jp_main.add(new JScrollPane(callTable), BorderLayout.CENTER);
            jp_main.add(jp_singleCallInfo, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        } else if(e.getSource()==menuitem_AddMoney) {
        	jp_main.removeAll();
            jp_main.setLayout(new BorderLayout());
           // jp_main.add(new JScrollPane(callTable), BorderLayout.CENTER);
            jp_main.add(jp_singleAddMoney, BorderLayout.SOUTH);
            jp_main.updateUI();
            return;
        }
        //修改用户信息
        if (e.getSource() == btn_confirm) {
            this.updateUser();
            this.updateUserTable();//更新用户表
            return;
        }
        //删除用户
        if (e.getSource() == btn_deleteUser) {
        	
            int r = 1;
            boolean flag=false;
            if(self_user.equals(txt_name.getText())||self_user.equals(txt_tel.getText())) {
            	flag=true;
            	r=JOptionPane.showConfirmDialog(this, "确认注销本账号？", "提醒", JOptionPane.OK_CANCEL_OPTION);
            }
            else {
            	r=JOptionPane.showConfirmDialog(this, "确认删除此用户？", "提醒", JOptionPane.OK_CANCEL_OPTION);
            }
			System.out.println(r);//确认则r返回为0
            if (r == 0) {
                this.deleteUser(flag);
               
                this.updateUserTable();//更新用户表
                return;
            }
        } 
        if(e.getSource() == btn_yes) {
        	int r = JOptionPane.showConfirmDialog(this, "确认更改此条目的价格？", "提醒", JOptionPane.OK_CANCEL_OPTION);
        	if(r==0) {
        		if(pow<2) {
        			JOptionPane.showMessageDialog(this, "对不起，您无权修改！请联系root管理员更改！", "提醒", JOptionPane.PLAIN_MESSAGE);
        			return;
        		}
        		this.updatePrice();
        		this.updateServiceTable();
        		return;
        	}
        }
        if(e.getSource() == btn_send) {
        	String message=txt_info.getText(),recv=txt_recv.getText();
        	if(message.equals("")||recv.equals("")) {
        		JOptionPane.showMessageDialog(this, "输入均不应该为空", "提醒", JOptionPane.PLAIN_MESSAGE);
        		return;
        	}
        	if(!CheckString.Checkallnumber(recv)) {
        		JOptionPane.showMessageDialog(this, "手机号应当都为数字", "提醒", JOptionPane.PLAIN_MESSAGE);
        		return ;
        	}
        	if(CheckString.CheckSQL(message)) {
        		JOptionPane.showMessageDialog(this, "信息中不能包含以下字符(',#)", "提醒", JOptionPane.PLAIN_MESSAGE);
        		return ;
        	}
        	double price=ServiceDao.QueryPrice(0);
        	double consumption=price*message.length();
        	String info="本条信息将会花费"+consumption+"，请确您的收件人："+recv;
        	int k = JOptionPane.showConfirmDialog(this, info, "提醒", JOptionPane.OK_CANCEL_OPTION);
        	if(k==0) {
        		this.updateBalance();
        		if (Double.valueOf(txt_money.getText())<consumption) {
        			JOptionPane.showMessageDialog(this, "余额不足，请充值", "提醒", JOptionPane.PLAIN_MESSAGE);
        			return ;
        		}
        		
        		boolean res=MessageDao.add(txt_send.getText(), recv, message, System.currentTimeMillis());
        		if(!res) {
        			JOptionPane.showMessageDialog(this, "发送失败，请重试", "提醒", JOptionPane.PLAIN_MESSAGE);
        			return ;
        		}
        		UsersDao.updateBalance(tel, Double.valueOf(txt_money.getText())-consumption);
        		ConsumptionDao.add(tel, consumption, 0, message.length(), System.currentTimeMillis(), pow);
        		this.updateBalance();
        	}
        	
        }
        if(e.getSource() == btn_call) {
        	String recver = txt_recv1.getText();
        	if(!is_in_call) {
        		if(recver.equals("")) {
        			JOptionPane.showMessageDialog(this, "输入不应该为空", "提醒", JOptionPane.PLAIN_MESSAGE);
        			return ;
        		}
        		if(!CheckString.Checkallnumber(recver)) {
        			JOptionPane.showMessageDialog(this, "电话号码应该只包含数字", "提醒", JOptionPane.PLAIN_MESSAGE);
        			return ;
        		}
        		double price=ServiceDao.QueryPrice(1);
        		
        		int k = JOptionPane.showConfirmDialog(this, "本次通话单价为"+price+"每秒，您的通话对象为："+recver, "提醒", JOptionPane.OK_CANCEL_OPTION);
        		if(k==0) {
        			if(Double.valueOf(txt_money.getText())<0) {
        				JOptionPane.showConfirmDialog(this, "您的余额不足，请尽快充值", "提醒", JOptionPane.OK_CANCEL_OPTION);
        				return ;
        			}
        			this.time=System.currentTimeMillis();
        			is_in_call=true;
        			txt_recv1.setEditable(false);
        			btn_call.setText("挂断");
        			lbl_tonghua.setVisible(true);
        		}
        	}
        	else {
        		long x=System.currentTimeMillis();
        		btn_call.setText("拨号");
        		int times=(int)(x-this.time)/1000+1;
        		//System.out.println(times/1000);roo
        		is_in_call=false;
        		txt_recv1.setEditable(true);
        		double money=times*ServiceDao.QueryPrice(1);
        		String info="您本次通话"+times+"秒，共计消费"+String.valueOf(money)+"元";
        		lbl_tonghua.setVisible(false);
        		CallDao.add(tel, recver, times, this.time,pow);
        		ConsumptionDao.add(tel, money, 1, times, this.time, pow);
        		double now_balance=Double.valueOf(txt_money.getText());
        		UsersDao.updateBalance(tel, now_balance-money);
        		this.updateBalance();
        		JOptionPane.showMessageDialog(this, info, "提醒", JOptionPane.PLAIN_MESSAGE);
        		
        	}
        }
        if(e.getSource()==btn_add) {
        	String money=txt_add.getText();
        	if(money.equals("")) {
        		JOptionPane.showMessageDialog(this,"输入不应该为空！", "提醒", JOptionPane.PLAIN_MESSAGE);
        		return;
        	}
        	if(!CheckString.Checkallnumber(money)) {
        		JOptionPane.showMessageDialog(this,"输入不应该含有数字以外的其它字符", "提醒", JOptionPane.PLAIN_MESSAGE);
        		return;
        	}
        	int add_money=Integer.valueOf(money);
        	int k = JOptionPane.showConfirmDialog(this, "您将充值"+add_money+"元，请将人民币放到指定位置", "提醒", JOptionPane.OK_CANCEL_OPTION);
        	if(k==0) {
        		this.updateBalance();
        		double true_money=Double.valueOf(txt_moneyc.getText());
        		UsersDao.updateBalance(tel, true_money+add_money);
        		ConsumptionDao.add(tel, add_money, 3, add_money, System.currentTimeMillis(),pow);
        		JOptionPane.showMessageDialog(this,"充值成功！", "提醒", JOptionPane.PLAIN_MESSAGE);
        		this.updateBalance();
        	}
        }
        //退出系统
        if (e.getSource() == menuitem_exit) {
            int k = JOptionPane.showConfirmDialog(this, "是否退出系统？", "提醒", JOptionPane.OK_CANCEL_OPTION);
            if (k == 0) {
                dispose();
            }
        }
    }
    
    //更新主面板的表格和信息模块
    private void updateMainPanel(JTable tablePanel, JPanel panel) {
        jp_main.removeAll();
        jp_main.add(tablePanel, BorderLayout.CENTER);
        jp_main.add(panel, BorderLayout.SOUTH);
        jp_main.updateUI();
    }
    
    //生成所有用户信息表格
    private void updateUserTable() {
        ArrayList<Users> users = UsersDao.userQueryAll(pow,self_user);
        
        
        
        rows_user = new Object[users.size()][cols_user.length];
        int k = 0;
        while (k < users.size()) {
            String usertype = "用户";
            if (users.get(k).getUser_type() == 1) {
                usertype = "管理员";
            } else if (users.get(k).getUser_type() == 2) {
            	usertype = "超级管理员";
            }
            String sex="男";
            if(users.get(k).getSex()==1)sex="女";
            String idtype="中华人民共和国居民身份证";
            if (users.get(k).getId_type() == 1) {
                idtype = "港澳台居民身份证";
            } else if (users.get(k).getId_type() == 2) {
            	idtype = "护照";
            }
            rows_user[k][0] = users.get(k).getPhone_number();
            rows_user[k][1] = "******";//因为采取了md加密字串,因此密码默认不显示,但是具有修改权限,同时也保证了客户隐私
            rows_user[k][2] = users.get(k).getUsername();
            rows_user[k][3] = sex;
            rows_user[k][4] = usertype;
            rows_user[k][5] = idtype;
            rows_user[k][6] = users.get(k).getId_number();
            rows_user[k][7] = users.get(k).getBalance();
            k++;
        }
        userTable.setModel(new DefaultTableModel(rows_user, cols_user));
    }
    
    private void updateServiceTable() {
    	services = ServiceDao.serviceQueryAll();
        rows_service = new Object[services.size()][cols_service.length];
        int j = 0;
        for (Services service : services) {
            String ser="短信";
            if(service.getName()==1) {
            	ser="电话";
            }
            else if(service.getName()==2) {
            	ser="流量";
            }
            rows_service[j][0] = ser;
            rows_service[j][1] = service.getPrice();
            ++j;
        }
        serviceTable.setModel(new DefaultTableModel(rows_service, cols_service));
    }
    private void updateMessageTable() {
    	messages = MessageDao.messageQueryAll(tel,pow);
        rows_message = new Object[messages.size()][cols_message.length];
        int j = 0;
        for (Messages message : messages) {
            rows_message[j][0] = message.getFrom();
            rows_message[j][1] = message.getTo();
            rows_message[j][2] = message.getMessage();
            rows_message[j][3] = message.getDate();
            ++j;
        }
        messageTable.setModel(new DefaultTableModel(rows_message, cols_message));
    }
    private void updateComsumptionTable() {
    	consumptions = ConsumptionDao.consumptionQueryAll(tel,pow);
        rows_consumption = new Object[consumptions.size()][cols_consumption.length];
        int j = 0;
        for (Consumptions consumption : consumptions) {
        	int type=consumption.getType(),usertype=consumption.getUsertype();
        	String service_type="短信",user="用户";
        	if(type==1) {
        		service_type="电话";
        	}
        	else if(type==2) {
        		service_type="流量";
        	}
        	else if(type==3) {
        		service_type="充值";
        	}
        	else if(type==4) {
        		service_type="换号码";
        	}
        	if(usertype==1) {
        		user="管理员";
        	}
        	else if(usertype==2) {
        		user="超级管理员";
        	}
        	rows_consumption[j][0] = consumption.getTel();
        	rows_consumption[j][1] = consumption.getConsumption();
        	rows_consumption[j][2] = service_type;
        	rows_consumption[j][3] = consumption.getCount();
        	rows_consumption[j][4] = consumption.getDate();
        	rows_consumption[j][5] = user;
            ++j;
        }
        consumptionTable.setModel(new DefaultTableModel(rows_consumption, cols_consumption));
    }
    private void updateCallTable() {
    	calls = CallDao.callQueryAll(tel,pow);
        rows_call = new Object[calls.size()][cols_call.length];
        int j = 0;
        for (Calls call : calls) {
        	rows_call[j][0] = call.getFrom();
        	rows_call[j][1] = call.getTo();
        	rows_call[j][2] = call.getTimes();
        	rows_call[j][3] = call.getDate();
        	rows_call[j][4] = call.getPow();
            ++j;
        }
        callTable.setModel(new DefaultTableModel(rows_call, cols_call));
    }
    private void updateBalance() {
    	String money=String.valueOf(UsersDao.userQuerybalance(tel));
    	txt_money.setText(money);
    	txt_moneys.setText(money);
    	txt_moneyc.setText(money);
    }
}
