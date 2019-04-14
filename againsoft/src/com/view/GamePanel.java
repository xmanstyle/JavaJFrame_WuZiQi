package com.view;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 * 游戏界面
 * Created by Xman on 2017/6/10 0010.
 */
public abstract class GamePanel extends JPanel implements MouseListener{
    private final int row = 15;
    private final int col = 15;
    private final int span = 30;
    private Point[][] points = new Point[row+1][col+1];

    Vector<Qi> myQies = new Vector<>();
    Vector<Qi> enemyQies = new Vector<>();

    UsedPoint[][] pointStatus = new UsedPoint[row+1][col+1];

    final Color WHITE = Color.WHITE;
    final Color BLACK = Color.BLACK;
    Color myColor;
    Color enemyColor;
    Color nextColor;

    private boolean clickAble;
    private boolean isInvited;

    private Point[] dirs = new Point[4];
    private final Point up = new Point(-1,0);
    private final Point right_down = new Point(1,1);
    private final Point right_up = new Point(-1,1);
    private final Point right = new Point(0,1);
//    private final Point left_down = new Point(1,-1);
//    private final Point down = new Point(1,0);
//    private final Point left_up = new Point(-1,-1);
//    private final Point left = new Point(0,-1);


    public GamePanel(Color selfColor,boolean beInvited){
        this.addMouseListener(this);
        this.setBackground(new Color(203,155,82));
        for(int i=0;i<=row;i++){
            for(int j=0;j<=col;j++){
                pointStatus[i][j] = new UsedPoint(i,j,false,UsedPoint.NONE);
            }
        }
        dirs[0] = up;
        dirs[1] = right_up ;
        dirs[2] = right;
        dirs[3] = right_down;
//        dirs[4] = down;
//        dirs[5] = left_down;
//        dirs[6] = left;
//        dirs[7] = left_up;
        if(selfColor==WHITE){
            myColor = WHITE;
            enemyColor = BLACK;
        }else{
            myColor = BLACK;
            enemyColor = WHITE;
        }
        this.isInvited = beInvited;
        if(beInvited){//被邀请
            nextColor = enemyColor;
            clickAble = false;
        }else{///主动方
            nextColor = myColor;
            clickAble = true;
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        String[] pos = name.split(",");
        System.out.println("panel setName: x->"+pos[0]);
        System.out.println("y->"+pos[1]);

        //收到敌人的下棋步子
        int x = Integer.parseInt(pos[0]);
        int y = Integer.parseInt(pos[1]);
        Qi qi = new Qi(x,y,enemyColor);
        enemyQies.add(qi);

        Point enemyUsedPos = findFitPoint(x,y);
        pointStatus[enemyUsedPos.x][enemyUsedPos.y].isUsed= true;
        pointStatus[enemyUsedPos.x][enemyUsedPos.y].whoUsed= UsedPoint.ENEMY;
        System.out.println("enemy used pos: x->"+x+" y->"+y);

        if(isGameOver(enemyUsedPos,UsedPoint.ENEMY)){
            gameOver(enemyColor);
        }
        //然后我就可点，显示该我下棋
        clickAble = true;
        nextColor = myColor;
        this.repaint();
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i <= row; i++) {
            for (int j = 0; j <= col; j++) {
                if(points[i][j] == null){
                    points[i][j] = new Point(i * span + 90, j * span + 30);
                    System.out.println("add point:x." + (i * span + 90) + " y." + (j * span + 30));
                }
                g.drawLine(90, j * span + 30, col * span + 90, j * span + 30);///画横线
            }
            g.drawLine(i * span + 90, 30, i * span + 90, row * span + 30);///画竖线
        }

        for(int j=0;j<myQies.size();j++){
            Qi t = myQies.get(j);
            g.setColor(myColor);
            g.fillOval(t.x-15,t.y-15,30,30);
        }
        for(int j=0;j<enemyQies.size();j++){
            Qi t = enemyQies.get(j);
            g.setColor(enemyColor);
            g.fillOval(t.x-15,t.y-15,30,30);
        }



        //画我
        g.setColor(myColor);
        g.fillOval(300-15,540-15,30,30);
        g.fillRect(280,555,40,53);
        g.drawString("Me",330,575);

        //画敌人
        g.setColor(enemyColor);
        g.fillOval(655-15,45-15,30,30);
        g.fillRect(635,60,40,53);
        g.drawString("Enemy",685,83);

        //画出该谁下棋
        g.setColor(nextColor);
        g.fillRect(655,480,40,40);

        g.setColor(Color.BLACK);
    }

    public Point findFitPoint(int clickX,int clickY){
        Point temp = new Point(-1,-1);
        for(int i=0;i<=row;i++){
            for(int j=0;j<=col;j++){
                Point t = points[i][j];
                if((clickX>t.x-15&&clickX<t.x+15)
                    &&(clickY>t.y-15&&clickY<t.y+15)){
                    temp.x = i;
                    temp.y = j;
                    return temp;
                }
            }
        }
        return temp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("click:x->"+e.getX()+" y->"+e.getY());
        if(clickAble){
            ///必须收到敌人的数据包以后才轮到我点击
            if((e.getX()>80&&e.getX()<550)&&(e.getY()>20&&e.getY()<490)) {
                Point myUsedPos = findFitPoint(e.getX(), e.getY());///返回点的下标
                if (myUsedPos.x < 0)
                    return;
                if (!pointStatus[myUsedPos.x][myUsedPos.y].isUsed) {/// 处理已经有的棋子
                    pointStatus[myUsedPos.x][myUsedPos.y].isUsed = true;
                    pointStatus[myUsedPos.x][myUsedPos.y].whoUsed = UsedPoint.ME;
                    int x = points[myUsedPos.x][myUsedPos.y].x;
                    int y = points[myUsedPos.x][myUsedPos.y].y;
                    System.out.println("fit:x->" + x + " y->" + y);

                    ///点了位置以后，添加我的棋子
                    Qi qi = new Qi(x, y, myColor);
                    myQies.add(qi);

                    //把我下的步子信息告诉对方
                    String stepInfo = "step:"+x+","+y;
                    System.out.println("send stepInfo:"+stepInfo);
                    sendStepInfo(x,y);

                    //我下完以后，该对手下
                    clickAble = false;
                    nextColor = enemyColor;

                    repaint();
                    /// 判断输赢
                    if(isGameOver(myUsedPos,UsedPoint.ME)){
                        gameOver(myColor);
                    }
                }
            }else{
                System.out.println("position have been used");
            }
        }else{
            System.out.println("It's not your turn!");
        }
    }

    public boolean isGameOver(Point myUsedPos,int judgeWho){
        boolean isOver = false;
        //遍历八个方向
        for(int i=0;i<4;i++){
            int count = 0;
            boolean flg = true;
            int tempX = myUsedPos.x;
            int tempY = myUsedPos.y;

            int otherDir = 1;

            while(flg){
                //向该棋子周围的八个方向移动
                tempX += (otherDir*dirs[i].x);
                tempY += (otherDir*dirs[i].y);

                //如果周围的点是我已经下过的点，就继续以那个点继续向同一个方向移动
                if(pointStatus[tempX][tempY].whoUsed == judgeWho){
                    count++;
                    if(count>=4){
                        isOver = true;
                        break;
                    }
                }else{///该点周围的点不是我的店,转到相反方向继续判断
                    if(otherDir != -1){//如果相反方向还没判断过
                        otherDir = -1;
                        tempX = myUsedPos.x;
                        tempY = myUsedPos.y;
                    }else{//相反方向已经判断过
                        flg = false;
                    }
                }
            }
            if(isOver){
                break;
            }
        }
        return isOver;
    }
    //游戏结束
    public abstract void gameOver(Color whoWin);
    public abstract void sendStepInfo(int x,int y);


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
