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
	// ����װ������Դ���б�
	private List<ImageBucket> dataList;
	// ��ͼ����
	private GridView gridView;
	// �Զ����������
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
	 * ��ʼ������
	 */
	private void initData() {		
		dataList = helper.getImagesBucketList(false);	
//		bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic_unfocused);
	}

	/**
	 *��ʼ��view��ͼ
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
				 * ����position���������Ի�ø�GridView����View��󶨵�ʵ���࣬Ȼ���������isSelected״̬��
				 * ���ж��Ƿ���ʾѡ��Ч���� ����ѡ��Ч���Ĺ��������������Ĵ����л���˵��
				 */
//				 if(dataList.get(position).isSelected()){
//				 dataList.get(position).setSelected(false);
//				 }else{
//				 dataList.get(position).setSelected(true);
//				 }
				
				//��ȡ��ǰ�ļ��е�����
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
		 * �����¼�
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
