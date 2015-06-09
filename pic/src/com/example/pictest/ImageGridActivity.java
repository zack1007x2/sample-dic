package com.example.pictest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.example.pictest.ImageGridAdapter.TextCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
/**
 * 鍒濆鍖栫晫闈㈠強鎺т歡
 * @author talos001
 *
 */
public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	//鐢ㄦ潵瑁呰澆鏁版嵁婧愮殑鍒楄〃
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;//  鑷畾涔夌殑閫傞厤鍣
	AlbumHelper helper;
	Button bt,mBack;
	private TextView mTitle;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;

			default:
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image_grid);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		initView();

		//鎺ユ敹浠嶵estPicActivity浼犻掕繃鏉ョ殑鏁版嵁
		Intent intent = getIntent();		
		String title = intent.getStringExtra("TestPic");
		mTitle.setText(title);
	}

	/**
	 * 鍒濆鍖杤iew瑙嗗浘
	 */
	private void initView() {
		mTitle = (TextView) findViewById(R.id.title);
		gridView = (GridView) findViewById(R.id.gridview);
		mBack = (Button) findViewById(R.id.image_grid_back);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList, mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				
			}
		});
        /**
         * 鍒楄〃欏圭偣鍑諱簨浠
         */
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//鑾峰彇鍥劇墖璺緞
				String path = dataList.get(position).imagePath;
				//璺寵漿
				Intent intent = new Intent(ImageGridActivity.this, CutPicActivity.class);
				//鎼哄甫璺緞淇℃伅
				intent.putExtra("bitmappath",path);	
				startActivity(intent);
				finish();
			}

		});
		/**
		 * 榪斿洖浜嬩歡
		 */
        mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ImageGridActivity.this, TestPicActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
