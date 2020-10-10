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
	//棋盘宽度
	private int PanelWidth;
	//棋盘格子的高度
	private float LineHeight;
	//棋盘最大行列数
	private int MAX_LINE_NUM=15;
	//定义画笔绘制棋盘
	private Paint mPaint=new Paint();
	//定义黑白棋子的Bitmap
	private Bitmap mWhitePiece;
	private Bitmap mBlackPiece;
	//棋子的缩放比例
	private float pieceScaleRatio=3*1.0f/4;
	//存储黑白棋子的坐标
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    //哪方先下子
    private boolean isWhiteFirst = false;
    private final int INIT_WIN=-1;//初始化游戏状态
    public static final int White_Win=0;//白棋赢得胜利
    public static final int Black_Win=1;//黑棋赢得胜利
    public static final int No_Win=2;//和棋
    //游戏结果
    private int Winresult=INIT_WIN;
    //游戏是否结束
    private boolean isGameOver;
  //保存坐标
    private Point mPoint;
  //游戏结束监听
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
     * 初始化设置
     */
    private void init() {
        //初始化画笔
        mPaint.setColor(Color.BLACK);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置防抖动
        mPaint.setDither(true);
        //设置为空心(画线)
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_black_piece);
    }
    /**
     * 测量
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

        //此处的逻辑判断是处理当我们自定义的View被嵌套在ScrollView中时,获得的测量模式
        // 会是UNSPECIFIED
        // 使得到的widthSize或者heightSize为0
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        Log.d("pyh", "onMeasure: width" + width + "height" + heightSize);
        //调用此方法使我们的测量结果生效
        setMeasuredDimension(width, width);
    }

    /**
     * 当宽高发生变化时回调此方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("pyh", "onSizeChanged: w" + w + "h" + h);

        //此处的参数w就是在onMeasure()方法中设置的自定义View的大小
        //计算出棋盘宽度和行高
        PanelWidth = w;
        LineHeight = PanelWidth * 1.0f / MAX_LINE_NUM;

        //将棋子根据行高变化
        int pieceWidth = (int) (pieceScaleRatio * LineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    /**
     * 进行绘制工作
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
     * 绘制棋盘
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = PanelWidth;
        float lineHeight = LineHeight;

        for (int i = 0; i < MAX_LINE_NUM; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            //画横线
            canvas.drawLine(startX, y, endX, y, mPaint);
            //画竖线
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    /**
     * 
    * @Title: drawPieces 
    * @Description: TODO(绘制棋子) 
    * @param @param canvas  参数说明 
    * @return void    返回类型 
    * @throws
     */
    private void drawPieces(Canvas canvas) {
        //绘制白棋子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            //棋子之间的间隔为1/4行高
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - pieceScaleRatio) / 2) * LineHeight,
                    (whitePoint.y + (1 - pieceScaleRatio) / 2) * LineHeight, null);
        }
        //绘制黑棋子
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            //棋子之间的间隔为1/4行高
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - pieceScaleRatio) / 2) * LineHeight,
                    (blackPoint.y + (1 - pieceScaleRatio) / 2) * LineHeight, null);
        }
    }

    /**
     * 处理用户手势操作
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return false;

        int action = event.getAction();
        //手指抬起后处理
        if (action == MotionEvent.ACTION_UP){

            //拦截事件自己来处理
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
            //调用重绘
            invalidate();
            isWhiteFirst = !isWhiteFirst;
        }
        return true;
    }

    /**
     * 将用户点击的位置的Point转换为类似于(0,0)d的坐标
     * @param x
     * @param y
     * @return
     */
    private Point getValidPoint(int x, int y) {
        Log.d("pyh1", "getValidPoint: lx" + (int) (x / LineHeight) + "ly" + (int) (y / LineHeight));
        return new Point((int) (x / LineHeight), (int) (y / LineHeight));
    }

    /**
     * 检查游戏是否结束
     */
    private void checkGameOver() {
        //检查是否五子连珠
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
     * 重新开始,再来一局
     */
    public void restartGame(){
        mBlackArray.clear();
        mWhiteArray.clear();
        isGameOver = false;
        isWhiteFirst=false;
        Winresult=INIT_WIN;
        //重绘
        invalidate();
    }
  //悔棋
    public void Return() {
        if (isWhiteFirst) { //当前该白方下棋，意味着黑方式上一个下棋者，为黑方悔棋
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
     * 防止内存不足活动被回收
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //保存游戏数据
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }
    //恢复游戏数据
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
     * 设置游戏结束回调监听
     * @param onGameOverListener
     */
    public void setOnGameOverListener(OnGameOverListener onGameOverListener){
        this.onGameOverListener = onGameOverListener;
    }

    /**
     * 游戏结束回调监听
     */
    public interface OnGameOverListener{
       void gameOver(int Winresult);
    }

    /**
     * 设置谁先下棋
     *这里使用随机先手下棋或抢棋
     */
    public void setFirstPiece(boolean isWhiteFirst){
        this.isWhiteFirst = isWhiteFirst;
        
    }
}
