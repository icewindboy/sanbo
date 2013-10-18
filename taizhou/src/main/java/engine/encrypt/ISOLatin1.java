package engine.encrypt;

import engine.util.Hex;

public class ISOLatin1
{
    private ISOLatin1() {}

    public static byte[] toByteArray(String s, int offset, int length)
    {
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++)
            buf[i] = (byte) s.charAt(offset + i);

        return buf;
    }

    public static byte[] toByteArray(String s)
    {
        return toByteArray(s, 0, s.length());
    }

    public static byte[] toByteArrayLossless(String s, int offset, int length)
    {
        byte[] buf = new byte[length];
        char c;
        for (int i = 0; i < length; i++) {
            c = s.charAt(offset + i);
            if (c > '\u00FF')
                throw new IllegalArgumentException("non-ISO-Latin-1 character in input: \\"
                    + "u" + Hex.shortToString(c));
            buf[i] = (byte) c;
        }

        return buf;
    }

    public static byte[] toByteArrayLossless(String s)
    {
        return toByteArrayLossless(s, 0, s.length());
    }

    public static String toString(byte[] b, int offset, int length)
    {
        char[] cbuf = new char[length];
        for (int i = 0; i < length; i++)
            cbuf[i] = (char) (b[i] & 0xFF);

        return new String(cbuf);
    }

    public static String toString(byte[] b)
    {
        return toString(b, 0, b.length);
    }
}
