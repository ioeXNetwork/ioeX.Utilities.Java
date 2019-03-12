package org.ioexnetwork.ioex;


/**
 * 通信格式轉換
 *
 * Java和一些windows編程語言如c、c++、delphi所寫的網絡程序進行通訊時，需要進行相應的轉換
 * 高、低字節之間的轉換
 * windows的字節序為低字節開頭
 * linux,unix的字節序為高字節開頭
 * java則無論平台變化，都是高字節開頭
 */

public class FormatTransfer {
    /**
     * 將int轉為低字節在前，高字節在後的byte數組
     * @param n int
     * @return byte[]
     */
    public static byte[] toLH(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 將int轉為高字節在前，低字節在後的byte數組
     * @param n int
     * @return byte[]
     */
    public static byte[] toHH(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 將short轉為低字節在前，高字節在後的byte數組
     * @param n short
     * @return byte[]
     */
    public static byte[] toLH(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 將short轉為高字節在前，低字節在後的byte數組
     * @param n short
     * @return byte[]
     */
    public static byte[] toHH(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }



/**
 * 將int轉為高字節在前，低字節在後的byte數組

 public static byte[] toHH(int number) {
 int temp = number;
 byte[] b = new byte[4];
 for (int i = b.length - 1; i > -1; i--) {
 b = new Integer(temp & 0xff).byteValue();
 temp = temp >> 8;
 }
 return b;
 }

 public static byte[] IntToByteArray(int i) {
 byte[] abyte0 = new byte[4];
 abyte0[3] = (byte) (0xff & i);
 abyte0[2] = (byte) ((0xff00 & i) >> 8);
 abyte0[1] = (byte) ((0xff0000 & i) >> 16);
 abyte0[0] = (byte) ((0xff000000 & i) >> 24);
 return abyte0;
 }


 */

    /**
     * 將float轉為低字節在前，高字節在後的byte數組
     */
    public static byte[] toLH(float f) {
        return toLH(Float.floatToRawIntBits(f));
    }

    /**
     * 將float轉為高字節在前，低字節在後的byte數組
     */
    public static byte[] toHH(float f) {
        return toHH(Float.floatToRawIntBits(f));
    }

    /**
     * 將String轉為byte數組
     */
    public static byte[] stringToBytes(String s, int length) {
        while (s.getBytes().length < length) {
            s += " ";
        }
        return s.getBytes();
    }


    /**
     * 將字節數組轉換為String
     * @param b byte[]
     * @return String
     */
    public static String bytesToString(byte[] b) {
        StringBuffer result = new StringBuffer("");
        int length = b.length;
        for (int i=0; i<length; i++) {
            result.append((char)(b[i] & 0xff));
        }
        return result.toString();
    }

    /**
     * 將字符串轉換為byte數組
     * @param s String
     * @return byte[]
     */
    public static byte[] stringToBytes(String s) {
        return s.getBytes();
    }

    /**
     * 將高字節數組轉換為int
     * @param b byte[]
     * @return int
     */
    public static int hBytesToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[i] >= 0) {
                s = s + b[i];
            } else {
                s = s + 256 + b[i];
            }
            s = s * 256;
        }
        if (b[3] >= 0) {
            s = s + b[3];
        } else {
            s = s + 256 + b[3];
        }
        return s;
    }

    /**
     * 將低字節數組轉換為int
     * @param b byte[]
     * @return int
     */
    public static int lBytesToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[3-i] >= 0) {
                s = s + b[3-i];
            } else {
                s = s + 256 + b[3-i];
            }
            s = s * 256;
        }
        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        return s;
    }


    /**
     * 高字節數組到short的轉換
     * @param b byte[]
     * @return short
     */
    public static short hBytesToShort(byte[] b) {
        int s = 0;
        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        s = s * 256;
        if (b[1] >= 0) {
            s = s + b[1];
        } else {
            s = s + 256 + b[1];
        }
        short result = (short)s;
        return result;
    }

    /**
     * 低字節數組到short的轉換
     * @param b byte[]
     * @return short
     */
    public static short lBytesToShort(byte[] b) {
        int s = 0;
        if (b[1] >= 0) {
            s = s + b[1];
        } else {
            s = s + 256 + b[1];
        }
        s = s * 256;
        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        short result = (short)s;
        return result;
    }

    /**
     * 高字節數組轉換為float
     * @param b byte[]
     * @return float
     */
    public static float hBytesToFloat(byte[] b) {
        int i = 0;
        Float F = new Float(0.0);
        i = ((((b[0]&0xff)<<8 | (b[1]&0xff))<<8) | (b[2]&0xff))<<8 | (b[3]&0xff);
        return F.intBitsToFloat(i);
    }

    /**
     * 低字節數組轉換為float
     * @param b byte[]
     * @return float
     */
    public static float lBytesToFloat(byte[] b) {
        int i = 0;
        Float F = new Float(0.0);
        i = ((((b[3]&0xff)<<8 | (b[2]&0xff))<<8) | (b[1]&0xff))<<8 | (b[0]&0xff);
        return F.intBitsToFloat(i);
    }

    /**
     * 將byte數組中的元素倒序排列
     */
    public static byte[] bytesReverseOrder(byte[] b) {
        int length = b.length;
        byte[] result = new byte[length];
        for(int i=0; i<length; i++) {
            result[length-i-1] = b[i];
        }
        return result;
    }


    public static void logBytes(byte[] bb) {
        int length = bb.length;
        String out = "";
        for (int i=0; i<length; i++) {
            out = out + bb + " ";
        }

    }

    /**
     * 將int類型的值轉換為字節序顛倒過來對應的int值
     * @param i int
     * @return int
     */
    public static int reverseInt(int i) {
        int result = FormatTransfer.hBytesToInt(FormatTransfer.toLH(i));
        return result;
    }

    /**
     * 將short類型的值轉換為字節序顛倒過來對應的short值
     * @param s short
     * @return short
     */
    public static short reverseShort(short s) {
        short result = FormatTransfer.hBytesToShort(FormatTransfer.toLH(s));
        return result;
    }

    /**
     * 將float類型的值轉換為字節序顛倒過來對應的float值
     * @param f float
     * @return float
     */
    public static float reverseFloat(float f) {
        float result = FormatTransfer.hBytesToFloat(FormatTransfer.toLH(f));
        return result;
    }

}
