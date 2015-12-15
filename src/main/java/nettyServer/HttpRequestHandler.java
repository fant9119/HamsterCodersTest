package nettyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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
				sendRedirectResponse(ctx, splitter.parameters().get("url").get(0));
				System.out.println(splitter.parameters().get("url").get(0));
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
		//ctx.executor().schedule(new Runnable, 10, TimeUnit.SECONDS);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, 
				Unpooled.copiedBuffer("Hello World>", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
		System.out.println(response);
		try {
			ctx.writeAndFlush(response).sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void sendRedirectResponse(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    } 


	

}
