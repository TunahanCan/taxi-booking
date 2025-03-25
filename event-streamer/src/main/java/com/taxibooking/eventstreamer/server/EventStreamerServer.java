package com.taxibooking.eventstreamer.server;


import com.taxibooking.eventstreamer.decoder.BrokerRequestDecoder;
import com.taxibooking.eventstreamer.encoder.BrokerRequestEncoder;
import com.taxibooking.eventstreamer.encoder.BrokerResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EventStreamerServer {

    private final int port = 39092; // Broker portu

    @PostConstruct
    public void startBroker() {
        // Netty sunucusunu ayrı bir thread üzerinde başlatıyoruz.
        new Thread(this::runServer).start();
    }

    private void runServer() {
        var bossGroup = new NioEventLoopGroup(1);
        var workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // Decoder/Encoder ve iş mantığı handler'ı ekliyoruz.
                            pipeline.addLast(new BrokerRequestDecoder());
                            pipeline.addLast(new BrokerRequestEncoder());
                            pipeline.addLast(new BrokerResponseEncoder());
                            pipeline.addLast(new BrokerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Broker started on port " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
