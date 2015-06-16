package com.zwq.view.effect;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class CubeTransformer implements PageTransformer {
	
	/**
	 * position參數指明給定頁面相對於屏幕中心的位置。它是一個動態屬性，會隨著頁面的滾動而改變。當一個頁面填充整個屏幕是，它的值是0，
	 * 當一個頁面剛剛離開屏幕的右邊時，它的值是1。當兩個也頁面分別滾動到一半時，其中一個頁面的位置是-0.5，另一個頁面的位置是0.5。基于屏幕上頁面的位置
	 * ，通過使用諸如setAlpha()、setTranslationX()、或setScaleY()方法來設置頁面的屬性，來創建自定義的滑動動畫。
	 */
	@Override
	public void transformPage(View view, float position) {
		if (position <= 0) {
			//從右向左滑動為當前View
			
			//設置旋轉中心點；
			ViewHelper.setPivotX(view, view.getMeasuredWidth());
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			
			//只在Y軸做旋轉操作
			ViewHelper.setRotationY(view, 90f * position);
		} else if (position <= 1) {
			//從左向右滑動為當前View
			ViewHelper.setPivotX(view, 0);
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			ViewHelper.setRotationY(view, 90f * position);
		}
	}
}
