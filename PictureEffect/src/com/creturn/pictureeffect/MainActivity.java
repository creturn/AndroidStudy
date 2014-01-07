package com.creturn.pictureeffect;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	//相册调用
	final int RESULT_LOAD_IMAGE = 1;
	//相机调用
	final int CAMERA_RESULT = 2;
	final String LOG_NAME = "main_activity";
	private Button btn_pt;
	private Button btn_info;
	private Button btn_cm;
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
		btn_cm = (Button) findViewById(R.id.cm_pic);
		imgView = (ImageView) findViewById(R.id.imageView1);
	}

	private void initEvent() {
		//调用相册
		btn_pt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//从相册加载图片
//				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				//解决调用图片过小问题
				intent.setType("image/*");  
				startActivityForResult(intent, RESULT_LOAD_IMAGE); 
				
			}
		});
		//调用相机
		btn_cm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				 //这里获取大图原理，先把相机拍的照片存到固定目录，然后读取时候从保存路径读取即可，不用直接通过内存传输照片
				 //通过intent传递尺寸会被压缩
//				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile(F.SD_CARD_TEMP_PHOTO_PATH)));
				 startActivityForResult(intent, CAMERA_RESULT);
			}
		});
		//信息按钮
		btn_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i(LOG_NAME,"info click" + Environment.getExternalStorageDirectory().getPath());
				
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//相册反馈
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Log.i(LOG_NAME, "suceess load");
			
			Uri selectedImage = data.getData();
			Log.i(LOG_NAME, selectedImage.getPath());
			/**
			 * 这里的代码获取到的是小图
			 * 由于业务需求需要获取原始大图
			 * 
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picPath = cursor.getString(columnIndex);
			cursor.close();
			imgView.setImageBitmap(BitmapFactory.decodeFile(picPath));
			*/
			
			try {
				Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
				Log.i(LOG_NAME,"pic width:" + picture.getWidth() + "Heigth:" + picture.getHeight());
				Bitmap newPic = ScalePic(picture, picture.getWidth()/2, picture.getHeight()/2);
				Log.i(LOG_NAME, "New Pic widh:" + newPic.getWidth() + "height:" + newPic.getHeight());
				picture.recycle();
				imgView.setImageBitmap(newPic);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		if(requestCode == CAMERA_RESULT && resultCode == RESULT_OK && null != data){
			Bundle extras = data.getExtras();
			Bitmap bmpBitmap=(Bitmap)extras.get("data");
			imgView.setImageBitmap(bmpBitmap);
			
			
			//可以保存到相册中
			String url = MediaStore.Images.Media.insertImage(getContentResolver(), bmpBitmap, "", "");
			Log.i(LOG_NAME, "Image Save Path:" + url);
			//检测sdk可用不，可用就写入图片到sdk
			
		}
	}
	
	public Bitmap ScalePic(Bitmap bmp, float ScaleWidth, float ScaleHeight){
		Matrix matrix = new Matrix();
		matrix.postScale(ScaleWidth/bmp.getWidth(), ScaleHeight/bmp.getHeight());
		return Bitmap.createBitmap(bmp,0,0,bmp.getWidth(), bmp.getHeight(), matrix, true);
	}
}
