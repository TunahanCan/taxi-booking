package com.taxibooking.eventstreamer.encoder;

import com.taxibooking.eventstreamer.models.BrokerRequest;
import com.taxibooking.eventstreamer.models.enums.CommandType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;


public class BrokerRequestEncoder extends MessageToByteEncoder <BrokerRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BrokerRequest req, ByteBuf out) {
        // Komut kodu (4 byte)
        out.writeInt(req.command().getCode());
        // Topic: önce uzunluk (4 byte) sonra topic verisi
        byte[] topicBytes = req.topic().getBytes(StandardCharsets.UTF_8);
        out.writeInt(topicBytes.length);
        out.writeBytes(topicBytes);
        if (req.command() == CommandType.PRODUCE) {
            // PRODUCE için: önce payload uzunluğu, sonra payload verisi
            byte[] payloadBytes = req.payload().getBytes(StandardCharsets.UTF_8);
            out.writeInt(payloadBytes.length);
            out.writeBytes(payloadBytes);
        } else if (req.command() == CommandType.CONSUME) {
            // CONSUME için: offset (8 byte)
            out.writeLong(req.offset());
        }
    }
}