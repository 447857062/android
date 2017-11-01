package com.example;

public class MyClass {
  public static void main(String[] args) {
        // TODO Auto-generated method stub
     /*   //1.建立TCP连接
        String ip="192.168.68.205";   //服务器端ip地址
        int port=8889;        //端口号
        Socket sck=new Socket(ip,port);
        //2.传输内容
        String content="这是一个java模拟客户端";
        byte[] bstream=content.getBytes("GBK");  //转化为字节流
        OutputStream os=sck.getOutputStream();   //输出流
        os.write(bstream);
        //3.关闭连接
        sck.close();*/
      byte[]temp=new byte[2];
      temp[0]=(byte) 0x00;
      temp[1]=(byte) 0x21;
      System.out.print(bytesToInt(temp,0,2));
    }
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
}

