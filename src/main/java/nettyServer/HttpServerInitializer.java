package nettyServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		//pipeline.addFirst("traffic", new TrafficHandler());
		pipeline.addFirst("serverCodec", new HttpServerCodec());
		pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
		
		pipeline.addLast("requestHandler", new HttpRequestHandler());
		
		ServerStatistics.getInstance().addChannel(ch);
	}
	
}
