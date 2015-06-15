package com.example.pictest;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class TestPicActivity extends Activity {
	// 用来装载数据源的列表
	private List<ImageBucket> dataList;
	// 视图容器
	private GridView gridView;
	// 自定义的适配器
	private ImageBucketAdapter adapter;
	private AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bitmap;
	private Button mBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testpic);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initData();
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {		
		dataList = helper.getImagesBucketList(false);	
//		bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic_unfocused);
	}

	/**
	 *初始化view视图
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(TestPicActivity.this, dataList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
				 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
				 */
//				 if(dataList.get(position).isSelected()){
//				 dataList.get(position).setSelected(false);
//				 }else{
//				 dataList.get(position).setSelected(true);
//				 }
				
				//获取当前文件夹的名字
				TextView mTextView = (TextView) view.findViewById(R.id.name);
				String str = mTextView.getText().toString();
				Intent intent = new Intent(TestPicActivity.this, ImageGridActivity.class);
				intent.putExtra(TestPicActivity.EXTRA_IMAGE_LIST, (Serializable) dataList.get(position).imageList);
				intent.putExtra("TestPic", str);
				startActivity(intent);
				finish();
			}

		});
		/**
		 * 返回事件
		 */
		mBack = (Button) findViewById(R.id.test_pic_back);
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
