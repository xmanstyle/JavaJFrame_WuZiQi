package com.view;

import java.awt.*;

/**
 * Created by Xman on 2017/6/13 0013.
 */
public class Point {
    public int x;
    public int y;
    public Point(int _x,int _y){
        this.x = _x;
        this.y = _y;
    }
}

class Qi extends Point
{
    Color color;

    public Qi(int x,int y,Color color){
        super(x,y);
        this.color = color;
    }
}

class UsedPoint
{
    static final int ME = 0;//ME 使用
    static final int ENEMY = 1;//ENEMY 使用
    static final int NONE = 2;//没人使用
    boolean isUsed;
    int whoUsed;//谁使用了这个点
    int indexX;
    int indexY;
    public UsedPoint(int x,int y,boolean isUsed,int whoUsed){
        this.isUsed = isUsed;
        this.whoUsed = whoUsed;
        this.indexX = x;
        this.indexY = y;
    }
}
