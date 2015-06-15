package com.example.pictest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CutPicActivity extends Activity {
	private CirclView mCirclView ;
	private Button mComplete;
	//��Ƭ�洢·��
	private String filepath = "/sdcard/myheader";
	private String filepathimg="";
	private String picname="newpic";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cutpic);
		initView();
    	addListener();
    	new Handler().postDelayed(new Runnable(){   
    	    public void run() {   
    	    //execute the task   
    	    	//��ȡͼƬ·��
    			String bitmapPath = (String) getIntent().getExtras().get("bitmappath");
    			//��ͼƬ·��������ͼƬ
    			Bitmap bm = BitmapFactory.decodeFile(bitmapPath);   			
    			//��ȡͼƬ����ת�Ƕȣ�������Щ�ֻ����պ�����Ƭ��ת��
    			int degree = readPictureDegree(bitmapPath);  
    			//��ͼƬ�ŵ����õĲü�����
    			mCirclView.setBitmap(bm,degree);
    	    }   
    	 }, 30);//30�������ڵȴ�����ͼƬ��Ҫ�Գ�ʱ������Լ�����
    }
	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * @param path ͼƬ����·��
	 * @return degree��ת�ĽǶ�
	 */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
    /**
     * �ؼ��ļ����¼�
     */
	private void addListener() {
		
		mComplete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bm= mCirclView.getClipImage();
				if(null != bm){
					storeImageToSDCARD(bm, picname, filepath);
				}
				finish();
			}
		});
	}
	/**
	 * storeImageToSDCARD ��bitmap��ŵ�sdcard��
	 * */
	 public void storeImageToSDCARD(Bitmap colorImage, String ImageName, String path) {
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
	/**
	 * ʵ�����ؼ�
	 */
	private void initView() {
		mCirclView = (CirclView) findViewById(R.id.circl);		
		mComplete = (Button) findViewById(R.id.cut_pic_btn);		
	}
}
