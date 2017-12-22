package deplink.com.smartwirelessrelay.homegenius.util;

/**
 * Created by luoxiaoha on 2017/2/6.
 */

public class DataExchange {

    public static boolean compareData(byte[] src, byte[] dest) {
        if (src.length != dest.length)
            return false;
        for (int i = 0; i < src.length; i++) {
            if (src[i] != dest[i])
                return false;
        }
        return true;
    }

    /**
     * 将int型转换为bytes
     *
     * @param src
     * @param len
     * @return
     */
    public static byte[] intToBytes(int src, int len) {
        int tmp = src;
        byte[] dest = new byte[len];
        for (int i = 0; i < len; i++) {
            dest[len - i - 1] = (byte) (tmp & 0xff);
            tmp = tmp >> 8;
        }
        return dest;
    }

    /**
     * 字节流变成int型
     *
     * @param src
     * @param len
     * @return
     */
    public static int bytesToInt(byte[] src, int pos, int len) {
        int dest = 0;
        if (src == null)
            return 0;
        if (src.length < len)
            return 0;
        for (int i = 0; i < len; i++) {
            dest |= (src[i + pos] & 0xff);
            if (i == (len - 1)) {
            } else {
                dest = dest << 8;
            }
        }
        return dest;
    }

    public static long bytesToLong(byte[] src, int pos, int len) {
        long dest = 0;
        if (src == null)
            return 0;
        if (src.length < len)
            return 0;
        for (int i = 0; i < len; i++) {
            dest |= (src[i + pos] & 0xff);
            if (i == (len - 1)) {
            } else {
                dest = dest << 8;
            }
        }
        return dest;
    }

    public static byte[] intToFourByte(int data) {
        byte[] x = new byte[4];
        x[3] = (byte) (data & 0xff);
        x[2] = (byte) ((data >> 8) & 0xff);
        x[1] = (byte) ((data >> 16) & 0xff);
        x[0] = (byte) ((data >> 24) & 0xff);

        return x;
    }

    public static byte strByteToByte(byte a) {
        byte x;
        if (a >= 48 && a <= 57) {
            x = (byte) (a - 48);
        } else if (a >= 65 && a <= 70) {
            x = (byte) (a - 55);
        } else if (a >= 97 && a <= 102) {
            x = (byte) (a - 87);
        } else {
            x = 0;
        }
        return x;
    }

    public static byte strTobyte(String str) {
        byte data = 0;
        byte[] tmp = str.getBytes();
        if (tmp.length >= 2) {
            data = (byte) ((strByteToByte(tmp[0]) << 4) | strByteToByte(tmp[1]));
        } else {
            //System.out.print(" strTobyte error:"+str+"\n");
        }
        //data = (byte) (((int)strByteToByte(tmp[0]))*16+strByteToByte(tmp[1]));
        return data;
    }

    public static byte[] dbStringToBytes(String str) {
        if (str == null)
            return null;
        byte[] data = null;
        String[] xstr = str.split(" ");
        data = new byte[xstr.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = strTobyte(xstr[i]);
        }
        return data;
    }
    public static byte[] dbString_ToBytes(String str) {
        if (str == null)
            return null;
        byte[] data = null;
        String[] xstr = str.split("-");
        data = new byte[xstr.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = strTobyte(xstr[i]);
        }
        return data;
    }

    /**
     * byte[]转成字符创 ,例如byte 34 35 67 变成343567
     * @param data
     * @return
     */
    public static String dbBytesToString(byte[] data) {
        if (data == null)
            return null;
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int intVal = data[i] & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
            if ((i != (data.length - 1)))
                hexString.append(" ");
        }
        return hexString.toString().toUpperCase();
    }

    public static String byteToString(byte a) {
        StringBuffer hexString = new StringBuffer();
        int intVal = a & 0xff;
        if (intVal < 0x10)
            hexString.append("0");
        hexString.append(Integer.toHexString(intVal));
        return hexString.toString();
    }

    public static String byteToHexString(byte a) {
        StringBuffer hexString = new StringBuffer();
        int intVal = a & 0xff;
        hexString.append(" 0x");
        if (intVal < 0x10)
            hexString.append("0");
        hexString.append(Integer.toHexString(intVal));
        return hexString.toString();
    }

    public static String byteArrayToIntString(byte[] array) {
        StringBuffer intString = new StringBuffer();
        if (array != null) {
            for (byte b : array) {
                int intVal = b & 0xff;
                intString.append(intVal + " ");
            }
            return intString.toString();
        }
        return null;
    }

    public static String byteArrayToHexString(byte[] array) {
        StringBuffer hexString = new StringBuffer();
        if (array != null) {
            for (byte b : array) {
                int intVal = b & 0xff;
                hexString.append(" 0x");
                if (intVal < 0x10)
                    hexString.append("0");
                hexString.append(Integer.toHexString(intVal));
            }
            return hexString.toString();
        }
        return null;
    }

    public static byte[] longToEightByte(long data) {
        byte[] x = new byte[8];
        x[7] = (byte) (data & 0xff);
        x[6] = (byte) ((data >> 8) & 0xff);
        x[5] = (byte) ((data >> 16) & 0xff);
        x[4] = (byte) ((data >> 24) & 0xff);
        x[3] = (byte) ((data >> 32) & 0xff);
        x[2] = (byte) ((data >> 40) & 0xff);
        x[1] = (byte) ((data >> 48) & 0xff);
        x[0] = (byte) ((data >> 56) & 0xff);
        return x;
    }

    public static long eightByteToLong(byte data0, byte data1, byte data2, byte data3, byte data4, byte data5, byte data6, byte data7) {
        long tmp = 0;
        tmp = ((((long) data0 & 0xff) << 56) | (((long) data1 & 0xff) << 48) | (((long) data2 & 0xff) << 40) | (((long) data3 & 0xff) << 32) | (((long) data4 & 0xff) << 24) | (((long) data5 & 0xff) << 16) | (((long) data6 & 0xff) << 8) | ((long) data7 & 0xff));
        return tmp;
    }

    public static int get_crc16(byte[] bufData, int buflen, byte[] pcrc) {
        int ret = 0;
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        if (buflen == 0) {
            return ret;
        }
        for (i = 0; i < buflen; i++) {
            CRC ^= ((int) bufData[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
            //System.out.println(Integer.toHexString(CRC));
        }
        System.out.println(Integer.toHexString(CRC));
        pcrc[0] = (byte) (CRC & 0x00ff);
        pcrc[1] = (byte) (CRC >> 8);
        return ret;
    }

    public static int fourByteToInt(byte data0, byte data1, byte data2, byte data3) {
        int tmp;
        tmp = (((data0 & 0xff) << 24) | ((data1 & 0xff) << 16) | ((data2 & 0xff) << 8) | (data3 & 0xff));
        return tmp;
    }


    public static byte[] intToTwoByte(int data) {
        byte[] x = new byte[2];
        x[1] = (byte) (data & 0xff);
        x[0] = (byte) ((data >> 8) & 0xff);
        return x;
    }

    public static int fourCharToInt(byte[] data) {
        int itmp = 0;
        itmp = data[0];
        itmp = itmp << 8;
        itmp |= data[1];
        itmp = itmp << 8;
        itmp |= data[2];
        itmp = itmp << 8;
        itmp |= data[3];
        return itmp;
    }

    public static int twoCharToInt(byte[] data) {

        int tmp = 0;
        tmp = data[0];
        tmp = tmp << 8;
        tmp = tmp + data[1];
        return tmp;

    }

    public static long eightCharToLLong(byte[] data) {

        long tmp = 0;

        for (int i = 0; i < 8; i++) {
            tmp = tmp + data[i];
            if (i == 7)
                break;
            tmp = tmp << 8;
        }

        return tmp;
    }

    public static String charToIp(byte[] data){
        String tmp="",ip = "";
        for (int i=0;i<4;i++){
            if((int)data[i]<0){
                tmp=(256- Math.abs((int)data[i]))+"";
            }else{
                tmp=data[i]+"";
            }

            if(i==3){
                ip=ip+tmp+"";
            }else{
                ip=ip+tmp+".";
            }

        }
        return ip;

    }

}