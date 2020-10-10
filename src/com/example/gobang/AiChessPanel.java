package com.example.gobang;

import com.example.gobang.util.AI;
import com.example.gobang.util.AiCallBack;
import com.example.gobang.view.AiWuziqiPanel;
import com.example.gobang.view.GameCallBack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class AiChessPanel extends Activity implements GameCallBack, AiCallBack, View.OnClickListener{
	 //五子棋UI
    private AiWuziqiPanel fiveChessView;
    //显示用户以及ai得分
    private TextView userScoreTv, aiScoreTv;
    //显示玩家/ai执子
    private ImageView userChessIv, aiChessIv;
    //玩家/ai回合标识
    private ImageView userTimeIv, aiTimeIv;
    //游戏ai
    private AI ai;
    private Button btn_play_button;
    //PopUpWindow选择玩家执子
    private PopupWindow chooseChess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ai_chess_panel);
		//初始化控件
        initViews();
        //初始化ai
        ai = new AI(fiveChessView.getChessArray(), this);
        //view加载完成
        fiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
                initPop(dm.widthPixels,dm.heightPixels);
            }
        });
	}

	 private void initViews() {
	        //五子棋UI
	        fiveChessView = (AiWuziqiPanel) findViewById(R.id.AiWuziqiPanel);
	        fiveChessView.setCallBack(this);
	        
	        //显示用户以及ai得分
	        userScoreTv = (TextView) findViewById(R.id.user_score_tv);
	        aiScoreTv = (TextView) findViewById(R.id.ai_score_tv);
	        //显示玩家/ai执子
	        userChessIv = (ImageView) findViewById(R.id.user_chess_iv);
	        aiChessIv = (ImageView) findViewById(R.id.ai_chess_iv);
	        //玩家/ai回合标识
	        userTimeIv = (ImageView) findViewById(R.id.user_think_iv);
	        aiTimeIv = (ImageView) findViewById(R.id.ai_think_iv);
	        //重开游戏设置点击事件
	        findViewById(R.id.restart_game).setOnClickListener(this);
	        
	    }


	    //初始化PopupWindow
	    private void initPop(int width, int height) {
	        if (chooseChess == null) {
	            View view = View.inflate(this, R.layout.pop_choose_chess, null);
	            ImageButton white = (ImageButton) view.findViewById(R.id.choose_white);
	            ImageButton black = (ImageButton) view.findViewById(R.id.choose_black);
	            white.setOnClickListener(this);
	            black.setOnClickListener(this);
	            chooseChess = new PopupWindow(view, width, height);
	            chooseChess.setOutsideTouchable(false);
	            chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
	        }
	    }

	    @Override
	    public void GameOver(int winner) {
	        //更新游戏胜利局数
	        updateWinInfo();
	        switch (winner) {
	            case AiWuziqiPanel.BLACK_WIN:
	                showToast("黑棋胜利！");
	                break;
	            case AiWuziqiPanel.NO_WIN:
	                showToast("平局！");
	                break;
	            case AiWuziqiPanel.WHITE_WIN:
	                showToast("白棋胜利！");
	                break;
	        }
	    }

	    //更新游戏胜利局数
	    private void updateWinInfo() {
	        userScoreTv.setText(fiveChessView.getUserScore() + " ");
	        aiScoreTv.setText(fiveChessView.getAiScore() + " ");
	    }

	    @Override
	    public void ChangeGamer(boolean isWhite) {
	        //ai回合
	        ai.aiBout();
	        //更改当前落子
	        aiTimeIv.setVisibility(View.VISIBLE);
	        userTimeIv.setVisibility(View.GONE);
	    }

	    private void showToast(String str) {
	        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public void aiAtTheBell() {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                //更新UI
	                fiveChessView.postInvalidate();
	                //检查游戏是否结束
	                fiveChessView.checkAiGameOver();
	                //设置为玩家回合
	                fiveChessView.setUserBout(true);
	                //更改当前落子
	                aiTimeIv.setVisibility(View.GONE);
	                userTimeIv.setVisibility(View.VISIBLE);
	            }
	        });
	    }

	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.restart_game:
	                //显示PopupWindow
	                chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
	                //重新开始游戏
	                fiveChessView.resetGame();
	                break;
	            case R.id.choose_black:
	                changeUI(true);
	                chooseChess.dismiss();
	                break;
	            case R.id.choose_white:
	                changeUI(false);
	                chooseChess.dismiss();
	                break;
	          
	        }
	    }

	    //根据玩家选择执子，更新UI
	    private void changeUI(boolean isUserWhite) {
	        if (isUserWhite) {
	            //玩家选择白棋
	            fiveChessView.setUserChess(AiWuziqiPanel.WHITE_CHESS);
	            ai.setAiChess(AiWuziqiPanel.BLACK_CHESS);
	            //玩家先手
	            fiveChessView.setUserBout(true);
	            //更改当前落子
	            userChessIv.setBackgroundResource(R.drawable.icon_white_piece);
	            aiChessIv.setBackgroundResource(R.drawable.icon_black_piece);
	            aiTimeIv.setVisibility(View.GONE);
	            userTimeIv.setVisibility(View.VISIBLE);
	        } else {
	            //玩家选择黑棋
	            fiveChessView.setUserChess(AiWuziqiPanel.BLACK_CHESS);
	            fiveChessView.setUserBout(false);
	            //ai先手
	            ai.setAiChess(AiWuziqiPanel.WHITE_CHESS);
	            ai.aiBout();
	            //更改当前落子
	            userChessIv.setBackgroundResource(R.drawable.icon_black_piece);
	            aiChessIv.setBackgroundResource(R.drawable.icon_white_piece);
	            aiTimeIv.setVisibility(View.VISIBLE);
	            userTimeIv.setVisibility(View.GONE);
	        }
	    }
}
