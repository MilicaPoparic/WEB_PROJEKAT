package beans.cloudprovider;

import java.util.Date;

public class ChangeActivity {
	public String newStart;
	public Date start;
	public String newEnd;
	public Date end;
	public ChangeActivity(String newStart, Date start, String newEnd, Date end) {
		super();
		this.newStart = newStart;
		this.start = start;
		this.newEnd = newEnd;
		this.end = end;
	}
	
}
