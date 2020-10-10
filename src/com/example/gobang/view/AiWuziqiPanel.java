package com.example.gobang.view;

import java.util.ArrayList;
import java.util.Random;

import com.example.gobang.AiChessPanel;
import com.example.gobang.R;
import com.example.gobang.util.WuziqiUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AiWuziqiPanel extends View implements View.OnTouchListener{
	 //����
    private Paint paint;
    //��������
    private int[][] chessArray;
    //��ǰ����˳��(Ĭ�ϰ�������)
    private boolean isWhite = true;
    //��Ϸ�Ƿ����
    private boolean isGameOver = false;

    //bitmap
    private Bitmap whiteChess;
    private Bitmap blackChess;
    //Rect
    private Rect rect;
    //���̿��
    private float len;
    //���̸���
    private int GRID_NUMBER = 15;
    //ÿ��֮��ľ���
    private float preWidth;
    //�߾�
    private float offset;
    //�ص�
    private GameCallBack callBack;
    //��ǰ�ڰ���ʤ������
    private int whiteChessCount, blackChessCount;
    //�Ƿ�����ҵĻغ�
    private boolean isUserBout = true;
    //���ִ����ɫ
    private int userChess = WHITE_CHESS;
    //���/AIʤ������
    private int userScore = 0, aiScore = 0;
    /**
     * һЩ����
     */
    //����
    public static final int WHITE_CHESS = 1;
    //����
    public static final int BLACK_CHESS = 2;
    //����
    public static final int NO_CHESS = 0;
    //����Ӯ
    public static final int WHITE_WIN = 101;
    //����Ӯ
    public static final int BLACK_WIN = 102;
    //ƽ��
    public static final int NO_WIN = 103;

    public AiWuziqiPanel(Context context) {
        this(context, null);
    }

    public AiWuziqiPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AiWuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //��ʼ��Paint
        paint = new Paint();
        //���ÿ����
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        //��ʼ��chessArray
        chessArray = new int[GRID_NUMBER][GRID_NUMBER];
        //��ʼ������ͼƬbitmap
        whiteChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_white_piece);
        blackChess = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_black_piece);
        //��ʼ��ʤ������
        whiteChessCount = 0;
        blackChessCount = 0;
        //��ʼ��Rect
        rect = new Rect();
        //���õ������
        setOnTouchListener(this);
        //��������״̬
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
    }

    /**
     * ���²�����ߣ�ȷ�����һ��
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //��ȡ�߿�ֵ
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //��ȡ����н�С��ֵ
        int len = width > height ? height : width;
        //�������ÿ��
        setMeasuredDimension(len, len);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //����Ϊһ��GRID_NUMBER*GRID_NUMBER�������Σ��������̿�߱���һ��
        len = getWidth() > getHeight() ? getHeight() : getWidth();
        preWidth = len / GRID_NUMBER;
        //�߾�
        offset = preWidth / 2;
        //��������
        for (int i = 0; i < GRID_NUMBER; i++) {
            float start = i * preWidth + offset;
            //����
            canvas.drawLine(offset, start, len - offset, start, paint);
            //����
            canvas.drawLine(start, offset, start, len - offset, paint);
        }
        //��������
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //rect�е�����
                float rectX = offset + i * preWidth;
                float rectY = offset + j * preWidth;
                //����rectλ��
                rect.set((int) (rectX - offset), (int) (rectY - offset),
                        (int) (rectX + offset), (int) (rectY + offset));
                //����chessArray
                switch (chessArray[i][j]) {
                    case WHITE_CHESS:
                        //���ư���
                        canvas.drawBitmap(whiteChess, null, rect, paint);
                        break;
                    case BLACK_CHESS:
                        //���ƺ���
                        canvas.drawBitmap(blackChess, null, rect, paint);
                        break;
                }
            }
        }
    }

    /**
     * �ж��Ƿ����
     */
    private void checkGameOver() {
        //��ȡ���ӵ���ɫ(�����ǰ�ǰ��壬�������Ǻ���)
        int chess = isWhite ? BLACK_CHESS : WHITE_CHESS;
        //�����Ƿ�����
        boolean isFull = true;
        //����chessArray
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //�ж������Ƿ�����
                if (chessArray[i][j] != BLACK_CHESS && chessArray[i][j] != WHITE_CHESS) {
                    isFull = false;
                }
                //ֻ��Ҫ�ж������Ƿ���������
                if (chessArray[i][j] == chess) {
                    //�ж���������
                    if (isFiveSame(i, j)) {
                        //����������Ϸ����
                        isGameOver = true;
                        if (callBack != null) {
                            //�жϺڰ���ʤ��
                            if (chess == WHITE_CHESS) {
                                whiteChessCount++;
                            } else {
                                blackChessCount++;
                            }
                            //�ж����/AI ʤ��
                            if (userChess == chess) {
                                userScore++;
                            } else {
                                aiScore++;
                            }
                            callBack.GameOver(chess == WHITE_CHESS ? WHITE_WIN : BLACK_WIN);
                        }
                        return;
                    }
                }
            }
        }
        //�������������ƽ�ֽ���
        if (isFull) {
            isGameOver = true;
            if (callBack != null) {
                callBack.GameOver(NO_WIN);
            }
        }
    }
    /**
     * 
    * @Title: nudo 
    * @Description: TODO(Ai���ӵĻ���������жϵ�ǰ�Ƿ�Ϊ������ң����Ϊ������Ҳ��л���) 
    * @param   ����˵�� 
    * @return void    �������� 
    * @throws
     */
    public void nudo(){
    	//Ϊ�������
    	if(!isUserBout){
    		if(userChess==WHITE_CHESS){
    			if(chessArray!=null&&chessArray.length!=0){
    				chessArray[chessArray.length-1][chessArray.length-1]=0;
    				postInvalidate();
    			}
    		}else if(userChess==BLACK_CHESS){
    			if(chessArray!=null&&chessArray.length!=0){
    				chessArray[chessArray.length-1][chessArray.length-1]=0;
    				postInvalidate();
    			}
    	}
    	}
    }
    /**
     * ������Ϸ
     */
    public void resetGame() {
        isGameOver = false;
        //��������״̬
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
        //����UI
        postInvalidate();
    }

    /**
     * �ж��Ƿ������������
     *
     * @return
     */
    private boolean isFiveSame(int x, int y) {
        //�жϺ���
        if (x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y] && chessArray[x][y] == chessArray[x + 2][y]
                    && chessArray[x][y] == chessArray[x + 3][y] && chessArray[x][y] == chessArray[x + 4][y]) {
                return true;
            }
        }
        //�ж�����
        if (y + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x][y + 1] && chessArray[x][y] == chessArray[x][y + 2]
                    && chessArray[x][y] == chessArray[x][y + 3] && chessArray[x][y] == chessArray[x][y + 4]) {
                return true;
            }
        }
        //�ж�б��(���ϵ�����)
        if (y + 4 < GRID_NUMBER && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y + 1] && chessArray[x][y] == chessArray[x + 2][y + 2]
                    && chessArray[x][y] == chessArray[x + 3][y + 3] && chessArray[x][y] == chessArray[x + 4][y + 4]) {
                return true;
            }
        }
        //�ж�б��(���µ�����)
        if (y - 4 > 0 && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y - 1] && chessArray[x][y] == chessArray[x + 2][y - 2]
                    && chessArray[x][y] == chessArray[x + 3][y - 3] && chessArray[x][y] == chessArray[x + 4][y - 4]) {
                return true;
            }
        }
        return false;
    }

    //�����ж���Ϸ����
    public void checkAiGameOver() {
        isWhite = userChess == WHITE_CHESS;
        checkGameOver();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isGameOver && isUserBout) {
                    //��ȡ����ʱ��λ��
                    float downX = event.getX();
                    float downY = event.getY();
                    //�����λ����������
                    if (downX >= offset / 2 && downX <= len - offset / 2
                            && downY >= offset / 2 && downY <= len - offset / 2) {
                        //��ȡ���Ӷ�Ӧ��λ��
                        int x = (int) (downX / preWidth);
                        int y = (int) (downY / preWidth);
                        //�жϵ�ǰλ���Ƿ��Ѿ�����
                        if (chessArray[x][y] != WHITE_CHESS &&
                                chessArray[x][y] != BLACK_CHESS) {
                            //�����鸳ֵ
                            chessArray[x][y] = userChess;
                            //�޸ĵ�ǰ������ɫ
                            isWhite = userChess == WHITE_CHESS;
                            //�޸ĵ�ǰΪ����ִ��
                            isUserBout = false;
                            //��������
                            postInvalidate();
                            //�ж��Ƿ����
                            checkGameOver();
                            //�ص���ǰִ��
                            if (callBack != null) {
                                callBack.ChangeGamer(isWhite);
                            }
                        }
                    }
                } else if (isGameOver) {
                    Toast.makeText(getContext(), "��Ϸ�Ѿ������������¿�ʼ��",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    public void setCallBack(GameCallBack callBack) {
        this.callBack = callBack;
    }

    public int getWhiteChessCount() {
        return whiteChessCount;
    }

    public int getBlackChessCount() {
        return blackChessCount;
    }

    public int[][] getChessArray() {
        return chessArray;
    }

    public void setUserBout(boolean userBout) {
        isUserBout = userBout;
    }

    public void setUserChess(int userChess) {
        this.userChess = userChess;
    }

    public int getUserScore() {
        return userScore;
    }

    public int getAiScore() {
        return aiScore;
    }
}
