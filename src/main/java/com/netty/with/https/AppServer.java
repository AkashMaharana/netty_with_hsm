package com.netty.with.https;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AppServer {

    private static final int HTTPS_PORT = 1234;

    public void run() throws Exception {
        HsmManager.login();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        SslHandlerProvider.initializeSSLContext();

        try {

            ServerBootstrap httpBootstrap = new ServerBootstrap();

            httpBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerHandler())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture httpChannel = httpBootstrap.bind(HTTPS_PORT).sync();

            System.out.println("Done");
            httpChannel.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

        HsmManager.logout();
    }

    public static void main(String[] args) throws Exception {
        new AppServer().run();
    }
}
