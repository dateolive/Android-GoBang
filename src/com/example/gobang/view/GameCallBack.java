package com.example.gobang.view;

public interface GameCallBack {
    //��Ϸ�����ص�
    void GameOver(int winner);
    //��Ϸ����ִ�ӻص�
    void ChangeGamer(boolean isWhite);
}