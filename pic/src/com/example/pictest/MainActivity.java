package com.example.pictest;

import java.io.File;
import java.io.FileOutputStream;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends Activity implements OnClickListener{
//	private RelativeLayout rlSc,rlNhxx,rlJkda,rlYjfk;
	private ImageView ivHead;
	public  Drawable drawable;
	//保存的檔案的路徑
	private String filepath = "/sdcard/myheader";
	private String filepathimg="";
	private String picname="newpic";
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_CUT = 2;// 結果
	private static final int PHOTO_REQUEST_ALBUM = 3;// 相冊
	 

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_activity);
		ivHead=(ImageView)this.findViewById(R.id.ivHead);
		filepathimg = filepath + "/" + picname + ".jpg";
		File f = new File(filepathimg);
		if (f.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(filepathimg);
			ivHead.setImageBitmap(Util.toRoundBitmap(bm));		
		}        
		ivHead.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v==ivHead){//點擊更換頭像按鈕
			ShowPickDialog();
		}
		
	}
	
	/**
	 * 選擇提示對話框
	 */
	private void ShowPickDialog() {
		new AlertDialog.Builder(this)
				.setTitle("設置頭像...")
				.setNegativeButton("相冊", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(MainActivity.this, TestPicActivity.class);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
						startActivityForResult(intent,PHOTO_REQUEST_ALBUM);

					}
				})
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						/**
						 * 下面這句還是老樣子，調用快速拍照功能，至于為什麼叫快速拍照，大家可以參考如下官方
						 * 文檔，you_sdk_path/docs/guide/topics/media/camera.html
						 */
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						//下面這句指定調用相機拍照後的照片存儲的路徑
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										"newpic.jpg")));
						startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_CAMERA:// 當選擇拍照時調用
        	String path = Environment.getExternalStorageDirectory()
					+ "/newpic.jpg";
			Intent intent = new Intent(MainActivity.this, CutPicActivity.class);
			intent.putExtra("bitmappath",path);
			startActivityForResult(intent, PHOTO_REQUEST_ALBUM);
            break;
        case PHOTO_REQUEST_CUT:// 返回的結果
            if (data != null)               
                sentPicToNext(data);
            break; 
        case PHOTO_REQUEST_ALBUM://相冊選擇圖片
        	filepathimg = filepath + "/" + picname + ".jpg";
    		File f = new File(filepathimg);
    		
    		if (f.exists()) {
    			Bitmap bm = BitmapFactory.decodeFile(filepathimg);
    			if(bm == null){
    				f.delete();
//    				mHeader.setImageResource(R.drawable.mq_ui_head_inside);
    			}else{				
    				ivHead.setImageBitmap(Util.toRoundBitmap(bm));
    			}
    		} else {
    			ivHead.setImageResource(R.drawable.ic_launcher);
    		}
        	break; 
		}    
	    super.onActivityResult(requestCode, resultCode, data);
	}
	

	
	/**
	 * 保存裁剪之後的圖片數據
	 * @param picdata
	 */
	@SuppressWarnings("deprecation")
	private void sentPicToNext(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");//裁剪後的圖片
			ivHead.setImageBitmap(photo);
			storeImageToSDCARD(photo, picname, filepath);
		}
	}

	/**
	 * storeImageToSDCARD 將bitmap存放到sdcard中
	 * */
	 public void storeImageToSDCARD(Bitmap colorImage, String ImageName,String path) {
		File file = new File(path);
		if (!file.exists()) {
		    file.mkdir();
		}
		File imagefile = new File(file, ImageName + ".jpg");
			try {
			    imagefile.createNewFile();
			    FileOutputStream fos = new FileOutputStream(imagefile);
			    colorImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			    fos.flush();
			    fos.close();
			} catch (Exception e) {
			    e.printStackTrace();
			}
	 }

}
