package com.view;

import javax.swing.*;

/**
 * Created by Administrator on 2017/6/15.
 */
public class SButton extends JButton {

    private int id;

    public int getId() {
        return id;
    }

    public SButton(int _id) {
        super();
        id = _id;
    }

    public SButton(Icon icon, int _id) {
        super(icon);
        id = _id;
    }

    public SButton(String text, int _id) {
        super(text);
        id = _id;
    }

    public SButton(Action a, int _id) {
        super(a);
        id = _id;
    }

    public SButton(String text, Icon icon, int _id) {
        super(text, icon);
        id = _id;
    }
}
