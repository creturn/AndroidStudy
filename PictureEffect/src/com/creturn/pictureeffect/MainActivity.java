package com.creturn.pictureeffect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"img_tmp.jpg")));
				 startActivityForResult(intent, CAMERA_RESULT);
			}
		});
		//���Ŵ���
		btn_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i(LOG_NAME,"old remember" + Environment.getExternalStorageDirectory().getPath());
				Bitmap newpic = oldRemeber(((BitmapDrawable)imgView.getDrawable()).getBitmap());
				imgView.setImageBitmap(newpic);
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
		
		if(requestCode == CAMERA_RESULT && resultCode == RESULT_OK){
			/**
			 * ���������һ��Ĭ�ϵ���������ӱ���λ��
			 * ���ص��Ǿ���ѹ����ͼƬ
			 * 
			Bundle extras = data.getExtras();
			Bitmap bmpBitmap=(Bitmap)extras.get("data");
			imgView.setImageBitmap(bmpBitmap);
			*/
			Bitmap bitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/img_tmp.jpg");
			
			//���浽�����
			MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
			Bitmap nBitmap = ScalePic(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2);
			bitmap.recycle();
			imgView.setImageBitmap(nBitmap);
//			Log.i(LOG_NAME, "Image Save Path:" + url);
			//���sdk���ò������þ�д��ͼƬ��sdk
			
		}
	}
	
	/**
	 * ����Ч��(���֮ǰ�����Ż���һ��)
	 * @param bmp
	 * @return
	 */
	public Bitmap oldRemeber(Bitmap bmp)
	{
		// �ٶȲ���
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++)
		{
			for (int k = 0; k < width; k++)
			{
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}
		
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time="+(end - start));
		return bitmap;
	}
	/**
	 * ��СͼƬ	
	 * @param bmp
	 * @param ScaleWidth
	 * @param ScaleHeight
	 * @return
	 */
	public Bitmap ScalePic(Bitmap bmp, float ScaleWidth, float ScaleHeight){
		Matrix matrix = new Matrix();
		matrix.postScale(ScaleWidth/bmp.getWidth(), ScaleHeight/bmp.getHeight());
		return Bitmap.createBitmap(bmp,0,0,bmp.getWidth(), bmp.getHeight(), matrix, true);
	}
}
