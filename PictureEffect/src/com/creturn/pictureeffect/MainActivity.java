package com.creturn.pictureeffect;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	final int RESULT_LOAD_IMAGE = 1;
	final String LOG_NAME = "main_activity";
	private Button btn_pt;
	private Button btn_info;
	private ImageView imgView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		initEvent();
		
	}

	private void initUI() {
		btn_pt = (Button) findViewById(R.id.pt_pic);
		btn_info = (Button) findViewById(R.id.btn_info);
		imgView = (ImageView) findViewById(R.id.imageView1);
	}

	private void initEvent() {
		btn_pt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//¥”œ‡≤·º”‘ÿÕº∆¨
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
				startActivityForResult(i, RESULT_LOAD_IMAGE); 
				
			}
		});
		
		btn_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Log.i(LOG_NAME, "suceess load");
			
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picPath = cursor.getString(columnIndex);
			cursor.close();
			Log.i(LOG_NAME, picPath);
			imgView.setImageBitmap(BitmapFactory.decodeFile(picPath));
			
		}
	}
	
	
}
