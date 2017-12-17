package ch.sebi.acmf.utils;

/**
 * Created by Sebastian on 20.07.2017.
 */
public class ByteUtils {
    public static Byte B(byte b) {
        return new Byte(b);
    }

    public static Byte[] B(byte[] b) {
        Byte[] r = new Byte[b.length];
        for(int i = 0; i < b.length; i++)
            r[i] = b[i];

        return r;
    }

    public static byte[] b(Byte[] b) {
        byte[] r = new byte[b.length];
        for(int i = 0; i < b.length; i++)
            r[i] = b[i].byteValue();

        return r;
    }
}
