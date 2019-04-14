package com.net;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/6/10.
 */
public abstract class LAN {

    private ArrayList<User> online = null;
    //    private ServerSocket ss = null;
//    private Socket socket = null;
    private User user = null;
    private RecBCThread recBCThread = null;
    //    private DataInputStream dis = null;
//    private DataOutputStream dos = null;
//    private boolean read_flag;
    private User enemy;

    public LAN() {
        user = Util.getUser();
        System.out.println("LAN():"+user.getName());
        recBC();
    }

    public User getUser() {
        return user;
    }

    public ArrayList<User> getOnline() {
        return online;
    }

    public void Search() {
        if (recBCThread.rec_flag == false || recBCThread == null) {
            recBC();
        }
        online = new ArrayList<>();     //初始化在线人数列表
        //获取在线人:
        sendBC("#" + user.getIp(), null);    //询问是否在线
    }


//    /// 两台主机连接
//    public void connection(int i) {
//        closeBC();
//        sendBC("$" + user.getName() + "," + user.getIp(), online.get(i).getIp());   //发送联机邀请
//
//        try {
//            ss = new ServerSocket(8888);    //
//            socket = ss.accept();
//
//            dis = new DataInputStream(socket.getInputStream());
//            dos = new DataOutputStream(socket.getOutputStream());
//            dos.writeUTF("已连接");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TCPReader reader = new TCPReader();
//        reader.start();
//
//    }

//    // 接收到游戏请求
//    public void accept(User user) {     //接受游戏邀请
//        try {
//            socket = new Socket(user.getIp(), 8888);
//            dis = new DataInputStream(socket.getInputStream());
//            dos = new DataOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        TCPReader reader = new TCPReader();
//        reader.start();
//    }

//    class TCPReader extends Thread {
//        @Override
//        public void run() {
//            read_flag = true;
//            while (read_flag) {
//                String s = null;
//                try {
//                    s = dis.readUTF();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(s);
//                read(s);
//            }
//        }
//    }

//    public void wrote(String s) {
//        try {
//            dos.writeUTF(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public void beDeny() {
//        try {
//            read_flag = false;
//            dis.close();
//            dos.close();
//            socket.close();
//            if (ss != null)
//            ss.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        dis = null;
//        dos = null;
//        ss = null;
//        socket = null;
//
//        recBC();
//    }

    public void sendBC(String s, String ip) {
        Util.SendBroadCast(s, ip);
    }

    public void setEnemy(User e) {
        enemy = e;
    }

    public void xiaQi(int x, int y) {
        String s = "(" + x + "," + y;
        Util.SendBroadCast(s, enemy.getIp());
    }

    public void recBC() {   //开启接收广播
        recBCThread = new RecBCThread();
        recBCThread.start();
    }

    public void closeBC() {     //停止接收广播
        recBCThread.rec_flag = false;
    }

    private class RecBCThread extends Thread {  //接收广播线程

        private boolean rec_flag = true;

        @Override
        public void run() {
            while (rec_flag) {
                String recBC;
                if ((recBC = Util.ReceiveBroadCast()) != null) {
//                    System.out.println("recBC:"+recBC);
                    //  #询问    @ 回复在线   $邀请联机
                    new MyThread(recBC).start();

                }
            }
        }
    }

    private class MyThread extends Thread {     //广播消息处理线程

        private String recBC;

        public MyThread(String recBC) {
            recBC = recBC.substring(0, recBC.indexOf(0));
            this.recBC = recBC;
        }

        @Override
        public void run() {
//            System.out.println(recBC);
            String[] u;
//            switch (recBC.charAt(0)) {
//                case '#':   //询问是否在线
//                    if (!recBC.substring(1).equals(user.getIp())) {
//                        sendBC("@" + user.getName() + "," + user.getIp(), recBC.substring(1));
//                    }
//                    break;
//                case '@':   //接收到在线消息
//                    System.out.println(recBC);
//                    recBC = recBC.substring(1);
//                    u = recBC.split(",");
//                    User user1 = new User(u[0], u[1]);
//                    //System.out.println(u[0] + "is online");
//                    online.add(user1);
//                    NewPlay(user1, online.size() - 1);
//                    break;
//                case '$':   //接收到联机邀请
//                    recBC = recBC.substring(1, recBC.indexOf('\u0000'));
//                    u = recBC.split(",");
//                    invited(new User(u[0], u[1]));
//                    break;
//                case 'h':   //接收到邀请的回复
//                    if (recBC.charAt(1) == 'a') {
//                        agree();
//                    } else if (recBC.charAt(1) == 'd') {
//                        deny();
//                    }
//                    break;
//                case '(':   //下棋位置
//                    recBC.substring(1, recBC.indexOf('\u0000'));
//                    u = recBC.split(",");
//                    read(Integer.valueOf(u[0]), Integer.valueOf(u[1]));
//                    break;
//
//            }

            if (recBC.charAt(0) == '#') {  //接收到询问广播，回复在线
                System.out.println(recBC);
                if (recBC.substring(1).equals(user.getIp())) ;
                else
                    sendBC("@" + user.getName() + "," + user.getIp(), recBC.substring(1));
            }
            else if (recBC.charAt(0) == '@') {   //接收到在线消息，将在线主机添加到online表
                recBC = recBC.substring(1);
                u = recBC.split(",");
                System.out.println(u[0]);
                User user1 = new User(u[0], u[1]);
                System.out.println(u[0] + "is online");
                online.add(user1);
                NewPlay(user1, online.size() - 1);
            }
            else if (recBC.charAt(0) == '$') {
                recBC = recBC.substring(1);
                u = recBC.split(",");
                invited(new User(u[0], u[1]));
            }
            else if (recBC.charAt(0) == 'h') {
                if (recBC.charAt(1) == 'a') agree();
                else if (recBC.charAt(1) == 'd') deny();
            } else if (recBC.charAt(0) == '(') {
                recBC = recBC.substring(1);
                u = recBC.split(",");
                read(Integer.parseInt(u[0]), Integer.parseInt(u[1]));
            }
        }

    }

    public abstract void deny();

    public abstract void agree();

    public abstract void NewPlay(User user, int i);     //搜索到新玩家

    public abstract void invited(User user);    //被邀请时联机

    public abstract void read(int x, int y);    //接收到数据

}
