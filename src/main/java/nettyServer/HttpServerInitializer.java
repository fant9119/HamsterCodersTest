package nettyServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		System.out.println(ch.remoteAddress().getAddress() + " - connected to server.");
		
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("statistic", new StatisticsHandler(AbstractTrafficShapingHandler.DEFAULT_CHECK_INTERVAL));
		pipeline.addLast("serverCodec", new HttpServerCodec());
		pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));	
		pipeline.addLast("requestHandler", new HttpRequestHandler());
		
		ServerStatistics.getInstance().addChannel(ch);
		
		ChannelFuture future = ch.closeFuture();
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println(ch.remoteAddress().getAddress() + " - disconnected from server.");
			}
			
		});
	}
	
}
