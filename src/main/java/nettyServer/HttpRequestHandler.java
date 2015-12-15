package nettyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler.Sharable;


@Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		String uri = msg.getUri();
		QueryStringDecoder splitter = new QueryStringDecoder(uri);
		String path = splitter.path();

		switch(path) {
			case "/hello": 
				//System.out.println("Works");
				helloResponse(ctx);
				break;
			case "/redirect":
				System.out.println("Works url");
				break;
			case "/status":
				System.out.println("Status works");
				break;
			default:
				System.out.println("default");
				break;
		}
	}

	private void helloResponse(ChannelHandlerContext ctx) {
		ctx.executor().schedule(new Runnable, 10, TimeUnit.SECONDS);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().add("hello","Hello Word!");
		try {
			ctx.writeAndFlush(response).sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
