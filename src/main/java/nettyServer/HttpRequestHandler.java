package nettyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		String uri = msg.getUri();
		QueryStringDecoder splitter = new QueryStringDecoder(uri);
		String path = splitter.path().toLowerCase();

		switch(path) {
			case "/hello": 
				System.out.println("/hello");
				helloResponse(ctx);
				break;
			case "/redirect":
				System.out.println("/redirect");
				Map<String, List<String>> parameters = splitter.parameters();
				if(parameters.containsKey("url")) {
					redirectResponse(ctx, parameters.get("url").get(0));
				} else {
					pageNotFoundResponse(ctx, msg);
				}
				break;
			case "/status":
				System.out.println("/status");
				statusResponse(ctx);
				break;
			default:
				System.out.println(path);
				pageNotFoundResponse(ctx, msg);
				break;
		}
	}

	private void statusResponse(ChannelHandlerContext ctx) {
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, 
				Unpooled.copiedBuffer(ServerStatistics.getInstance().makeStatistics(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void pageNotFoundResponse(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
		RandomAccessFile file = new RandomAccessFile("resources/404.html", "r");
	
		HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.NOT_FOUND);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if(keepAlive) {
        	response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
        	response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
   
        future.addListener(ChannelFutureListener.CLOSE);
        
        file.close();
	}

	private void helloResponse(final ChannelHandlerContext ctx) {
		ctx.executor().schedule(new Runnable() {
			
			@Override
			public void run() {
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, 
						Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
		        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
				ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			}	
			
		}, 10, TimeUnit.SECONDS);
	}
	
	private void redirectResponse(ChannelHandlerContext ctx, String url) {
		Pattern pattern = Pattern.compile("^http(s)?://[^\\s]*");
		Matcher matcher = pattern.matcher(url);
		if(!matcher.matches()) {
			url = "http://" + url;
		}
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, url);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
		ServerStatistics.getInstance().addRedirectResponse(url);
    } 

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
