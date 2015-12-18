package nettyServer;

import java.net.InetSocketAddress;
import java.util.Date;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class StatisticsHandler extends ChannelTrafficShapingHandler {
	
	private ConnectionInfo info;
	private long receivedBytes;
	private long sentBytes;
	private int speed;
	private String uri;
	private String ip;

	public StatisticsHandler(long interval) {
		super(interval);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {	
		super.channelRead(ctx, msg);
		setSpeed();	
		if(msg instanceof HttpRequest) {
			ServerStatistics.getInstance().addRequest();
			ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
			ServerStatistics.getInstance().addIp(ip);
			
			HttpRequest req = (HttpRequest)msg;
			uri = req.getUri();

			if(speed == 0) {
				setSpeed();
			}
			info = new ConnectionInfo(ip, uri, new Date(), sentBytes, receivedBytes, speed);
			ServerStatistics.getInstance().addConectionToLog(info);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	private void setSpeed() {
		TrafficCounter traffic = this.trafficCounter();
		sentBytes = traffic.currentReadBytes();
		receivedBytes = traffic.cumulativeWrittenBytes();
		speed = (int) ((sentBytes + receivedBytes) * 1000 / (System.currentTimeMillis() - traffic.lastTime()));
		traffic.resetCumulativeTime();
	}
}