package com.creturn.pictureeffect;

import com.edmodo.cropper.CropImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class CropActivity extends Activity {
	private Button btn_return;
	public CropImageView cropImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
	    WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_crop);
		initUI();
		initEvent();
	}

	private void initUI() {
		btn_return = (Button) findViewById(R.id.btn_return);
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		Bitmap bitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/crop_tmp_img.png");
		cropImageView.setImageBitmap(bitmap);
	}

	private void initEvent() {
		//返回主界面
		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}
