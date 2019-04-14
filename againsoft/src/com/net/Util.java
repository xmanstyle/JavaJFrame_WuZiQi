package com.net; /**
 * Created by Administrator on 2017/6/10.
 */

import java.net.*;
import java.util.Enumeration;
import java.util.Random;

public class Util {

    public static String[] adj = new String[]{
            "厚道的", "理智的", "忠厚的", "勤劳勇敢的", "勤奋的", "幽默的", "文雅的",
            "礼貌的", "斯文的", "开朗的", "直率的", "耿直的", "温柔的", "贤惠的", "谦虚的",
            "严以律已宽已待人的", "好学上进的", "勇挑重担的", "舍已为人的",
            "爱憎分明的", "平易近人的", "高风亮节的", "虚怀若谷的",
            "出淤泥而不染的", "坐怀不乱的"//adj size 25
    };

    public static String[] n = new String[]{
            "莎士比亚", "赵本山", "郁达夫", "三毛", "张资平", "张居正", "武则天", "钟无艳", "卫子夫",
            "达尔文", "奥巴马", "希特勒", "柏拉图", "卷心投手", "食堂阿姨", "门卫大叔", "辅导员", "校园变态痴汉",
            "德玛西亚之力盖伦", "刀锋意志艾瑞莉娅", "UC浏览器", "QQ拼音输入法", "萌妹子", "电路板", "铁板烧炒饭",
            "怡宝纯净水", "华为手机", "狼人", "预言家", "女巫" //n size 30
    };

    public static User getUser() {
        User user;
        String ip = getIpAddress();
        String name;
        int i = new Random().nextInt(25);
        int j = new Random().nextInt(30);
        name = new String(adj[i] + n[j]);
        user = new User(name, ip);
        return user;
    }


    //发送广播
    public static void SendBroadCast(String str, String ipStr) {
        try {
            InetAddress ip;
            if (ipStr == null) {
                ip = InetAddress.getByName("255.255.255.255");
            } else {
                ip = InetAddress.getByName(ipStr);
            }
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.getBytes().length, ip, 8888);
            ds.send(dp);
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }// 创建用来发送数据报包的套接字
    }

    //接收广播
    public static String ReceiveBroadCast() {
        String rec = null;
        try {
            DatagramSocket ds = new DatagramSocket(8888);// 创建接收数据报套接字并将其绑定到本地主机上的指定端口
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            ds.receive(dp);
            rec = new String(buf);
//            System.out.println(rec);
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rec;
    }


    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}