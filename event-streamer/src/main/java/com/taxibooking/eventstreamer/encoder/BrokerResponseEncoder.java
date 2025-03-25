package com.taxibooking.eventstreamer.encoder;

import com.taxibooking.eventstreamer.models.BrokerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;


public class BrokerResponseEncoder extends MessageToByteEncoder<BrokerResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BrokerResponse resp, ByteBuf out) throws Exception {
        // Status kodu (4 byte)
        out.writeInt(resp.statusCode());
        // Topic: önce uzunluk, sonra topic verisi
        byte[] topicBytes = resp.topic().getBytes(StandardCharsets.UTF_8);
        out.writeInt(topicBytes.length);
        out.writeBytes(topicBytes);
        // Offset (8 byte)
        out.writeLong(resp.offset());
        // Payload: önce uzunluk, sonra payload (payload null ise 0 yazılır)
        if (resp.payload() != null) {
            byte[] payloadBytes = resp.payload().getBytes(StandardCharsets.UTF_8);
            out.writeInt(payloadBytes.length);
            out.writeBytes(payloadBytes);
        } else {
            out.writeInt(0);
        }
    }
}
