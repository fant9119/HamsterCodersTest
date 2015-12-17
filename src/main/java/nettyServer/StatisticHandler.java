package nettyServer;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class StatisticHandler extends ChannelTrafficShapingHandler {

	public StatisticHandler(long interval) {
		super(interval);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ServerStatistics.getInstance().addRequest();
		String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		ServerStatistics.getInstance().addIp(ip);
		super.channelActive(ctx);
	}

	@Override
	protected void doAccounting(TrafficCounter counter) {
		long receivedBytes = counter.cumulativeReadBytes();
		long sentBytes = counter.cumulativeReadBytes();
		int speed = (int)(counter.lastWrittenBytes() * 1000 / counter.checkInterval());
	}
	
	
}
