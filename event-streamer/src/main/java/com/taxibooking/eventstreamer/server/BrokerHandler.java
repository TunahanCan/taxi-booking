package com.taxibooking.eventstreamer.server;

import com.taxibooking.eventstreamer.models.BrokerRequest;
import com.taxibooking.eventstreamer.models.BrokerResponse;
import com.taxibooking.eventstreamer.models.enums.CommandType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class BrokerHandler extends SimpleChannelInboundHandler<BrokerRequest> {

    // Her konu için mesaj listesini (append-only log) saklıyoruz
    private final Map<String, List<String>> topicLogs = new ConcurrentHashMap<>();
    // Her konu için offset sayacı
    private final Map<String, AtomicLong> topicOffsets = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BrokerRequest req) {
        if (req.command() == CommandType.PRODUCE) {
            topicLogs.computeIfAbsent(req.topic(), k -> new CopyOnWriteArrayList<>());
            topicOffsets.computeIfAbsent(req.topic(), k -> new AtomicLong(0));
            long offset = topicOffsets.get(req.topic()).getAndIncrement();
            topicLogs.get(req.topic()).add(req.payload());
            BrokerResponse response = new BrokerResponse(0, req.topic(), offset, "Message stored");
            ctx.writeAndFlush(response);
        } else if (req.command() == CommandType.CONSUME) {
            List<String> messages = topicLogs.get(req.topic());
            if (messages != null && req.offset() < messages.size()) {
                String message = messages.get((int) req.offset());
                BrokerResponse response = new BrokerResponse(0, req.topic(), req.offset(), message);
                ctx.writeAndFlush(response);
            } else {
                BrokerResponse response = new BrokerResponse(1, req.topic(), req.offset(), null);
                ctx.writeAndFlush(response);
            }
        }
    }
}
