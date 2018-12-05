//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package info.kgeorgiy.java.advanced.hello;

import org.junit.Assert;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class Util {
    public static final Charset CHARSET;
    private static final Random RANDOM;
    private static final List<String> ANSWER;
    public static boolean i18n;

    static {
        CHARSET = StandardCharsets.UTF_8;
        RANDOM = new Random(4357204587045842850L);
        ANSWER = Arrays.asList("%s Hello", "%s ආයුබෝවන්", "Բարեւ, %s", "مرحبا %s", "Салом %s", "Здраво %s", "Здравейте %s", "Прывітанне %s", "Привіт %s", "Привет, %s", "Поздрав %s", "سلام به %s", "שלום %s", "Γεια σας %s", "העלא %s", "ہیل%s٪ ے", "Bonjou %s", "Bonjour %s", "Bună ziua %s", "Ciao %s", "Dia duit %s", "Dobrý deň %s", "Dobrý den, %s", "Habari %s", "Halló %s", "Hallo %s", "Halo %s", "Hei %s", "Hej %s", "Hello  %s", "Hello %s", "Hello %s", "Helo %s", "Hola %s", "Kaixo %s", "Kamusta %s", "Merhaba %s", "Olá %s", "Ola %s", "Përshëndetje %s", "Pozdrav %s", "Pozdravljeni %s", "Salom %s", "Sawubona %s", "Sveiki %s", "Tere %s", "Witaj %s", "Xin chào %s", "ສະບາຍດີ %s", "สวัสดี %s", "ഹലോ %s", "ಹಲೋ %s", "హలో %s", "हॅलो %s", "नमस्कार%sको", "হ্যালো %s", "ਹੈਲੋ %s", "હેલો %s", "வணக்கம் %s", "ကို %s မင်္ဂလာပါ", "გამარჯობა %s", "ជំរាបសួរ %s បាន", "こんにちは%s", "你好%s", "안녕하세요  %s");
    }

    private Util() {
    }

    public static String getString(final DatagramPacket var0) {
        return new String(var0.getData(), var0.getOffset(), var0.getLength(), CHARSET);
    }

    public static void setString(final DatagramPacket var0, final String var1) {
        final byte[] var2 = var1.getBytes(CHARSET);
        var0.setData(var2);
        var0.setLength(var0.getData().length);
    }

    public static DatagramPacket createPacket(final DatagramSocket var0) throws SocketException {
        return new DatagramPacket(new byte[var0.getReceiveBufferSize()], var0.getReceiveBufferSize());
    }

    public static String request(final String var0, final DatagramSocket var1, final SocketAddress var2) throws IOException {
        send(var1, var0, var2);
        return receive(var1);
    }

    public static String receive(final DatagramSocket var0) throws IOException {
        final DatagramPacket var1 = createPacket(var0);
        var0.receive(var1);
        return getString(var1);
    }

    public static void send(final DatagramSocket var0, final String var1, final SocketAddress var2) throws IOException {
        final DatagramPacket var3 = new DatagramPacket(new byte[0], 0);
        setString(var3, var1);
        var3.setSocketAddress(var2);
        var0.send(var3);
    }

    public static String response(final String var0) {
        return String.format("Hello, %s", var0);
    }

    public static String i18nResponse(final String var0) {
        return i18n ? String.format(ANSWER.get(RANDOM.nextInt(ANSWER.size())), var0) : response(var0);
    }

    public static AtomicInteger[] server(final String var0, final int var1, final double var2, final DatagramSocket var4) {
        final AtomicInteger[] var5 = Stream.generate(AtomicInteger::new).limit((long) var1).toArray(var0x -> {
            return new AtomicInteger[var0x];
        });
        new Thread(() -> {
            Random var5x = new Random(4357204587045842850L);

            try {
                while (true) {
                    DatagramPacket var6 = createPacket(var4);
                    var4.receive(var6);
                    String var7 = getString(var6);
                    String var8 = "Invalid or unexpected request " + var7;
                    Assert.assertTrue(var8, var7.startsWith(var0));
                    String[] var9 = var7.substring(var0.length()).split("_");
                    Assert.assertTrue(var8, var9.length == 2);

                    try {
                        int var10 = Integer.parseInt(var9[0]);
                        int var11 = Integer.parseInt(var9[1]);
                        Assert.assertTrue(var8, 0 <= var10 && var10 < var5.length);
                        Assert.assertTrue(var8, var11 == var5[var10].get());
                        if (var2 >= var5x.nextDouble()) {
                            var5[var10].incrementAndGet();
                            setString(var6, i18nResponse(var7));
                            var4.send(var6);
                        } else if (var5x.nextBoolean()) {
                            setString(var6, corrupt(i18nResponse(var7), var5x));
                            var4.send(var6);
                        }
                    } catch (NumberFormatException var12) {
                        throw new AssertionError(var8);
                    }
                }
            } catch (IOException var13) {
                System.err.println(var13.getMessage());
            }
        }).start();
        return var5;
    }

    private static String corrupt(final String var0, final Random var1) {
        switch (var1.nextInt(4)) {
            case 0:
                return var0.replace("_", "0");
            case 1:
                return var0.replace("_", "++");
            case 2:
                return "";
            case 3:
                return var0.replace("HelloClientTest", "Hello");
            default:
                throw new AssertionError("Impossible");
        }
    }
}
