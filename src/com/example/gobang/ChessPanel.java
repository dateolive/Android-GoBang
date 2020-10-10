package com.example.gobang;

import com.example.gobang.view.WuziqiPanel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChessPanel extends Activity {
	private AlertDialog.Builder alertBuilder;
	private AlertDialog alertDialog;
	private WuziqiPanel wuziqiPanel;
	private Button bt_Return;
	private Button bt_ReStart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess_panel);
		bt_Return = (Button) findViewById(R.id.id_bt_return);
		bt_ReStart = (Button) findViewById(R.id.id_bt_restart);
		//��Ϸ����ʱ�����Ի���
        alertBuilder = new AlertDialog.Builder(ChessPanel.this);
        alertBuilder.setPositiveButton("����һ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wuziqiPanel.restartGame();
            }
        });
        alertBuilder.setNegativeButton("ȡ����Ϸ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChessPanel.this.finish();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle("��Ϸ����");
        wuziqiPanel = (WuziqiPanel) findViewById(R.id.id_wuziqi_panel);
        wuziqiPanel.setOnGameOverListener(new WuziqiPanel.OnGameOverListener() {
            @Override
            public void gameOver(int Winresult) {
                //����ʤ�����
            	switch(Winresult){
            		case WuziqiPanel.White_Win:
            			alertBuilder.setMessage("����ʤ��");
            			break;
            		case WuziqiPanel.Black_Win:
            			alertBuilder.setMessage("����ʤ��");
            			break;
            		case WuziqiPanel.No_Win:
            			alertBuilder.setMessage("����");
            			break;
            		default:
            			break;
            	}
            	alertDialog=alertBuilder.create();
            	alertDialog.show();
            	
            }
        });
        initEvents();
	}
	 private void initEvents() {
		 	bt_ReStart.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                //����
	            	wuziqiPanel.restartGame();
	            }
	        });
	        bt_Return.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                //����
	            	wuziqiPanel.Return();
	            }
	        });
	    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chess_panel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
