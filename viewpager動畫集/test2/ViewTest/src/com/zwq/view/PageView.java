package com.zwq.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class PageView extends ViewPager {

	private float mTrans;
	private float mScale;
	private static final float SCALE_MAX = 0.5f;
	Map<Integer, View> mViewMap = new HashMap<Integer, View>();

	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPositionView(int key, View value) {
		mViewMap.put(key, value);
	}

	public View getPositionView(int postion) {
		return mViewMap.get(postion);
	}

	View leftView = null;
	View rightView = null;

	@Override
	protected void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
//		leftView = mViewMap.get(position);
//		rightView = mViewMap.get(position + 1);
//		dealAnimate(leftView, rightView, positionOffset, positionOffsetPixels);
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	private void dealAnimate(View left, View right, float effectOffset,
			int positionOffsetPixels) {
		if (right != null) {
			/**
			 * 縮小比例 如果手指從右到左的滑動（切換到後一個）：0.0~1.0，即從一半到最大
			 * 如果手指從左到右的滑動（切換到前一個）：1.0~0，即從最大到一半
			 */
			mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;
			/**
			 * x偏移量： 如果手指從右到左的滑動（切換到後一個）：0-720 如果手指從左到右的滑動（切換到前一個）：720-0
			 */
			mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
			ViewHelper.setScaleX(right, mScale);
			ViewHelper.setScaleY(right, mScale);
			ViewHelper.setTranslationX(right, mTrans);
		}
		if (left != null) {
			left.bringToFront();
		}
	}
}