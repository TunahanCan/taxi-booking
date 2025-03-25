package com.taxibooking.eventstreamer.decoder;

import com.taxibooking.eventstreamer.models.BrokerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class BrokerResponseDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 20) return; // Minimum gereksinim: 4 (status) + 4 (topic length) + 8 (offset) + 4 (payload length)
        in.markReaderIndex();
        int statusCode = in.readInt();
        int topicLength = in.readInt();
        if (in.readableBytes() < topicLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] topicBytes = new byte[topicLength];
        in.readBytes(topicBytes);
        String topic = new String(topicBytes, StandardCharsets.UTF_8);
        if (in.readableBytes() < 8) {
            in.resetReaderIndex();
            return;
        }
        long offset = in.readLong();
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int payloadLength = in.readInt();
        String payload = null;
        if (payloadLength > 0) {
            if (in.readableBytes() < payloadLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] payloadBytes = new byte[payloadLength];
            in.readBytes(payloadBytes);
            payload = new String(payloadBytes, StandardCharsets.UTF_8);
        }
        out.add(new BrokerResponse(statusCode, topic, offset, payload));
    }

}
