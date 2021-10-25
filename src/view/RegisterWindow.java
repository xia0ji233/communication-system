package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DAO.UsersDAO;
import entity.CheckString;
import entity.Id;
import entity.md5;


/**
 * @author: xiaoji233
 * @Description: TODO 注册界面
 */
public class RegisterWindow extends JFrame implements ActionListener {
	private JTextField txt_tel, txt_name, txt_id;
	private JPasswordField txt_pwd1, txt_pwd2;
	private JComboBox<String> com_sex, com_idType;
	private JRadioButton rbtn_clause;
	private JLabel lbl_clause;
	private JButton btn_confirm, btn_cancel;
    private JPanel jp_show, jp_input, jp_bottom;
    
    public RegisterWindow(String s) {
        //设置界面题头和符号
        setTitle(s);
        ImageIcon icon = new ImageIcon("picture/logo.jpg");
        setIconImage(icon.getImage());
        //初始化顶部显示信息模块
        initTopShowInfo();

        //初始化中部用户填写信息模块
        initMidUserInfo();

        //初始化底部阅读条约与界面功能按钮模块
        initBottomModule();

        //主窗体添加以上模块
        this.add(jp_show, BorderLayout.NORTH);
        this.add(jp_input, BorderLayout.CENTER);
        this.add(jp_bottom, BorderLayout.SOUTH);
        this.validate();
        this.setVisible(true);
        this.setSize(450, 400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    //实现监听事件
    public void actionPerformed(ActionEvent e) {
        //获取到输入信息
        String tel = txt_tel.getText().trim();
        String pwd1 = new String(txt_pwd1.getPassword());
        String pwd2 = new String(txt_pwd2.getPassword());
        String username = txt_name.getText().trim();
        int sex = com_sex.getSelectedIndex();
        String id = txt_id.getText().trim();
        int idType = com_idType.getSelectedIndex();

        //注册按钮事件
        if (e.getSource() == btn_confirm) {
            if (!inputInfoValid(tel, pwd1, pwd2, username, id)) {
                return;//如果输入无效，直接返回
            }
            UsersDAO usersDao = new UsersDAO();
            if (usersDao.userExisted(tel,username)) { //如果此账号已存在
                JOptionPane.showMessageDialog(this, "该用户名或手机号已经注册！", "提醒", JOptionPane.PLAIN_MESSAGE);
                System.out.println(usersDao.userExisted(tel,username)+"注册方法");
                return;
            }
            //如果用户不存在则开始注册
            boolean success = usersDao.register(tel, md5.getMD5(pwd1), username, sex, 0, id, idType);
            if (success) { //如果注册成功
                JOptionPane.showMessageDialog(this, "注册成功，请登录！", "提醒", JOptionPane.PLAIN_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "注册失败！", "提醒", JOptionPane.PLAIN_MESSAGE);
            }
            return;
        }
        //取消按钮事件，关闭当前窗口
        if (e.getSource() == btn_cancel) {
            this.dispose();
        }
    }
    //初始化顶部信息显示模块
    private void initTopShowInfo() {
        //界面上部显示信息
        JLabel lbl_show = new JLabel("请完善资料");
        lbl_show.setFont(new Font("楷体", Font.PLAIN, 30));
        jp_show = new JPanel();
        jp_show.add(lbl_show);
    }

    //初始化中部用户填写信息模块
    private void initMidUserInfo() {
        //初始化所要填写信息的标签
        //1.手机号码标签
        JLabel lbl_tel = new JLabel("手机号码:");
        lbl_tel.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_tel.setHorizontalAlignment(JLabel.CENTER);
        //2.设置密码标签
        JLabel lbl_setPassword = new JLabel("设置密码:");
        lbl_setPassword.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_setPassword.setHorizontalAlignment(JLabel.CENTER);
        //3.确认密码标签
        JLabel lbl_confirmPassword = new JLabel("确认密码:");
        lbl_confirmPassword.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_confirmPassword.setHorizontalAlignment(JLabel.CENTER);
        //4.姓名标签
        JLabel lbl_name = new JLabel("用 户 名:");
        lbl_name.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_name.setHorizontalAlignment(JLabel.CENTER);
        //5.性别标签
        JLabel lbl_sex = new JLabel("性    别:");
        lbl_sex.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_sex.setHorizontalAlignment(JLabel.CENTER);
        //6.证件号码标签
        JLabel lbl_certificateNumber = new JLabel("证件号码:");
        lbl_certificateNumber.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_certificateNumber.setHorizontalAlignment(JLabel.CENTER);
        //7.证件类型标签
        JLabel lbl_certificateType = new JLabel("证件类型:");
        lbl_certificateType.setFont(new Font("楷体", Font.BOLD, 20));
        lbl_certificateType.setHorizontalAlignment(JLabel.CENTER);

        //初始化所要填写信息的输入框
        txt_tel = new JTextField(11);
        txt_pwd1 = new JPasswordField(15);
        txt_pwd2 = new JPasswordField(15);
        txt_name = new JTextField(10);
        com_sex = new JComboBox<>(new String[]{"男", "女"});
        String[] idType = {"中华人民共和国居民身份证", "港澳台居民身份证", "护照"};
        com_idType = new JComboBox<>(idType);
        txt_id = new JTextField(20);

        //初始化中部填写信息面板
        jp_input = new JPanel();
        jp_input.setLayout(new GridLayout(7, 2));
        jp_input.add(lbl_tel);
        jp_input.add(txt_tel);
        jp_input.add(lbl_setPassword);
        jp_input.add(txt_pwd1);
        jp_input.add(lbl_confirmPassword);
        jp_input.add(txt_pwd2);
        jp_input.add(lbl_name);
        jp_input.add(txt_name);
        jp_input.add(lbl_sex);
        jp_input.add(com_sex);
        jp_input.add(lbl_certificateNumber);
        jp_input.add(txt_id);
        jp_input.add(lbl_certificateType);
        jp_input.add(com_idType);
    }

    //初始化底部阅读条约与界面功能按钮模块
    private void initBottomModule(){
        //阅读条约
        rbtn_clause = new JRadioButton("我已阅读并同意遵守");
        lbl_clause = new JLabel("《中国移动通信服务中心网站服务条款》");
        lbl_clause.setForeground(Color.blue);
        lbl_clause.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                lbl_clause.setForeground(Color.red);
                JOptionPane.showMessageDialog(null, "最终解释权归@xiaoji233&@H3所有！！！", "霸王条款", JOptionPane.PLAIN_MESSAGE);
            }
        });
        JPanel jp_clause = new JPanel();
        jp_clause.add(rbtn_clause);
        jp_clause.add(lbl_clause);

        //功能按钮
        btn_confirm = new JButton("确认");
        btn_confirm.addActionListener(this);
        btn_cancel = new JButton("取消");
        JPanel jp_clause_confirm = new JPanel();
        jp_clause_confirm.add(btn_confirm);
        jp_clause_confirm.add(btn_cancel);

        //初始化底部模块模板
        jp_bottom = new JPanel();
        jp_bottom.setLayout(new GridLayout(2, 1));
        jp_bottom.add(jp_clause);
        jp_bottom.add(jp_clause_confirm);
    }
    
    //检查输入信息的有效性
    private boolean inputInfoValid(String tel, String pwd1, String pwd2, String username, String id){
    	//检查信息是否完善
        if (tel.equals("") || pwd1.equals("") || pwd1.equals("") ||
                username.equals("") || id.equals("")) {
            JOptionPane.showMessageDialog(this, "输入均不能为空", "注意", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        //判断手机号是否都为数字，且为11位
        if(tel.length()!=11) {
        	JOptionPane.showMessageDialog(this, "手动注册手机号必须位11位,如需要获取靓号请联系root管理员更改，但是在更改之前不要有任何消费"
        			, "注意", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        
    	if(!CheckString.Checkallnumber(tel)) {
    		JOptionPane.showMessageDialog(this, "手机号必须只含数字", "注意", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
        
        if(username.length()<=5) {
        	JOptionPane.showMessageDialog(this, "手动注册的用户名不得少于五位，如果有需要请联系数据库管理员", "注意", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
      //用户名过滤特殊字符且接近C变量命名规则
        char first_ch=username.charAt(0);
        if((first_ch<'A'||first_ch>'Z')&&(first_ch<'a'||first_ch>'z')) {
        	JOptionPane.showMessageDialog(this, "用户名必须以字母为开头", "注意", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        
        for(int i=1;i<username.length();i++) {
        	char ch=username.charAt(i);
        	if(!(ch>='A'&&ch<='Z'||ch>='a'&&ch<='z'||ch>='0'&&ch<='9')) {
        		JOptionPane.showMessageDialog(this, "用户名不允许包含特殊字符", "注意", JOptionPane.WARNING_MESSAGE);
        		return false;
        	}
        }
        
        if(pwd1.length()<8) {
        	JOptionPane.showMessageDialog(this, "为了您的账户安全，密码不得少于八位", "注意", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        //检测密码强度顺带过滤特殊字符防止注入
        boolean include_num=false,include_ch=false,include_sp=false;
        for(int i=0;i<pwd1.length();i++) {
        	char ch=pwd1.charAt(i);
        	if(ch>='0'&&ch<='9') {
        		include_num=true;
        	}
        	else if(ch>='A'&&ch<='Z'||ch>='a'&&ch<='z') {
        		include_ch=true;
        	}
        	else {
        		include_sp=true;
        	}
        }
        
        if(!(include_num&&(include_ch||include_sp))) {
        	JOptionPane.showMessageDialog(this, "密码必须包含数字和字母或者特殊字符", "注意", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        //防止错误输入密码
        if (!pwd1.equals(pwd2)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致", "注意", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        //目前只开通了中华人民共和国居民身份证的校验
        if(com_idType.getSelectedIndex()==0) {
        	String info=Id.IDCardValidate(id);
        	if (!info.equals("身份证验证通过")) {
        		JOptionPane.showMessageDialog(this, info, "注意", JOptionPane.WARNING_MESSAGE);
        		return false;
        	}
        }
        
        //服务条款的阅读
        if (!rbtn_clause.isSelected()) {
            JOptionPane.showMessageDialog(this, "请先同意服务条款", "注意", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

}

