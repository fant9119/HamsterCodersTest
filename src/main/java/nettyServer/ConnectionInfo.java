package nettyServer;

import java.util.Date;

public class ConnectionInfo {
	
	private String srcIp;
	private String uri;
	private Date date;
	private long sentBytes;
	private long receivedBytes;
	private int speed;
		
	public ConnectionInfo(String srcIp, String uri, Date date, 
			long sentBytes, long receivedBytes, int speed) {
		
		this.srcIp = srcIp;
		this.uri = uri;
		this.date = date;
		this.sentBytes = sentBytes;
		this.receivedBytes = receivedBytes;
		this.speed = speed;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getSentBytes() {
		return sentBytes;
	}

	public void setSentBytes(long sentBytes) {
		this.sentBytes = sentBytes;
	}

	public long getReceivedBytes() {
		return receivedBytes;
	}

	public void setReceivedBytes(long receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
