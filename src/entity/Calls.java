package entity;

import java.util.Date;

public class Calls {
	private String from,to;
	private int times,pow;
	private Date date;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getPow() {
		return pow;
	}
	public void setPow(int pow) {
		this.pow = pow;
	}
}
