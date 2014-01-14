package com.creturn.pictureeffect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MainActivity extends Activity {
	//相册调用
	final int RESULT_LOAD_IMAGE = 1;
	//相机调用
	final int CAMERA_RESULT = 2;
	final int UPLOAD_IMG = 3;
	final String UPLOAD_URL = "http://222.73.234.196/up.php";
	final String LOG_NAME = "main_activity";
	private Button btn_pt;
	private Button btn_info;
	private Button btn_cm;
	private Button btn_crop;
	private Button btn_up;
	private ImageView imgView;
	private TextView txt_up_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
	    WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_main);
		initUI();
		initEvent();
	}

	private void initUI() {
		btn_pt = (Button) findViewById(R.id.pt_pic);
		btn_info = (Button) findViewById(R.id.btn_info);
		btn_cm = (Button) findViewById(R.id.cm_pic);
		btn_up = (Button) findViewById(R.id.btn_upload);
		btn_crop = (Button) findViewById(R.id.btn_crop);
		imgView = (ImageView) findViewById(R.id.imageView1);
		txt_up_progress = (TextView) findViewById(R.id.txt_upload_porgress);
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
				 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"img_tmp.jpg")));
				 startActivityForResult(intent, CAMERA_RESULT);
			}
		});
		//复古处理
		btn_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i(LOG_NAME,"old remember" + Environment.getExternalStorageDirectory().getPath());
				Bitmap newpic = ImageUtils.oldRemeber(((BitmapDrawable)imgView.getDrawable()).getBitmap());
				imgView.setImageBitmap(newpic);
			}
		});
		//处理裁剪
		btn_crop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CropActivity.class);
//				intent.putExtra("BitmapImage", ((BitmapDrawable)imgView.getDrawable()).getBitmap());
				ImageUtils.saveBitmap(((BitmapDrawable)imgView.getDrawable()).getBitmap(), "crop_crop_tmp_img.png");
				
				startActivity(intent);
			}
		});
		
		//上传处理
		btn_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//从相册加载图片
//				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				//解决调用图片过小问题
				intent.setType("image/*");  
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"up_img.jpg")));
				startActivityForResult(intent, UPLOAD_IMG);
				
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
				Bitmap newPic = ImageUtils.ScalePic(picture, picture.getWidth()/2, picture.getHeight()/2);
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
			 * 和上面调用一样默认调用相机不加保存位置
			 * 返回的是经过压缩的图片
			 * 
			Bundle extras = data.getExtras();
			Bitmap bmpBitmap=(Bitmap)extras.get("data");
			imgView.setImageBitmap(bmpBitmap);
			*/
			Bitmap bitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/img_tmp.jpg");
			
			//保存到相册中
			MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
			Bitmap nBitmap = ImageUtils.ScalePic(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2);
			bitmap.recycle();
			imgView.setImageBitmap(nBitmap);
		}
		//处理上传
		if(requestCode == UPLOAD_IMG && resultCode == RESULT_OK){
			Uri selectedImage = data.getData();
			
			Log.i(LOG_NAME, selectedImage.getPath());
			Log.i(LOG_NAME, "RealPath:" + ImageUtils.getRealPathFromURI(getApplicationContext(), selectedImage));
			txt_up_progress.setText("开始上传");
			File file = new File(ImageUtils.getRealPathFromURI(getApplicationContext(), selectedImage));
			RequestParams params = new RequestParams();
			params.addBodyParameter("file", file);
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.send(HttpRequest.HttpMethod.POST, UPLOAD_URL, params, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String msg) {
					alert(msg);
				}
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					if (isUploading) {
						Log.i(LOG_NAME, "upload:" + current + "/" + total);
						txt_up_progress.setText("Size:" + current + "/" + total);
					}
				}
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					alert(responseInfo.result);
					Log.i(LOG_NAME, responseInfo.result);
				}
			});
		}
	}
	 
	public void alert(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}
