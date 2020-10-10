package com.example.gobang.view;

public interface GameCallBack {
    //游戏结束回调
    void GameOver(int winner);
    //游戏更换执子回调
    void ChangeGamer(boolean isWhite);
}