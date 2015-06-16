package com.zwq.view.effect;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class DepthPageTransformer implements PageTransformer {
	private static float MIN_SCALE = 0.5f;

	/**
	 * position參數指明給定頁面相對於屏幕中心的位置。它是一個動態屬性，會隨著頁面的滾動而改變。當一個頁面填充整個屏幕是，它的值是0，
	 * 當一個頁面剛剛離開屏幕的右邊時
	 * ，它的值是1。當兩個也頁面分別滾動到一半時，其中一個頁面的位置是-0.5，另一個頁面的位置是0.5。基于屏幕上頁面的位置
	 * ，通過使用諸如setAlpha()、setTranslationX()、或setScaleY()方法來設置頁面的屬性，來創建自定義的滑動動畫。
	 */
	@Override
	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		if (position < -1) { // [-Infinity,-1)
								// This page is way off-screen to the left.
			view.setAlpha(0);
			view.setTranslationX(0);
		} else if (position <= 0) { // [-1,0]
									// Use the default slide transition when
									// moving to the left page
			view.setAlpha(1);
			view.setTranslationX(0);
			view.setScaleX(1);
			view.setScaleY(1);
		} else if (position <= 1) { // (0,1]
									// Fade the page out.
			view.setAlpha(1 - position);
			// Counteract the default slide transition
			view.setTranslationX(pageWidth * -position);
			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - Math.abs(position));
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
		} else { // (1,+Infinity]
					// This page is way off-screen to the right.
			view.setAlpha(0);
			view.setScaleX(1);
			view.setScaleY(1);
		}
	}

}