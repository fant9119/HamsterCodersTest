package nettyServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * This class contains all statistics. It is build with
 * double checked locking & volatile Singelton pattern.
 * 
 * @author rk
 *
 */
public class ServerStatistics {

	private volatile static ServerStatistics instance;
	
	/**
	 * All current active (open) connections. When Channel becomes inactive it
	 * removes from this group automatically.
	 * 
	 * @see {@link ChannelGroup}
	 */
	private ChannelGroup connections;
	
	/**
	 * Log that contains not more than 16 records.
	 */
	private Queue<ConnectionInfo> connectionsLog;
	
	/**
	 * The number of all requests
	 */
	private AtomicLong allRequests;
	
	/**
	 * Stores all redirected URL's and number of redirection (key = URL)
	 */
	private Map<String, Integer> redirectsStatistics;
	
	/**
	 * Contains information about ip (ip, number of requests, the time of last request). (key = ip)
	 * 
	 * @see {@link IpInfo}
	 */
	private Map<String, IpInfo> ipStatistics;
	
	private Set<ConnectionInfo> uniqueRequests;
	
	
	private ServerStatistics() {
		connections = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		connectionsLog = new ConcurrentLinkedQueue<>();
		allRequests = new AtomicLong(0);
		redirectsStatistics = new ConcurrentHashMap<>();
		ipStatistics = new ConcurrentHashMap<>();
		uniqueRequests = Collections.synchronizedSet(new HashSet<ConnectionInfo>());
	}
	
	public static ServerStatistics getInstance() {
		if(instance == null) {
			synchronized(ServerStatistics.class) {
				if(instance == null) {
					instance = new ServerStatistics();
				}
			}
		}
		return instance;
	}

	/**
	 * Adds channel to {@link ChannelGroup}.
	 * @param channel - channel to be added
	 * @return true if channel was added successful
	 * 
	 * @see ChannelGroup add(Channel ch) method
	 */
	public boolean addChannel(Channel channel) {
		return connections.add(channel);		
	}
	
	/**
	 * Stores the last 16 added ConnectionInfo and collects unique requests (one per IP).
	 * @param info ConnectionInfo to be stored.
	 * 
	 * @see {@link ConnectionInfo}
	 */
	public void addConectionToLog(ConnectionInfo info) {
		uniqueRequests.add(info);
		connectionsLog.offer(info);
		while(connectionsLog.size() > 16) connectionsLog.poll();
	}
	
	/**
	 * Increments the total number of requests
	 */
	public void addRequest() {
		allRequests.incrementAndGet();
	}

	/**
	 * Increment the total number of redirects made to this URL
	 * or stores URL if it is the first redirection.
	 * @param url
	 */
	public synchronized void addRedirectResponse(String url) {
		if(redirectsStatistics.containsKey(url)) {
			int count = redirectsStatistics.get(url);
			redirectsStatistics.put(url, ++count);
			return;
		}
		redirectsStatistics.put(url, 1);
	}
	
	/**
	 * Stores the information about ip or updates it.
	 * @param ip
	 * 
	 * @see {@link IpInfo}, IpInfo update() method
	 */
	public synchronized void addIp(String ip) {
		if(ipStatistics.containsKey(ip)) {
			IpInfo ipInfo = ipStatistics.get(ip);
			ipInfo.update();
			return;
		}
		ipStatistics.put(ip, new IpInfo(ip));
	}
	
	/**
	 * @return the number of open connections
	 */
	public int getOpenConnectionsCount() {
		return connections.size();
	}
	
	/**
	 * Makes statistics for status reponse.
	 * @return string representation of html status page
	 */
	public String makeStatistics() {
		return StatusPage.getStatusPage();
	}

	/**
	 * @return the number of all requests
	 */
	public long getAllRequests() {
		return allRequests.get();
	}

	public Queue<ConnectionInfo> getConnectionsLog() {
		return connectionsLog;
	}

	public Map<String, Integer> getRedirectsStatistics() {
		return redirectsStatistics;
	}

	public Map<String, IpInfo> getIpStatistics() {
		return ipStatistics;
	}	
	
	public int getUniqueRequestsCount() {
		return uniqueRequests.size();
	}
}
