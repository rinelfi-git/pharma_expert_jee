package mg.adequa.utils;

import java.util.Calendar;
import java.util.Date;

public class MyDate extends Date {
	
	public MyDate() {super();}
	
	public Date reculerMois(int nombreDeMmois) {
		Date parent = new Date(super.getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parent);
		calendar.add(Calendar.MONTH, -nombreDeMmois);
		return calendar.getTime();
	}
}
