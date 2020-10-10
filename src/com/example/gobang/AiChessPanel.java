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
	 //������UI
    private AiWuziqiPanel fiveChessView;
    //��ʾ�û��Լ�ai�÷�
    private TextView userScoreTv, aiScoreTv;
    //��ʾ���/aiִ��
    private ImageView userChessIv, aiChessIv;
    //���/ai�غϱ�ʶ
    private ImageView userTimeIv, aiTimeIv;
    //��Ϸai
    private AI ai;
    private Button btn_play_button;
    //PopUpWindowѡ�����ִ��
    private PopupWindow chooseChess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ai_chess_panel);
		//��ʼ���ؼ�
        initViews();
        //��ʼ��ai
        ai = new AI(fiveChessView.getChessArray(), this);
        //view�������
        fiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //��ʼ��PopupWindow
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
                initPop(dm.widthPixels,dm.heightPixels);
            }
        });
	}

	 private void initViews() {
	        //������UI
	        fiveChessView = (AiWuziqiPanel) findViewById(R.id.AiWuziqiPanel);
	        fiveChessView.setCallBack(this);
	        
	        //��ʾ�û��Լ�ai�÷�
	        userScoreTv = (TextView) findViewById(R.id.user_score_tv);
	        aiScoreTv = (TextView) findViewById(R.id.ai_score_tv);
	        //��ʾ���/aiִ��
	        userChessIv = (ImageView) findViewById(R.id.user_chess_iv);
	        aiChessIv = (ImageView) findViewById(R.id.ai_chess_iv);
	        //���/ai�غϱ�ʶ
	        userTimeIv = (ImageView) findViewById(R.id.user_think_iv);
	        aiTimeIv = (ImageView) findViewById(R.id.ai_think_iv);
	        //�ؿ���Ϸ���õ���¼�
	        findViewById(R.id.restart_game).setOnClickListener(this);
	        
	    }


	    //��ʼ��PopupWindow
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
	        //������Ϸʤ������
	        updateWinInfo();
	        switch (winner) {
	            case AiWuziqiPanel.BLACK_WIN:
	                showToast("����ʤ����");
	                break;
	            case AiWuziqiPanel.NO_WIN:
	                showToast("ƽ�֣�");
	                break;
	            case AiWuziqiPanel.WHITE_WIN:
	                showToast("����ʤ����");
	                break;
	        }
	    }

	    //������Ϸʤ������
	    private void updateWinInfo() {
	        userScoreTv.setText(fiveChessView.getUserScore() + " ");
	        aiScoreTv.setText(fiveChessView.getAiScore() + " ");
	    }

	    @Override
	    public void ChangeGamer(boolean isWhite) {
	        //ai�غ�
	        ai.aiBout();
	        //���ĵ�ǰ����
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
	                //����UI
	                fiveChessView.postInvalidate();
	                //�����Ϸ�Ƿ����
	                fiveChessView.checkAiGameOver();
	                //����Ϊ��һغ�
	                fiveChessView.setUserBout(true);
	                //���ĵ�ǰ����
	                aiTimeIv.setVisibility(View.GONE);
	                userTimeIv.setVisibility(View.VISIBLE);
	            }
	        });
	    }

	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.restart_game:
	                //��ʾPopupWindow
	                chooseChess.showAtLocation(fiveChessView, Gravity.CENTER, 0, 0);
	                //���¿�ʼ��Ϸ
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

	    //�������ѡ��ִ�ӣ�����UI
	    private void changeUI(boolean isUserWhite) {
	        if (isUserWhite) {
	            //���ѡ�����
	            fiveChessView.setUserChess(AiWuziqiPanel.WHITE_CHESS);
	            ai.setAiChess(AiWuziqiPanel.BLACK_CHESS);
	            //�������
	            fiveChessView.setUserBout(true);
	            //���ĵ�ǰ����
	            userChessIv.setBackgroundResource(R.drawable.icon_white_piece);
	            aiChessIv.setBackgroundResource(R.drawable.icon_black_piece);
	            aiTimeIv.setVisibility(View.GONE);
	            userTimeIv.setVisibility(View.VISIBLE);
	        } else {
	            //���ѡ�����
	            fiveChessView.setUserChess(AiWuziqiPanel.BLACK_CHESS);
	            fiveChessView.setUserBout(false);
	            //ai����
	            ai.setAiChess(AiWuziqiPanel.WHITE_CHESS);
	            ai.aiBout();
	            //���ĵ�ǰ����
	            userChessIv.setBackgroundResource(R.drawable.icon_black_piece);
	            aiChessIv.setBackgroundResource(R.drawable.icon_white_piece);
	            aiTimeIv.setVisibility(View.VISIBLE);
	            userTimeIv.setVisibility(View.GONE);
	        }
	    }
}
