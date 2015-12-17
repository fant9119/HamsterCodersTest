package nettyServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
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
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Singelton pattern double checked locking & volatile
 * 
 * @author rk
 *
 */
public class ServerStatistics {

	private volatile static ServerStatistics instance;
	
	private ChannelGroup channels;
	//private Map<Integer, FullHttpRequest> requests;
	private Queue<ConnectionInfo> connectionsLog;
	private AtomicLong allRequests;
	private Map<String, Integer> redirectsByUrlCount;
	private Map<String, IpInfo> allUniqueIps;
	
	
	private ServerStatistics() {
		channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		//channels = new DefaultChannelGroup(null);
		//requests = Collections.synchronizedMap(new HashMap<>());
		connectionsLog = new ConcurrentLinkedQueue<>();
		allRequests = new AtomicLong(0);
		redirectsByUrlCount = new ConcurrentHashMap<>();
		allUniqueIps = new ConcurrentHashMap<>();
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
	

	public int getChannelsCount() {
		return channels.size();
	}

	public boolean addChannel(Channel channel) {
		//System.out.println((Channel[])channels.toArray());
		return channels.add(channel);		
	}
	
	public synchronized boolean addConectionToLog(ConnectionInfo info) {
		while(connectionsLog.size() >= 15) connectionsLog.poll();
		return connectionsLog.offer(info);
	}
	
	public void addRequest() {
		allRequests.incrementAndGet();
	}

	public void addRedirectResponse(String url) {
		if(redirectsByUrlCount.containsKey(url)) {
			int count = redirectsByUrlCount.get(url);
			redirectsByUrlCount.put("url", ++count);
			return;
		}
		redirectsByUrlCount.put("url", 1);
	}
	
	public void addIp(String ip) {
		if(allUniqueIps.containsKey(ip)) {
			IpInfo ipInfo = allUniqueIps.get(ip);
			ipInfo.update();
			return;
		}
		allUniqueIps.put(ip, new IpInfo(ip));
	}
	
	public String makeStatistics() {
		try {
			File file = new File("resources/status.html");
			FileReader fr = new FileReader(file);
			char[] chars = new char[(int)file.length()];
			fr.read(chars);
			String page = new String(chars);
			page = page.replace("totalRequestCount", allRequests.toString());
			page = page.replace("uniqueRequestCount", 5+"");
			page = page.replace("connectionsCurrentlyOpened", getChannelsCount()+"");
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		 //TODO
		
	}
}
