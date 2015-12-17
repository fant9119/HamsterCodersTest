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
import java.net.InetSocketAddress;
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
		String path = splitter.path();

		switch(path) {
			case "/hello": 
				helloResponse(ctx);
				break;
			case "/redirect":
				Map<String, List<String>> parameters = splitter.parameters();
				if(parameters.containsKey("url")) {
					redirectResponse(ctx, parameters.get("url").get(0));
				} else {
					pageNotFoundResponse(ctx, msg);
				}
				break;
			case "/status":
				System.out.println("Status works");
				statusResponse(ctx);
				break;
			default:
				pageNotFoundResponse(ctx, msg);
				break;
		}
	}

	private void statusResponse(ChannelHandlerContext ctx) {
		System.out.println("Channels: " + ServerStatistics.getInstance().getChannelsCount());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, 
				Unpooled.copiedBuffer(ServerStatistics.getInstance().makeStatistics(), CharsetUtil.UTF_8));
		System.out.println(ServerStatistics.getInstance().makeStatistics());
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void pageNotFoundResponse(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
	/*	File file = new File("404.html");
	    char[] chars = new char[(int)file.length()];
	    FileReader fr = new FileReader(file);
		fr.read(chars);
				
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,  
				Unpooled.copiedBuffer(chars, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	*/
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
        if(!keepAlive) {
        	future.addListener(ChannelFutureListener.CLOSE);
        }
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
