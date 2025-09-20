package dev.yalan.live;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import dev.yalan.live.events.EventLiveConnectionStatus;
import dev.yalan.live.netty.LiveProto;
import dev.yalan.live.netty.codec.FrameDecoder;
import dev.yalan.live.netty.codec.FrameEncoder;
import dev.yalan.live.netty.codec.crypto.RSADecoder;
import dev.yalan.live.netty.codec.crypto.RSAEncoder;
import dev.yalan.live.netty.handler.LiveHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import jnic.JNICInclude;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventManager;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@ControlFlowObfuscation
@JNICInclude
public class LiveClient {
    public static LiveClient INSTANCE;
    public static final Gson GSON = new GsonBuilder().create();
    public static final JsonParser JSON_PARSER = new JsonParser();

    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("Live-Worker"));
    public final LiveReconnectThread reconnectThread = new LiveReconnectThread();
    public final LiveComponent liveComponent = new LiveComponent(this);
    public final AtomicReference<String> crashMessage = new AtomicReference<>();
    public final AtomicReference<String> autoUsername = new AtomicReference<>();
    public final AtomicReference<String> autoPassword = new AtomicReference<>();
    public final AtomicInteger loginIndex1 = new AtomicInteger();
    public final AtomicInteger loginIndex2 = new AtomicInteger();
    public final AtomicBoolean isConnecting = new AtomicBoolean();
    private final Minecraft mc = Minecraft.getMinecraft();
    public final EventManager eventManager = new EventManager();
    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;
    private final String hardwareId;

    public LiveUser liveUser;
    private Channel channel;

    public LiveClient() {
        try {
            //checkVersion();
            rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDCsRWqoHw37Jn3H++NgP/FuHql9xGy8AzQR478DJ55KV1GP+aMep2CEQ2CFVT/n85fx2MteHyJXRmDoB/NRGR54oX1u3zlJhMQtqh+zRpvNv9C9QxYbBGRLQq7RVLM/8vSZuakouixxFAeogoxAa6/GTsd10u/G/jdijrev9GLguUpct/wNsf24CHQDMHP6yTYkgMkUMOqd/J3sk29/uinK8M0OqVwo1Kexzl1K22MaRJRlRYjVMcfmBssQ7SsAngyWGH8Ru+aG2YaqfRsEIMqZT9ngb+vY8tfq0uph84hXgtdrXzLUhgK9pbOsfkQpt9h1VzY/MVsUfmsYGHhwD+TAgMBAAECggEAENmSrnnFRnHLwX+aE/0eJdaZhtOjTIFKGpyW22mcLZBO6k2Nfor+JsujB6cg/B6wlcmD4+ORo7HTC3lIR1Or/oCEZ4gafxqUe9XTL2ZA628vGHRs5Ro1SwNC8oJFcEiTM1qEi6styHTdGkmb6DHlFdTxMNv5Skj5ePMFtVGUqSY8GDnEY1AqV/gWQ0L2t/vDt3pY+WJr1e/rctsmhJ+vVAIgko0w8r3znohtGYnE4Pbpi5gd6cak7fBwmdOrAw57IwepeydDdylsyQO8KFLuqObz5Uj0PJlZm1tGMbAPUg5OIp48ab8AjPmY2k6H3pftx38kYQ1/O4VZv7orVlQjAQKBgQDG5MgSI3YmmbQBHgb/5dpIwxOk4WprqfHTvTxPyR9EvOfWcGS8qyWsijHIDYhOatHLCCy2V90MFfAFcJJSkzaNuvQc7uch3bBIZ1c1IYImUt2UmUAmrreDGtj/BjvmISQ9liq2Sr2/5mQytaVyKJLEKUxD19MV+rd5rsDWD8vecQKBgQD6l3L9l9SYVMINaE73OE5vnbSCMzhuu8AwqBwF6QoRg6e8K5Bs7UIlVh3Do/y9KEe6t3Wqc4WCgO3WcXsBBqzx+w59+XtOtbLKiziEQNkmghBMA3nbMHGCMGGTLxwzAiNoozTT3NwYDbo0+3gHN4YB+MPHCEyySVFTWYUMs0mIQwKBgQC/z3VPYNmaAlNBXJb9hMsNruwr76Q1LpDkVbRrcZfxL4kaGgxck2Viz2eQ+dQBZCzs8/ZC59tqw2FVram/bu+Gocy4doF6/JP8T4Uf3S/qC7Jbk6v1YjvieSraT0XG+yE7L64DXXM/NU5eRSXIQW7BNN6y8tpEcSUueaTJDDTdUQKBgESCNSjJYpDG00qREs2Zsi/noJrb3/i+EZv7ybV/8YW6RZg3HMP61VOZyIddBNK9+WY9k8imzoBQOTWgTq2IIq4BIgQkuaZaHGgtSaU2iWdytXvik2TuSIn8KiDwBziWxBFuVRn6zp0w25Byk2z9rABL/1Ihnb48NySfW7WOKnM7AoGAIOA2GjjEeIVqKUnnsV3guhSHaaTR0i83tVFlEcN7Bgmae+mEoUxu12Tuk0eo2YsfYomnEfibgeQMk2slsy91hhI8NoAV7hNRkUZ9e2iIQHWs63p41NxHJjp11cAk5OlPHrg3+pRN0cWN5X3w7QIs0V3AGH6O8UNclugTvacXJ3w=")));
            rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvwMkhOvJEV1WWh7JcLSWRVcRsF+9iagM8f5tvBgQHSM5aZXAGG5rfxxo1NCw94wKZiwD/RS1SXr93q/SgxMdhwq46qPvHD8S3pQVKi02FNiWZR0+Wt7jhQkhtnBecZ+RglkZ2G0UTmo/QiYAwwzr3pLhayxeiBTF0xDQYpMnZl1zRmWhD4k0c/QblUGGRmeVzSysbXl+c5MmdOFyFPxTUhpov0x3QXw9QWHAr+9+/IwqLM2HQUAkUfBxEWfFMwHsV929fMK58pgJFzfVmUjGJeSXkWap34xcQQbdEvc4ptCKPfE8ty6AeL7Pqrt1E0ZRz9uOdWeRsje0jlcv0N+8JwIDAQAB")));
            hardwareId = generateHardwareId();
        } catch (Exception e) {
            throw new RuntimeException("Can't init LiveClient", e);
        }
        eventManager.register(liveComponent);
    }

    private void checkVersion() throws Exception {
        final URL url = URI.create("https://naven.today/naven/version").toURL();
        final HttpURLConnection con = (HttpURLConnection) url.openConnection(mc.getProxy());
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        try {
            if (con.getResponseCode() != 200) {
                throw new Exception("Version check HttpCode != 200");
            }

            final String version = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);

            if (!"1.0.1".equals(version)) {
                final JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(null, "Your Client is old,Newer client version released: " + version + ",Please Download the newest Version!");
                mc.shutdown();
                System.exit(0);
            }
        } finally {
            con.disconnect();
        }
    }

    public void connect() {
        if (isOpen() || isConnecting.get()) {
            return;
        }

        isConnecting.set(true);

        final Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast("frame_decoder", new FrameDecoder())
                                .addLast("rsa_decoder", new RSADecoder(rsaPrivateKey))
                                .addLast("frame_encoder", new FrameEncoder())
                                .addLast("rsa_encoder", new RSAEncoder(rsaPublicKey))
                                .addLast("live_handler", new LiveHandler(LiveClient.this));
                    }
                });

        bootstrap.connect("h9j345ce.cnmnmsl.top", 8964).addListener((ChannelFutureListener) future -> {
            isConnecting.set(false);

            if (future.isSuccess()) {
                LiveProto.sendPacket(future.channel(), LiveProto.createHandshake());
            }

            mc.addScheduledTask(() -> {
                if (future.isSuccess()) {
                    channel = future.channel();
                }

                eventManager.call(new EventLiveConnectionStatus(future.isSuccess(), future.cause()));
            });
        });
    }

    public void sendPacket(LiveProto.LivePacket packet) {
        if (isActive()) {
            LiveProto.sendPacket(channel, packet);
        }
    }

    public void close() {
        stopReconnectThread();

        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        workerGroup.shutdownGracefully();
    }

    public void startReconnectThread() {
        reconnectThread.start();
    }

    public void stopReconnectThread() {
        if (!reconnectThread.isInterrupted()) {
            reconnectThread.interrupt();
        }
    }

    public boolean isOpen() {
        return channel != null && channel.isOpen();
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public static String generateHardwareId() throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        final SystemInfo systemInfo = new SystemInfo();
        final String input = "NavenHardwareId" +
                systemInfo.getHardware().getComputerSystem().getHardwareUUID() +
                systemInfo.getHardware().getComputerSystem().getSerialNumber() +
                systemInfo.getHardware().getComputerSystem().getBaseboard().getSerialNumber();

        return toHex(messageDigest.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    private static String toHex(byte[] bArray) {
        final StringBuilder builder = new StringBuilder(bArray.length * 2);

        for (byte b : bArray) {
            final String hexString = Integer.toHexString(b & 0xFF).toUpperCase(Locale.ENGLISH);

            if (hexString.length() == 1) {
                builder.append('0').append(hexString);
            } else {
                builder.append(hexString);
            }
        }

        return builder.toString();
    }
}
