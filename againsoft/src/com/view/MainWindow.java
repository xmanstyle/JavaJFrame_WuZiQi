package com.view;

import com.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Xman on 2017/6/14 0014.
 */
public class MainWindow extends JFrame implements ActionListener {
    JLabel titleLabel;
    JPanel mainPan;
    String panelTitle;
    JButton backBt;
    CardLayout mainCardLayout;

    ///欢迎界面
    JPanel welcomePan;
    JButton btSearch;
    JButton btFightHost;

    ///在线主机
    JPanel onlinePan;
    ArrayList<User> onlineUserList = new ArrayList<>();
    ArrayList<SButton> onlineHostList = new ArrayList<>();

    //等待对方同意
    JPanel waitPan;
    JLabel waitLabel;

    //收到邀请的Dialog
    JDialog invitedDialog;
    JButton btAgree;
    JButton btDeny;

    ///游戏界面
    JPanel gamePan;

    //游戏结束
    JPanel gameOverPan;
    JLabel winnerLabel;
    JLabel loserLabel;

    Container container;

    LAN lan;
    private User user;

    public MainWindow() {
        container = this.getContentPane();
        lan = new LAN() {
            @Override
            public void deny() {
                if (onlinePan != null && mainPan != null) {
                    mainCardLayout.show(mainPan, "online");
                }
                JOptionPane.showConfirmDialog(MainWindow.this, "reject!", null, JOptionPane.CLOSED_OPTION);
            }

            @Override
            public void agree() {
                beginGame(false);
            }

            //搜索到一台新的在线主机
            @Override
            public void NewPlay(User user, int i) {
                onlineUserList.add(user);
                addOnlineHost(user, i);
            }

            //被user1邀请
            @Override
            public void invited(User user1) {
                user = user1;
                showInvitedDialog();
            }

            @Override
            public void read(int x, int y) {

                //收到游戏信息
                changeGameView(x,y);

            }
        };

        backBt = new JButton("返回");
        backBt.addActionListener(this);
        backBt.setBounds(340, 500, 80, 50);

        panelTitle = "欢迎进入游戏！";
        titleLabel = new JLabel(panelTitle);
        titleLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
        titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        titleLabel.setForeground(Color.red);
        container.add("North", titleLabel);

        mainCardLayout = new CardLayout();
        mainPan = new JPanel(mainCardLayout);
        container.add(mainPan);

        btSearch = new JButton("搜索主机");
        btSearch.addActionListener(this);
        btFightHost = new JButton("对抗电脑");
        btFightHost.addActionListener(this);
        btSearch.setBounds(300, 300, 200, 50);
        btFightHost.setBounds(300, 400, 200, 50);
        welcomePan = new JPanel(null);
        welcomePan.add(btSearch);
        welcomePan.add(btFightHost);
        mainPan.add(welcomePan, "welcome");

        this.setSize(800, 700);
        this.setTitle("五子棋游戏");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ///搜索在线主机
        if (e.getSource() == btSearch) {
            showOnlineHost();
            lan.Search();
        }

        if(e.getSource() == backBt){
            mainCardLayout.show(mainPan,"welcome");
        }

        //对抗电脑
        if (e.getSource() == btFightHost) {
            fightHost();
        }

        //收到游戏邀请-》同意
        if (e.getSource() == btAgree) {
            //lan 发送同意消息
            lan.sendBC("ha", user.getIp());
            lan.setEnemy(user);
            invitedDialog.dispose();
            beginGame(true);
        }

        ///收到游戏邀请=》拒绝
        if (e.getSource() == btDeny) {
            //lan发送拒绝消息
            lan.sendBC("hd", user.getIp());
            invitedDialog.dispose();

        }

        if (e.getSource() instanceof SButton) {
            SButton sButton = (SButton) e.getSource();
            int hostIndex = -1;
            hostIndex = sButton.getId();
            System.out.println("conn "+ onlineUserList.get(hostIndex).getIp());
            if (hostIndex != -1) {
                String ip = onlineUserList.get(hostIndex).getIp();
                waitLabel = new JLabel("Wait....");
                waitLabel.setHorizontalAlignment(JLabel.CENTER);
                waitLabel.setBounds(300, 300, 200, 50);
                waitPan = new JPanel(null);
                waitPan.add(waitLabel);
                mainPan.add(waitPan, "wait");
                mainCardLayout.show(mainPan, "wait");
                lan.sendBC("$" + lan.getUser().getName() + "," + lan.getUser().getIp(), ip);   //发送联机邀请
                lan.setEnemy(onlineUserList.get(hostIndex));
            }

        }
    }

    public void showInvitedDialog() {
        invitedDialog = new JDialog(this, "有新的游戏请求");
        btAgree = new JButton("Agree");
        btAgree.addActionListener(this);
        btDeny = new JButton("Deny");
        btDeny.addActionListener(this);
        invitedDialog.setLayout(null);
        invitedDialog.setLocationRelativeTo(null);
        invitedDialog.setSize(300, 200);
        btAgree.setBounds(80, 100, 70, 30);
        btDeny.setBounds(160, 100, 70, 30);
        invitedDialog.add(btAgree);
        invitedDialog.add(btDeny);
        invitedDialog.show();
    }

    public void showOnlineHost() {
        if (onlinePan == null) {
            onlinePan = new JPanel();
        }
        onlinePan.validate();
        mainPan.add(onlinePan, "online");
        mainCardLayout.show(mainPan, "online");
        mainPan.validate();

    }

    public void addOnlineHost(User user, int i) {
        if (onlinePan == null) {
            onlinePan = new JPanel();
        }
        SButton sButton = new SButton(user.getName(), i);
        sButton.addActionListener(this);
        onlineHostList.add(sButton);
        onlinePan.add(sButton);
        onlinePan.validate();
    }

    public void fightHost() {

    }

    public void beginGame(boolean beInvited) {
        //发起请求的一方为白棋
        //被要求的一方为黑棋
        if (beInvited) {///被邀请
            gamePan = new GamePanel(Color.BLACK,true) {
                @Override
                public void gameOver(Color whoWin) {
                    //显示游戏结束界面
                    gameOverPan = new JPanel(null);
                    if (whoWin == Color.WHITE) {
                        winnerLabel = new JLabel("白棋玩家胜利！");
                        winnerLabel.setBounds(200, 300, 400, 50);
                        loserLabel = new JLabel("黑棋玩家失败");
                        loserLabel.setBounds(200, 380, 400, 50);
                    } else {
                        winnerLabel = new JLabel("黑棋玩家胜利！");
                        winnerLabel.setBounds(200, 300, 400, 50);
                        loserLabel = new JLabel("白棋玩家失败");
                        loserLabel.setBounds(200, 380, 400, 50);
                    }
                    gameOverPan.add(winnerLabel);
                    gameOverPan.add(loserLabel);
                    gameOverPan.add(backBt);
                    mainPan.add(gameOverPan, "gameover");
                    mainCardLayout.show(mainPan, "gameover");
                }

                @Override
                public void sendStepInfo(int x,int y) {
                    lan.xiaQi(x,y);
                }
            };
            mainPan.add(gamePan, "game");
            mainCardLayout.show(mainPan, "game");
        } else {///主动的一方
            gamePan = new GamePanel(Color.WHITE,false) {

                @Override
                public void gameOver(Color whoWin) {

                    //显示游戏结束界面
                    gameOverPan = new JPanel(null);
                    if (whoWin == Color.WHITE) {
                        winnerLabel = new JLabel("白棋玩家胜利！");
                        winnerLabel.setBounds(360, 250, 400, 50);
                        loserLabel = new JLabel("黑棋玩家失败");
                        loserLabel.setBounds(360, 300, 400, 50);
                    } else {
                        winnerLabel = new JLabel("黑棋玩家胜利！");
                        winnerLabel.setBounds(360, 250, 400, 50);
                        loserLabel = new JLabel("白棋玩家失败");
                        loserLabel.setBounds(360, 300, 400, 50);
                    }
                    gameOverPan.add(winnerLabel);
                    gameOverPan.add(loserLabel);
                    gameOverPan.add(backBt);
                    mainPan.add(gameOverPan, "gameover");
                    mainCardLayout.show(mainPan, "gameover");
                }

                @Override
                public void sendStepInfo(int x,int y) {
                    lan.xiaQi(x,y);
                }
            };
        }
        mainPan.add(gamePan, "game");
        mainCardLayout.show(mainPan, "game");
    }

    public void changeGameView(int x,int y) {
        //更新gamePan 的界面显示
        System.out.println("from enemy: x->"+x+" y->"+y);
        gamePan.setName(x+","+y+",");
    }
}
