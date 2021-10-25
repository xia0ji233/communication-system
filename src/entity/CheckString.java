package entity;

public class CheckString {
	public static boolean CheckSQL(String s) {//判断有无sql注入
		for(int i=0;i<s.length();i++) {
			char ch=s.charAt(i);
			if(ch=='\''||ch=='#') {
				return true;
			}
		}
		return false;
	}
	public static boolean Checkallnumber(String s) {//是否都为数字
		for(int i=0;i<s.length();i++) {
			char ch=s.charAt(i);
			if(ch<'0'||ch>'9') {
				return false;
			}
		}
		return true;
	}
}
