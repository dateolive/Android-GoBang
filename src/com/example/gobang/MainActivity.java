package com.example.gobang;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button btn1,btn2,btn3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn1 = (Button)findViewById(R.id.button1);		//����xml�е�button
		btn2 = (Button)findViewById(R.id.button2);	
		btn3 = (Button)findViewById(R.id.button3);	
		TextView textView = (TextView)findViewById(R.id.textView1);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/chunfen.ttf");
        textView.setTypeface(typeface);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ChessPanel.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AiChessPanel.class);
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	showDialog();
            }
        });
        
	}													
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	 private void showDialog(){
	        AlertDialog.Builder builder=new AlertDialog.Builder(this);
	        builder.setIcon(R.drawable.webicon);
	        builder.setTitle("��Ϸ����");
	        builder.setMessage("˫���ֱ�ʹ�úڰ���ɫ�����ӣ���������ֱ������ߵĽ�����ϣ����γ����������߻�ʤ��");
	        builder.setPositiveButton("��֪����",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	 
	                    }
	                });
	        AlertDialog dialog=builder.create();
	        dialog.show();
	 
	        }


}
