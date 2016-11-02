package com.ab.ircserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class Server {

    final int port;

    public Server(int port) {
    	super();
    	this.port = port;
    }
    
    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new Server(port).run();
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("string delimiter", new DelimiterBasedFrameDecoder(255, Delimiters.lineDelimiter()));
                            pipeline.addLast("string decoder", new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast("command decoder", new CommandDecoder());
                            pipeline.addLast("string encoder", new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast("command handler", new ChatServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true);
            
            // Bind and start to accept incoming connections
            ChannelFuture f = b.bind(port).sync();
            
            // wait until the server socket is closed
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
