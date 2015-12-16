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
				helloResponse(ctx);
				break;
			case "/redirect":
				String newUri = "http://" + splitter.parameters().get("url").get(0);
				sendRedirectResponse(ctx, newUri);
				break;
			case "/status":
				System.out.println("Status works");
				statusResponse(ctx);
				break;
			default:
				pageNotFoundResponse(ctx);
				break;
		}
	}

	private void statusResponse(ChannelHandlerContext ctx) {
		System.out.println("Channels: " + ServerStatistics.getInstance().getChannelsCount());
		
	}

	private void pageNotFoundResponse(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,  
				Unpooled.copiedBuffer("404 Sorry, but this page not found.", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		System.out.println(response);
	}

	private void helloResponse(ChannelHandlerContext ctx) {
		ctx.executor().schedule(new Runnable() {
			
			@Override
			public void run() {
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, 
						Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
		        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html");
				ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			}	
			
		}, 10, TimeUnit.SECONDS);
	}
	
	private void sendRedirectResponse(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    } 


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
