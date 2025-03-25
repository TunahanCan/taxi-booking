package com.taxibooking.cacheserver.servers;

import com.taxibooking.cacheserver.service.CacheService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CacheServerHandler extends SimpleChannelInboundHandler<String> {

    private final CacheService cacheService;

    @Value("${application.cache.server.ttl.time}")
    private long MAX_TTL_SECONDS ;

    public CacheServerHandler(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String response = processCommand(msg.strip());
        ctx.writeAndFlush(response + "\n");
        if (msg.strip().equalsIgnoreCase("quit")) {
            ctx.close();
        }
    }

    private String processCommand(String msg) {
        if (msg.isEmpty()) return "ERROR: Boş komut";
        String[] tokens = msg.split("\\s+", 2);
        String command = tokens[0].toLowerCase();

        return switch (command) {
            case "set", "add", "replace" -> {
                String[] parts = msg.split("\\s+", 4);
                if (parts.length < 4) yield "ERROR: Parametre eksik";
                String key = parts[1];
                long ttl;
                try {
                    ttl = Long.parseLong(parts[2]);
                } catch (NumberFormatException e) {
                    yield "ERROR: TTL sayı olmalı";
                }
                if (ttl > MAX_TTL_SECONDS) ttl = MAX_TTL_SECONDS;
                String value = parts[3];
                yield switch (command) {
                    case "set" -> cacheService.set(key, value, ttl);
                    case "add" -> cacheService.add(key, value, ttl);
                    case "replace" -> cacheService.replace(key, value, ttl);
                    default -> "ERROR: Bilinmeyen komut";
                };
            }
            case "append", "prepend" -> {
                String[] parts = msg.split("\\s+", 3);
                if (parts.length < 3) yield "ERROR: Parametre eksik";
                String key = parts[1];
                String value = parts[2];
                yield switch (command) {
                    case "append" -> cacheService.append(key, value);
                    case "prepend" -> cacheService.prepend(key, value);
                    default -> "ERROR: Bilinmeyen komut";
                };
            }
            case "get" -> {
                String[] parts = msg.split("\\s+");
                if (parts.length < 2) yield "ERROR: Anahtar eksik";
                yield cacheService.get(Arrays.copyOfRange(parts, 1, parts.length));
            }
            case "incr", "decr" -> {
                String[] parts = msg.split("\\s+");
                if (parts.length < 3) yield "ERROR: Parametre eksik";
                String key = parts[1];
                long delta;
                try {
                    delta = Long.parseLong(parts[2]);
                } catch (NumberFormatException e) {
                    yield "ERROR: Delta sayı olmalı";
                }
                yield switch (command) {
                    case "incr" -> cacheService.incr(key, delta);
                    case "decr" -> cacheService.decr(key, delta);
                    default -> "ERROR: Bilinmeyen komut";
                };
            }
            case "delete" -> {
                String[] parts = msg.split("\\s+");
                if (parts.length < 2) yield "ERROR: Anahtar eksik";
                yield cacheService.delete(parts[1]);
            }
            case "flush_all" -> cacheService.flushAll();
            case "stats" -> cacheService.stats();
            case "version" -> cacheService.version();
            case "quit" -> "OK, closing connection";
            default -> "ERROR: Bilinmeyen komut";
        };
    }

}
