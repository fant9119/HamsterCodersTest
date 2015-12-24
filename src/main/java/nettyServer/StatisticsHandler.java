package nettyServer;

import java.net.InetSocketAddress;
import java.util.Date;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.AttributeKey;

public class StatisticsHandler extends ChannelTrafficShapingHandler {
	
	private ServerStatistics statistics = ServerStatistics.getInstance();
	private ConnectionInfo info = new ConnectionInfo();
	private AttributeKey<ConnectionInfo> uriStat = AttributeKey.valueOf("uri");
	
	public StatisticsHandler(long checkInterval) {
		super(checkInterval);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		statistics.addRequest();
		statistics.addChannel(ctx.channel());

		InetSocketAddress ip = (InetSocketAddress) ctx.channel().remoteAddress();
	    info.setSrcIp(ip.getHostName());
	    statistics.addIp(ip.getHostName());
	    ctx.channel().attr(uriStat).set(info);
	    

	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    	trafficCounter.stop();
    	info.setReceivedBytes(trafficCounter.cumulativeReadBytes());
        info.setSentBytes(trafficCounter.cumulativeWrittenBytes());
        info.setSpeed((int)trafficCounter.getRealWriteThroughput());
        info.setDate(new Date());
        statistics.addConectionToLog(info);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}