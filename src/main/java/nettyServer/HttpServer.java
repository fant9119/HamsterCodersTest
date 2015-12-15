package nettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {
	
	private final int port;
	private final static int DEFEAULT_PORT = 9999;
	
	public HttpServer(int port) {
		this.port = port;
	}
	
	public HttpServer() {
		this(DEFEAULT_PORT);
	}
	
	public void start() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(4);
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new HttpServerInitializer());
			
			ChannelFuture future = bootstrap.bind(port).sync();
			System.out.println("Server starts at port: " + port);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			System.out.println("Server off!!!");
		}
		
	}

	public static void main(String[] args) {
		try {
			if(args.length > 0) {
				try {
					int port = Integer.parseInt(args[0]);
					new HttpServer(port).start();
				} catch (NumberFormatException e){
					System.err.println("Ops... Invalid port number!");
					System.err.println("Please, launch the program with valid port number or don't use parameters."
							+ "\nIt will starts server on default port (9999).");
				}
			} else {
				new HttpServer().start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	private static int getPort(String[] args) {
		
		return 0;
	}*/
}
