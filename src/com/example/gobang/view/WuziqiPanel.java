package com.example.gobang.view;

import java.util.ArrayList;

import com.example.gobang.R;
import com.example.gobang.util.WuziqiUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class WuziqiPanel extends View{
	//���̿��
	private int PanelWidth;
	//���̸��ӵĸ߶�
	private float LineHeight;
	//�������������
	private int MAX_LINE_NUM=15;
	//���廭�ʻ�������
	private Paint mPaint=new Paint();
	//����ڰ����ӵ�Bitmap
	private Bitmap mWhitePiece;
	private Bitmap mBlackPiece;
	//���ӵ����ű���
	private float pieceScaleRatio=3*1.0f/4;
	//�洢�ڰ����ӵ�����
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    //�ķ�������
    private boolean isWhiteFirst = false;
    private final int INIT_WIN=-1;//��ʼ����Ϸ״̬
    public static final int White_Win=0;//����Ӯ��ʤ��
    public static final int Black_Win=1;//����Ӯ��ʤ��
    public static final int No_Win=2;//����
    //��Ϸ���
    private int Winresult=INIT_WIN;
    //��Ϸ�Ƿ����
    private boolean isGameOver;
  //��������
    private Point mPoint;
  //��Ϸ��������
    private OnGameOverListener onGameOverListener;
    public WuziqiPanel(Context context) {
        this(context, null);
    }

    public WuziqiPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * ��ʼ������
     */
    private void init() {
        //��ʼ������
        mPaint.setColor(Color.BLACK);
        //���ÿ����
        mPaint.setAntiAlias(true);
        //���÷�����
        mPaint.setDither(true);
        //����Ϊ����(����)
        mPaint.setStyle(Paint.Style.STROKE);

        //��ʼ������
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_black_piece);
    }
    /**
     * ����
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //�˴����߼��ж��Ǵ��������Զ����View��Ƕ����ScrollView��ʱ,��õĲ���ģʽ
        // ����UNSPECIFIED
        // ʹ�õ���widthSize����heightSizeΪ0
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        Log.d("pyh", "onMeasure: width" + width + "height" + heightSize);
        //���ô˷���ʹ���ǵĲ��������Ч
        setMeasuredDimension(width, width);
    }

    /**
     * ����߷����仯ʱ�ص��˷���
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("pyh", "onSizeChanged: w" + w + "h" + h);

        //�˴��Ĳ���w������onMeasure()���������õ��Զ���View�Ĵ�С
        //��������̿�Ⱥ��и�
        PanelWidth = w;
        LineHeight = PanelWidth * 1.0f / MAX_LINE_NUM;

        //�����Ӹ����и߱仯
        int pieceWidth = (int) (pieceScaleRatio * LineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    /**
     * ���л��ƹ���
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    /**
     * ��������
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = PanelWidth;
        float lineHeight = LineHeight;

        for (int i = 0; i < MAX_LINE_NUM; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            //������
            canvas.drawLine(startX, y, endX, y, mPaint);
            //������
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    /**
     * 
    * @Title: drawPieces 
    * @Description: TODO(��������) 
    * @param @param canvas  ����˵�� 
    * @return void    �������� 
    * @throws
     */
    private void drawPieces(Canvas canvas) {
        //���ư�����
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            //����֮��ļ��Ϊ1/4�и�
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - pieceScaleRatio) / 2) * LineHeight,
                    (whitePoint.y + (1 - pieceScaleRatio) / 2) * LineHeight, null);
        }
        //���ƺ�����
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            //����֮��ļ��Ϊ1/4�и�
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - pieceScaleRatio) / 2) * LineHeight,
                    (blackPoint.y + (1 - pieceScaleRatio) / 2) * LineHeight, null);
        }
    }

    /**
     * �����û����Ʋ���
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return false;

        int action = event.getAction();
        //��ָ̧�����
        if (action == MotionEvent.ACTION_UP){

            //�����¼��Լ�������
            int x = (int) event.getX();
            int y = (int) event.getY();
            Log.d("pyh1", "onTouchEvent: fx" + event.getX() + "fy" + event.getY() + "ix" + x + "iy" + y);
            Point point = getValidPoint(x, y);
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)){
                return false;
            }
            if (isWhiteFirst){
                mWhiteArray.add(point);
            }else{
                mBlackArray.add(point);
            }
            //�����ػ�
            invalidate();
            isWhiteFirst = !isWhiteFirst;
        }
        return true;
    }

    /**
     * ���û������λ�õ�Pointת��Ϊ������(0,0)d������
     * @param x
     * @param y
     * @return
     */
    private Point getValidPoint(int x, int y) {
        Log.d("pyh1", "getValidPoint: lx" + (int) (x / LineHeight) + "ly" + (int) (y / LineHeight));
        return new Point((int) (x / LineHeight), (int) (y / LineHeight));
    }

    /**
     * �����Ϸ�Ƿ����
     */
    private void checkGameOver() {
        //����Ƿ���������
        boolean whiteWin = WuziqiUtil.checkFiveInLine(mWhiteArray);
        boolean blackWin = WuziqiUtil.checkFiveInLine(mBlackArray);
        boolean noWin=checkNoWin(whiteWin, blackWin);
        if(whiteWin){
        	Winresult=White_Win;
        }else if(blackWin){
        	Winresult=Black_Win;
        }else if(noWin){
        	Winresult=No_Win;
        }
        if(whiteWin||blackWin||noWin){
        	isGameOver=true;
        	if(onGameOverListener!=null)
        		onGameOverListener.gameOver(Winresult);
        }
        
    }
    private boolean checkNoWin(boolean whiteWin,boolean blackWin){
    	if(whiteWin||blackWin){
    		return false;
    	}
    	int maxSize=MAX_LINE_NUM*MAX_LINE_NUM;
    	if(mWhiteArray.size()+mBlackArray.size()==maxSize){
    		return true;
    	}
    	return false;
    }
    /**
     * ���¿�ʼ,����һ��
     */
    public void restartGame(){
        mBlackArray.clear();
        mWhiteArray.clear();
        isGameOver = false;
        isWhiteFirst=false;
        Winresult=INIT_WIN;
        //�ػ�
        invalidate();
    }
  //����
    public void Return() {
        if (isWhiteFirst) { //��ǰ�ð׷����壬��ζ�źڷ�ʽ��һ�������ߣ�Ϊ�ڷ�����
            if (mBlackArray != null && !mBlackArray.isEmpty()) {
                mBlackArray.remove(mBlackArray.size() - 1);
                if (mWhiteArray != null && !mWhiteArray.isEmpty()) {
                	mPoint = mWhiteArray.get(mWhiteArray.size() - 1);
                }
                isWhiteFirst = !isWhiteFirst;
                invalidate();
                return;
            }
        } else if (!isWhiteFirst) {
            if (mWhiteArray != null && !mWhiteArray.isEmpty()) {
                mWhiteArray.remove(mWhiteArray.size() - 1);
                if (mBlackArray != null && !mBlackArray.isEmpty()) {
                    mPoint = mBlackArray.get(mBlackArray.size() - 1);
                }
                isWhiteFirst = !isWhiteFirst;
                invalidate();
                return;
            }
        }
    }
    /**
     * ��ֹ�ڴ治��������
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //������Ϸ����
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }
    //�ָ���Ϸ����
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            isGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * ������Ϸ�����ص�����
     * @param onGameOverListener
     */
    public void setOnGameOverListener(OnGameOverListener onGameOverListener){
        this.onGameOverListener = onGameOverListener;
    }

    /**
     * ��Ϸ�����ص�����
     */
    public interface OnGameOverListener{
       void gameOver(int Winresult);
    }

    /**
     * ����˭������
     *����ʹ������������������
     */
    public void setFirstPiece(boolean isWhiteFirst){
        this.isWhiteFirst = isWhiteFirst;
        
    }
}
