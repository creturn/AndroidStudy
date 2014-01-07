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
	//������
	final int RESULT_LOAD_IMAGE = 1;
	//�������
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
		//�������
		btn_pt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//��������ͼƬ
//				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				//�������ͼƬ��С����
				intent.setType("image/*");  
				startActivityForResult(intent, RESULT_LOAD_IMAGE); 
				
			}
		});
		//�������
		btn_cm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				 //�����ȡ��ͼԭ���Ȱ�����ĵ���Ƭ�浽�̶�Ŀ¼��Ȼ���ȡʱ��ӱ���·����ȡ���ɣ�����ֱ��ͨ���ڴ洫����Ƭ
				 //ͨ��intent���ݳߴ�ᱻѹ��
//				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile(F.SD_CARD_TEMP_PHOTO_PATH)));
				 startActivityForResult(intent, CAMERA_RESULT);
			}
		});
		//��Ϣ��ť
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
		//��ᷴ��
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Log.i(LOG_NAME, "suceess load");
			
			Uri selectedImage = data.getData();
			Log.i(LOG_NAME, selectedImage.getPath());
			/**
			 * ����Ĵ����ȡ������Сͼ
			 * ����ҵ��������Ҫ��ȡԭʼ��ͼ
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
			
			
			//���Ա��浽�����
			String url = MediaStore.Images.Media.insertImage(getContentResolver(), bmpBitmap, "", "");
			Log.i(LOG_NAME, "Image Save Path:" + url);
			//���sdk���ò������þ�д��ͼƬ��sdk
			
		}
	}
	
	public Bitmap ScalePic(Bitmap bmp, float ScaleWidth, float ScaleHeight){
		Matrix matrix = new Matrix();
		matrix.postScale(ScaleWidth/bmp.getWidth(), ScaleHeight/bmp.getHeight());
		return Bitmap.createBitmap(bmp,0,0,bmp.getWidth(), bmp.getHeight(), matrix, true);
	}
}
