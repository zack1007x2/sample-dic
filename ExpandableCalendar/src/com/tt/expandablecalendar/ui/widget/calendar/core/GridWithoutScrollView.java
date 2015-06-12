package com.tt.expandablecalendar.ui.widget.calendar.core;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 不帶滾動屬性的gridview <功能簡述> <Br>
 * <功能詳細描述> <Br>
 * 
 * @author kysonX
 */
public class GridWithoutScrollView extends GridView {
    public GridWithoutScrollView(Context context) {
        super(context);
    }

    public GridWithoutScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
