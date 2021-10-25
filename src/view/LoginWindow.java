package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import entity.*;
import DAO.*;
import dbutil.*;

/**
 * @author: xiaoji233
 * @Description: TODO 登录界面
 */
public class LoginWindow extends JFrame implements ActionListener{
    
    	private JTextField txt_tel;                           //用户电话
        private JPasswordField txt_password;                  //用户密码
        private JComboBox<String> com_role;                   //用户角色类型
        private JButton btn_login, btn_register, btn_clear;  //功能按钮
        private Users user; //登录的用户信息，在用户界面中用到


        public LoginWindow(String title) {
            //设置登录界面题头和符号
            setTitle(title);
            String iconSrc = "picture/logo.jpg";
            ImageIcon icon = new ImageIcon(iconSrc);
            setIconImage(icon.getImage());

            //自定义设置主界面主面板的背景
            String bgdSrc = "picture/bg.jpg";
            ImageIcon background = new ImageIcon(bgdSrc);
            Background.setBackgroundPicture(this, background);

            //界面显示信息面板
            JLabel lbl_show = new JLabel("通信系统登陆");
            lbl_show.setForeground(Color.WHITE);
            lbl_show.setFont(new Font("楷体", Font.PLAIN, 65));
            lbl_show.setHorizontalAlignment(JLabel.CENTER);
            JPanel jp_show = new JPanel();
            jp_show.setOpaque(false);
            jp_show.add(lbl_show);

            //用户信息模块
            //1.手机号
            JLabel lbl_tel;
            lbl_tel = new JLabel("手机号或用户名:");
            lbl_tel.setForeground(Color.WHITE);
            lbl_tel.setFont(new Font("楷体", Font.BOLD, 20));
            lbl_tel.setHorizontalAlignment(SwingConstants.CENTER);
            //2.密码
            JLabel lbl_password = new JLabel("密     码:");
            lbl_password.setForeground(Color.WHITE);
            lbl_password.setFont(new Font("楷体", Font.BOLD, 20));
            lbl_password.setHorizontalAlignment(SwingConstants.CENTER);
            //3.身份选择
            JLabel lbl_role = new JLabel("身     份:");
            lbl_role.setForeground(Color.WHITE);
            lbl_role.setFont(new Font("楷体", Font.BOLD, 20));
            lbl_role.setHorizontalAlignment(SwingConstants.CENTER);
            //4.信息输入框
            txt_tel = new JTextField(15);
            txt_password = new JPasswordField(20);
            com_role = new JComboBox<>(new String[]{"用户", "管理员"});
            //5.用户信息面板（排版）
            JPanel jp_userInfo = new JPanel();
            jp_userInfo.setOpaque(false);//将面板背景设计为透明，因为要显示自定义的背景图片
            jp_userInfo.setLayout(new GridLayout(7, 2));
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(lbl_tel);
            jp_userInfo.add(txt_tel);
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(lbl_password);
            jp_userInfo.add(txt_password);
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(lbl_role);
            jp_userInfo.add(com_role);
            jp_userInfo.add(new JLabel());
            jp_userInfo.add(new JLabel());

            // 登录界面功能按钮模块
            //1.登录按钮
            btn_login = new JButton("登录");
            btn_login.setFont(new Font("楷体", Font.PLAIN, 20));
            btn_login.addActionListener(this);
            //2.注册按钮
            btn_register = new JButton("注册");
            btn_register.setFont(new Font("楷体", Font.PLAIN, 20));
            btn_register.addActionListener(this);
            //3.取消按钮
            btn_clear = new JButton("清空");
            btn_clear.setFont(new Font("楷体", Font.PLAIN, 20));
            btn_clear.addActionListener(this);
            //4.功能按钮面板
            JPanel jp_functionBtn = new JPanel();
            jp_functionBtn.setOpaque(false);
            jp_functionBtn.add(btn_login);
            jp_functionBtn.add(btn_register);
            jp_functionBtn.add(btn_clear);

            //设置主面板布局，并添加上面自定义的面板
            this.setLayout(new BorderLayout());
            this.add(jp_show, BorderLayout.NORTH);
            this.add(jp_userInfo, BorderLayout.CENTER);
            this.add(jp_functionBtn, BorderLayout.SOUTH);
            this.validate();
            this.setVisible(true);
            this.setSize(background.getIconWidth(), background.getIconHeight());
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e) {//监听动作
        //登录
        if (e.getSource() == btn_login) {
            String sign = txt_tel.getText().trim();
            String password = new String(txt_password.getPassword());
            int type = com_role.getSelectedIndex();
            if(sign.equals("root")) {//这里特判输入的用户名是否为root，如果是则把type置为2
            	type=2;
            }
            login(sign, password, type);
            return;
        }

        //注册判断
        if (e.getSource() == btn_register) {
            RegisterWindow RegisterWindow = new RegisterWindow("注册为用户");
            return;
        }

        //取消登陆
        if (e.getSource() == btn_clear) {
            txt_tel.setText("");
            txt_password.setText("");
            com_role.setSelectedIndex(0);
        }
    }
    
    private void login(String sign, String password, int type) {
        Icon success = new ImageIcon("picture/success.jpg");
        Icon failed = new ImageIcon("picture/failed.jpg");
        final int USER = 0;
        final int ADMIN = 1;
        final int SUPER = 2;
        //用户信息均不能为空
        if (sign.equals("") || password.equals("")) {
            JOptionPane.showMessageDialog(this, "输入不能为空!", "提醒", JOptionPane.WARNING_MESSAGE, failed);
            return;
        }
        //过滤特殊字符
        if(CheckString.CheckSQL(sign)) {
        	JOptionPane.showMessageDialog(this, "用户名不能含有特殊字符", "注意", JOptionPane.WARNING_MESSAGE, failed);
        	return;
        }
        /*
        if(CheckString.CheckSQL(password)) {
        	JOptionPane.showMessageDialog(this, "密码不能含有以下特殊字符(',#)", "注意", JOptionPane.WARNING_MESSAGE, failed);
    		return;
        }
        */
        //如果用户信息不为空，验证此用户是否存在
        UsersDAO usersDAO = new UsersDAO();
        boolean existed = usersDAO.userValidate(sign, md5.getMD5(password), type);
        if (!existed) {
            JOptionPane.showMessageDialog(this, "用户名或密码有误", "提醒", JOptionPane.WARNING_MESSAGE, failed);
            return;
        }
        //如果用户存在，判断是哪种用户类型，并进入用户界面
        if (type == USER) {
        	
            JOptionPane.showMessageDialog(this, "欢迎您！  " + sign + " 用户！", "登陆成功", JOptionPane.PLAIN_MESSAGE, success);
            new UserWindow("用户",type,sign);
            this.dispose();
        } else if (type == ADMIN) {
            JOptionPane.showMessageDialog(this, "欢迎您！  " + sign + " 管理员！", "登陆成功", JOptionPane.PLAIN_MESSAGE, success);
            new UserWindow("管理员",type,sign);
            this.dispose();
        } else if (type == SUPER) {
        	JOptionPane.showMessageDialog(this, "欢迎您！  root超级管理员！", "登陆成功", JOptionPane.PLAIN_MESSAGE, success);
        	new UserWindow("超级管理员",type,sign);
        	this.dispose();
        }
    }
    
	public static void main(String[] argv) {
		new LoginWindow("通信系统登录界面");
	}
}
