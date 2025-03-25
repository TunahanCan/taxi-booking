package com.taxibooking.eventstreamer.decoder;

import com.taxibooking.eventstreamer.models.BrokerRequest;
import com.taxibooking.eventstreamer.models.enums.CommandType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class BrokerRequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // En az 8 byte: 4 (komut) + 4 (topic uzunluk)
        if (in.readableBytes() < 8) return;
        in.markReaderIndex();
        int commandCode = in.readInt();
        CommandType command;
        try {
            command = CommandType.fromCode(commandCode);
        } catch (IllegalArgumentException e) {
            ctx.close();
            return;
        }
        int topicLength = in.readInt();
        if (in.readableBytes() < topicLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] topicBytes = new byte[topicLength];
        in.readBytes(topicBytes);
        String topic = new String(topicBytes, StandardCharsets.UTF_8);

        if (command == CommandType.PRODUCE) {
            if (in.readableBytes() < 4) {
                in.resetReaderIndex();
                return;
            }
            int payloadLength = in.readInt();
            if (in.readableBytes() < payloadLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] payloadBytes = new byte[payloadLength];
            in.readBytes(payloadBytes);
            String payload = new String(payloadBytes, StandardCharsets.UTF_8);
            out.add(new BrokerRequest(command, topic, 0L, payload));
        } else if (command == CommandType.CONSUME) {
            if (in.readableBytes() < 8) {
                in.resetReaderIndex();
                return;
            }
            long offset = in.readLong();
            out.add(new BrokerRequest(command, topic, offset, null));
        }
    }

}

