package com.example.gobang.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.gobang.view.AiWuziqiPanel;


public class AI implements Runnable {
    //������Ϣ
    private int[][] chessArray;
    //����ִ�ӣ�Ĭ�Ϻ��ӣ�
    private int aiChess = AiWuziqiPanel.BLACK_CHESS;
    //��������λ�õ���Ϣ����
    private List<Point> pointList;
    //ai���ӽ����ص�
    private AiCallBack callBack;
    //���̿�ߣ�panelLength��
    private int panelLength;
    /**
     * ���ֱ��������ȼ����֣�
     * FIVE ��������������
     * LIVE_X ��ʾX������һ����ӣ����߶�û�б���ס
     * DEAD_X ��ʾX������һ����ӣ�һ�߱���ס
     * DEAD ��ʾ���߱���ס
     */
    private final static int FIVE = 10000;
    private final static int LIVE_FOUR = 4500;
    private final static int DEAD_FOUR = 2000;
    private final static int LIVE_THREE = 900;
    private final static int DEAD_THREE = 400;
    private final static int LIVE_TWO = 150;
    private final static int DEAD_TWO = 70;
    private final static int LIVE_ONE = 30;
    private final static int DEAD_ONE = 15;
    private final static int DEAD = 1; 

    public AI(int[][] chessArray, AiCallBack callBack) {
        pointList = new ArrayList<>();
        this.chessArray = chessArray;
        this.callBack = callBack;
        this.panelLength = chessArray.length;
    }

    //ai��ʼ����
    public void aiBout() {
        new Thread(this).start();
    }

    //�ж����ӵ����ȼ�����
    private void checkPriority(Point p) {
        int aiPriority = checkSelf(p.getX(), p.getY());
        int userPriority = checkUser(p.getX(), p.getY());
        p.setPriority(aiPriority >= userPriority ? aiPriority : userPriority);
    }

    //��ȡ��ǰ�㣬ai���ȼ�����
    private int checkSelf(int x, int y) {
        return getHorizontalPriority(x, y, aiChess)
                + getVerticalPriority(x, y, aiChess)
                + getLeftSlashPriority(x, y, aiChess)
                + getRightSlashPriority(x, y, aiChess);
    }

    //��ȡ��ǰ�㣬������ȼ�����
    private int checkUser(int x, int y) {
        int userChess;
        if (aiChess == AiWuziqiPanel.WHITE_CHESS) {
            userChess = AiWuziqiPanel.BLACK_CHESS;
        } else {
            userChess = AiWuziqiPanel.WHITE_CHESS;
        }
        return getHorizontalPriority(x, y, userChess)
                + getVerticalPriority(x, y, userChess)
                + getLeftSlashPriority(x, y, userChess)
                + getRightSlashPriority(x, y, userChess);
    }

    //ͨ���߳�ѡ��������
    @Override
    public void run() {
        //���pointList
        pointList.clear();
        int blankCount = 0;
        for (int i = 0; i < panelLength; i++)
            for (int j = 0; j < panelLength; j++) {
                if (chessArray[i][j] == AiWuziqiPanel.NO_CHESS) {
                    Point p = new Point(i, j);
                    checkPriority(p);
                    pointList.add(p);
                    blankCount++;
                }
            }
        //����pointList���ҵ����ȼ���ߵ�Point
        Point max = pointList.get(0);
        for (Point point : pointList) {
            if (max.getPriority() < point.getPriority()) {
                max = point;
            }
        }
        //AI���ֻ����û����ֵ�һ������ʱ
        if (blankCount >= panelLength * panelLength - 1) {
            max = getStartPoint();
        }
        //����2��
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //���ӣ���������ص�
        chessArray[max.getX()][max.getY()] = aiChess;
        callBack.aiAtTheBell();
    }

    public void setAiChess(int aiChess) {
        this.aiChess = aiChess;
    }

    //AI���ֻ����û����ֵ�һ������ʱ�������ȡһ��������
    private Point getStartPoint() {
        //�õ��Ƿ���ñ�ʶ
        boolean isUse = true;
        //���м�λ���������һ����
        Random random = new Random();
        int x = random.nextInt(5) + 5;
        int y = random.nextInt(5) + 5;
        //ȷ����Χ��������������
        for (int i = x - 1; i <= x + 1; i++)
            for (int j = y - 1; j <= y + 1; j++) {
                if (chessArray[i][j] != AiWuziqiPanel.NO_CHESS) {
                    isUse = false;
                }
            }
        if (isUse) {
            return new Point(x, y);
        } else {
            return getStartPoint();
        }
    }

    /**
     * �ж�ָ����chessArray[x][y]�������ȼ�
     *
     * @param x     �����±�
     * @param y     �����±�
     * @param chess ������ɫ
     * @return �õ����ȼ�����
     */
    private int getHorizontalPriority(int x, int y, int chess) {
        //ָ������������
        int connectCount = 1;
        //����Ƿ񱻶�ס
        boolean isStartStem = false;
        //�ұ��Ƿ񱻶�ס
        boolean isEndStem = false;

        //������߼���
        //�����ǰλ��y = 0,�������̵ı�Եλ��,����߱�Ȼ����ס
        if (y == 0) {
            isStartStem = true;
        } else {
            //�������
            for (int i = y - 1; i >= 0; i--) {
                //�������ָ������
                if (chessArray[x][i] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isStartStem = chessArray[x][i] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == 0) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isStartStem = true;
                    }
                }
            }
        }

        //�����ұ߼���
        //�����ǰλ��y = panelLength,�������̵ı�Եλ��,���ұ߱�Ȼ����ס
        if (y == panelLength - 1) {
            isEndStem = true;
        } else {
            //�����ұ�
            for (int i = y + 1; i < panelLength; i++) {
                //�������ָ������
                if (chessArray[x][i] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isEndStem = chessArray[x][i] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == panelLength - 1) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isEndStem = true;
                    }
                }
            }
        }
        //�������ȼ�����
        return calcPriority(connectCount, isStartStem, isEndStem);
    }

    /**
     * �ж�ָ����chessArray[x][y]�������ȼ�
     *
     * @param x     �����±�
     * @param y     �����±�
     * @param chess ������ɫ
     * @return �õ����ȼ�����
     */
    private int getVerticalPriority(int x, int y, int chess) {
        //ָ������������
        int connectCount = 1;
        //����Ƿ񱻶�ס
        boolean isStartStem = false;
        //�ұ��Ƿ񱻶�ס
        boolean isEndStem = false;

        //�����ϱ߼���
        //�����̵ı�Եλ��,���ϱ߱�Ȼ����ס
        if (x == 0) {
            isStartStem = true;
        } else {
            //���ϱ���
            for (int i = x - 1; i >= 0; i--) {
                //�������ָ������
                if (chessArray[i][y] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isStartStem = chessArray[i][y] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == 0) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isStartStem = true;
                    }
                }
            }
        }

        //�����ұ߼���
        //�����ǰλ��y = panelLength,�������̵ı�Եλ��,���±߱�Ȼ����ס
        if (x == panelLength - 1) {
            isEndStem = true;
        } else {
            //���±���
            for (int i = x + 1; i < panelLength; i++) {
                //�������ָ������
                if (chessArray[i][y] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isEndStem = chessArray[i][y] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == panelLength - 1) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isEndStem = true;
                    }
                }
            }
        }
        //�������ȼ�����
        return calcPriority(connectCount, isStartStem, isEndStem);
    }

    /**
     * �ж�ָ����chessArray[x][y]��б�����ϵ����£����ȼ�
     *
     * @param x     �����±�
     * @param y     �����±�
     * @param chess ������ɫ
     * @return �õ����ȼ�����
     */
    private int getLeftSlashPriority(int x, int y, int chess) {
        //ָ������������
        int connectCount = 1;
        //����Ƿ񱻶�ס
        boolean isStartStem = false;
        //�ұ��Ƿ񱻶�ס
        boolean isEndStem = false;

        //�������ϼ���
        //�����̵ı�Եλ��,�����ϱ�Ȼ����ס
        if (x == 0 || y == 0) {
            isStartStem = true;
        } else {
            //�����ϱ���
            for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
                //�������ָ������
                if (chessArray[i][j] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isStartStem = chessArray[i][j] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == 0 || j == 0) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isStartStem = true;
                    }
                }
            }
        }

        //�������¼���
        //�����̵ı�Եλ��,�����±�Ȼ����ס
        if (x == panelLength - 1 || y == panelLength - 1) {
            isEndStem = true;
        } else {
            //��������
            for (int i = x + 1, j = y + 1; i < panelLength && j < panelLength; i++, j++) {
                //�������ָ������
                if (chessArray[i][j] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isEndStem = chessArray[i][j] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == panelLength - 1 || j == panelLength - 1) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isEndStem = true;
                    }
                }
            }
        }
        //�������ȼ�����
        return calcPriority(connectCount, isStartStem, isEndStem);
    }

    /**
     * �ж�ָ����chessArray[x][y]��б�����ϵ����£����ȼ�
     *
     * @param x     �����±�
     * @param y     �����±�
     * @param chess ������ɫ
     * @return �õ����ȼ�����
     */
    private int getRightSlashPriority(int x, int y, int chess) {
        //ָ������������
        int connectCount = 1;
        //����Ƿ񱻶�ס
        boolean isStartStem = false;
        //�ұ��Ƿ񱻶�ס
        boolean isEndStem = false;

        //�������ϼ���
        //�����̵ı�Եλ��,�����ϱ�Ȼ����ס
        if (x == panelLength - 1 || y == 0) {
            isStartStem = true;
        } else {
            //�����ϱ���
            for (int i = x + 1, j = y - 1; i < panelLength && j >= 0; i++, j--) {
                //�������ָ������
                if (chessArray[i][j] != chess) {
                    //�����Լ������ӣ�����߱���ס�����ǿ�λ
                    isStartStem = chessArray[i][j] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == panelLength - 1 || j == 0) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isStartStem = true;
                    }
                }
            }
        }

        //�������¼���
        //�����̵ı�Եλ��,�����±�Ȼ����ס
        if (x == 0 || y == panelLength - 1) {
            isEndStem = true;
        } else {
            //�����ұ�
            for (int i = x - 1, j = y + 1; i >= 0 && j < panelLength; i--, j++) {
                //�������ָ������
                if (chessArray[i][j] != chess) {
                    //�����Լ������ӣ��򱻶�ס�����ǿ�λ
                    isEndStem = chessArray[i][j] != AiWuziqiPanel.NO_CHESS;
                    break;
                } else {
                    connectCount++;
                    if (i == 0 || j == panelLength - 1) {
                        //�ڱ�Եλ�ã��򱻵�ס
                        isEndStem = true;
                    }
                }
            }
        }
        //�������ȼ�����
        return calcPriority(connectCount, isStartStem, isEndStem);
    }


    /**
     * �����������Լ���ʼ�����Ƿ񱻶�ס�������ȼ�����
     *
     * @param connectCount ������
     * @param isStartStem  ��ʼ�Ƿ񱻶�ס
     * @param isEndStem    �����Ƿ񱻶�ס
     * @return ���ȼ�����
     */
    private int calcPriority(int connectCount, boolean isStartStem, boolean isEndStem) {
        //���ȼ�����
        int priority = 0;
        if (connectCount >= 5) {
            //�ܹ�����
            priority = FIVE;
        } else {
            //��������
            if (isStartStem && isEndStem) {
                //��ʼ����������ס,����
                priority = DEAD;
            } else if (isStartStem == isEndStem) {
                //���߶�û����ס
                if (connectCount == 4) {
                    priority = LIVE_FOUR;
                } else if (connectCount == 3) {
                    priority = LIVE_THREE;
                } else if (connectCount == 2) {
                    priority = LIVE_TWO;
                } else if (connectCount == 1) {
                    priority = LIVE_ONE;
                }
            } else {
                //����һ�߱���ס
                if (connectCount == 4) {
                    priority = DEAD_FOUR;
                } else if (connectCount == 3) {
                    priority = DEAD_THREE;
                } else if (connectCount == 2) {
                    priority = DEAD_TWO;
                } else if (connectCount == 1) {
                    priority = DEAD_ONE;
                }
            }
        }
        return priority;
    }

}
