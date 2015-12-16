package nettyServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Map<Integer, FullHttpRequest> requests;
	
	
	private ServerStatistics() {
		channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		//channels = new DefaultChannelGroup(null);
		requests = Collections.synchronizedMap(new HashMap<>());
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

	public void addChannel(Channel channel) {
		channels.add(channel);		
	}
}
