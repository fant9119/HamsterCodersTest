package nettyServer;

import java.util.Date;

/**
 * This class contains information about connection:
 * ip address, uri, date of creation, sent and received bytes, speed (bytes/s).
 * <b>Autogenerated methods hashCode() and equals() use only srcIp and uri fields!</b>.
 * 
 * @author rk
 *
 */
public class ConnectionInfo {
	
	private String srcIp;
	private String uri;
	private Date date;
	private long sentBytes;
	private long receivedBytes;
	private int speed;
		
	public ConnectionInfo() {
		
	}
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((srcIp == null) ? 0 : srcIp.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionInfo other = (ConnectionInfo) obj;
		if (srcIp == null) {
			if (other.srcIp != null)
				return false;
		} else if (!srcIp.equals(other.srcIp))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [srcIp=" + srcIp + ", uri=" + uri + ", date=" + date + ", sentBytes=" + sentBytes
				+ ", receivedBytes=" + receivedBytes + ", speed=" + speed + "]";
	}
	
	
}
