package com.example.gobang.util;

public class Point {
    //��������λ��
    private int x, y;
    //�õ�����ȼ���AI�������ȼ����ӣ�
    private int priority = 0;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", priority=" + priority +
                '}';
    }
}
