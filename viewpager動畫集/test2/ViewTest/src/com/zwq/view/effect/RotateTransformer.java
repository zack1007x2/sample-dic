package com.zwq.view.effect;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class RotateTransformer implements PageTransformer {

	/**
	 * page當然值得就是滑動中德那個view，position這裡是float，不是平時理解的int位置信息，而是當前滑動狀態的一個表示，
	 * 比如當滑動到正全屏時
	 * ，position是0，而向左滑動，使得右邊剛好有一部被進入屏幕時，position是1，如果前一夜和下一頁基本各在屏幕占一半時
	 * ，前一頁的position是-0.5，後一頁的posiotn是0.5，所以根據position的值我們就可以自行設置需要的alpha，x/y信息。
	 */
	@Override
	public void transformPage(View view, float position) {
		if (position < -1) {
		} else if (position <= 0) {
			ViewHelper.setScaleX(view, 1 + position);
			ViewHelper.setScaleY(view, 1 + position);
			ViewHelper.setRotation(view, 360 * position);
		} else if (position <= 1) {
			ViewHelper.setScaleX(view, 1 - position);
			ViewHelper.setScaleY(view, 1 - position);
			ViewHelper.setRotation(view, 360 * position);
		} else {
		}
	}

}
