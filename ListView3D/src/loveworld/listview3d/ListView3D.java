package loveworld.listview3d;

import java.util.LinkedList;

import loveworld.listview3d.util.Dynamics;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;

public class ListView3D extends AdapterView<Adapter> {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// 新添加的所有子視圖在當前最當前最後一個子視圖後添加的布局模型
	private static final int LAYOUT_MODE_BELOW = 0;
	// 與LAYOUT_MODE_BELOW相反方向添加的布局模型
    private static final int LAYOUT_MODE_ABOVE = 1;
	
    
    // 初始模式，用戶還未接觸ListView
    private static final int TOUCH_MODE_REST = -1;
    // 觸摸Down事件模式
    private static final int TOUCH_MODE_DOWN = 0;
    // 滾動模式
    private static final int TOUCH_MODE_SCROLL = 1;
    
    private static final int INVALID_INDEX = -1;
    
    
    
    /** 3D 效果 **/
    // Item與List的寬度比
    private static final float ITEM_WIDTH = 0.85f;
    // Item高度因為需要預留給滾動空間，填充後的高度與Item實際高度的比例
    private static final float ITEM_VERTICAL_SPACE = 1.45f;
    private static final int AMBIENT_LIGHT = 55;
    private static final int DIFFUSE_LIGHT = 200;
    private static final float SPECULAR_LIGHT = 70;
    private static final float SHININESS = 200;
    private static final int MAX_INTENSITY = 0xFF;
    private static final float SCALE_DOWN_FACTOR = 0.15f;
    private static final int DEGREES_PER_SCREEN = 270;
    
    
    /** Fling 與 阻尼效果 **/
    private static final int PIXELS_PER_SECOND = 1000;
    private static final float POSITION_TOLERANCE = 0.4f;
    private static final float VELOCITY_TOLERANCE = 0.5f;
    private static final float WAVELENGTH = 0.9f;
    private static final float AMPLITUDE = 0.0f;
    
    
    
	// ===========================================================
	// Fields
	// ===========================================================

	// 視圖和數據適配
	private Adapter mAdapter;
	// 當前顯示最後一個Item在Adapter中位置
	private int mLastItemPosition = -1;
	// 當前顯示第一個Item在Adapter中位置
	private int mFirstItemPosition;
	
	
	// 當前頂部第一個item
	private int mListTop;
	// 當前第一個顯示的item與底部第一個item的頂部偏移量
	private int mListTopOffset;
	// 觸摸Down事件時進行記錄 
	private int mListTopStart;
	
	// 記錄ListView當前處於哪種模式
	private int mTouchMode = TOUCH_MODE_REST;

	// 記錄上一次觸摸X軸
	private int mTouchStartX;
	// 記錄上一次觸摸Y軸
	private int mTouchStartY;
	// 僅記錄Down事件時Y軸值 
	private int mMotionY;
	
	// 觸發滾動的最小移動距離
	private int mTouchSlop;
	
	// 可反複使用的Rect
	private Rect mRect;
	
	// 用於檢測是手長按動作
	private Runnable mLongPressRunnable;
	
	// View複用當前僅支持一種類型Item視圖複用
	// 想更多了解ListView視圖如何複用可以看AbsListView內部類RecycleBin
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();
	
	
	
	/** 3D 效果 **/
    private int mListRotation;
    private Camera mCamera;
    private Matrix mMatrix;
    private Paint mPaint;
    private boolean mRotationEnabled = true;
    private boolean mLightEnabled = true;
	
    
    
    /** Fling 與 阻尼效果 **/
    private VelocityTracker mVelocityTracker;
    private Dynamics mDynamics;
    private Runnable mDynamicsRunnable;
    private int mLastSnapPos = Integer.MIN_VALUE;
    
	// ===========================================================
	// Constructors
	// ===========================================================

	public ListView3D(Context context) {
		super(context);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs) {
		super(context, attrs);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initListView(context);
	}
	
	// ===========================================================
	// Setter
	// ===========================================================

    public void setDynamics(final Dynamics dynamics) {
        if (mDynamics != null) {
            dynamics.setState(mDynamics.getPosition(), mDynamics.getVelocity(), AnimationUtils
                    .currentAnimationTimeMillis());
        }
        mDynamics = dynamics;
        if (!mRotationEnabled) {
            mDynamics.setMaxPosition(0);
        }
    }
	
    
    private void setSnapPoint() {
        if (mRotationEnabled) {
            final int rotation = mListRotation % 90;
            int snapPosition = 0;

            if (rotation < 45) {
                snapPosition = (-(mListRotation - rotation) * getHeight()) / DEGREES_PER_SCREEN;
            } else {
                snapPosition = (-(mListRotation + 90 - rotation) * getHeight())
                        / DEGREES_PER_SCREEN;
            }

            if (mLastSnapPos == Integer.MIN_VALUE && mLastItemPosition == mAdapter.getCount() - 1
                    && getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
                mLastSnapPos = snapPosition;
            }

            if (snapPosition > 0) {
                snapPosition = 0;
            } else if (snapPosition < mLastSnapPos) {
                snapPosition = mLastSnapPos;
            }
            mDynamics.setMaxPosition(snapPosition);
            mDynamics.setMinPosition(snapPosition);

        } else {
            if (mLastSnapPos == Integer.MIN_VALUE && mLastItemPosition == mAdapter.getCount() - 1
                    && getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
                mLastSnapPos = mListTop;
                mDynamics.setMinPosition(mLastSnapPos);
            }
        }
    }
    
	// ===========================================================
	// Getter
	// ===========================================================
	
	private int getChildMargin(View child) {
	    return (int)(child.getMeasuredHeight() * (ITEM_VERTICAL_SPACE - 1) / 2);
	}
	
	private int getChildTop(View child) {
	    return child.getTop() - getChildMargin(child);
	}
	
	private int getChildBottom(View child) {
	    return child.getBottom() + getChildMargin(child);
	}
	
	private int getChildHeight(View child) {
	    return child.getMeasuredHeight() + 2 * getChildMargin(child);
	}

	
    public void enableRotation(final boolean enable) {
        mRotationEnabled = enable;
        mDynamics.setMaxPosition(Float.MAX_VALUE);
        mDynamics.setMinPosition(-Float.MAX_VALUE);
        mLastSnapPos = Integer.MIN_VALUE;
        if (!mRotationEnabled) {
            mListRotation = 0;
            mDynamics.setMaxPosition(0);
        } else {
            mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
            setSnapPoint();
            if (mDynamics != null) {
                mDynamics.setState(mListTop, mDynamics.getVelocity(), AnimationUtils
                        .currentAnimationTimeMillis());
                post(mDynamicsRunnable);
            }
        }    
        invalidate();
    }

    public boolean isRotationEnabled() {
        return mRotationEnabled;
    }

    public void enableLight(final boolean enable) {
        mLightEnabled = enable;
        if (!mLightEnabled) {
            mPaint.setColorFilter(null);
        } else {
            mPaint.setAlpha(0xFF);
        }
        invalidate();
    }

    public boolean isLightEnabled() {
        return mLightEnabled;
    }
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		// 異常處理
		if (mAdapter == null) {
			return;
		}
		
		// 當前ListView沒有任何子視圖(Item)，所以依次在從上向下填充子視圖
		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			// add and measure
			fillListDown(mListTop, 0);
		} else {
			final int offset = mListTop + mListTopOffset - getChildTop(getChildAt(0));
			// 移除可視區域的都幹掉
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		// layout，添加測量完後，獲取視圖擺放位置
		positioinItems();
		
		// draw， 上面子視圖都添加完了，重繪布局把子視圖繪制出來吧
		invalidate();
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(ev);
			return false;

		case MotionEvent.ACTION_HOVER_MOVE:
			return startScrollIfNeeded((int)ev.getY());
			
		default:
			endTouch(0);
			return false;
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getChildCount() == 0) {
			return false;
		}
		
		final int y = (int) event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(event);
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchMode == TOUCH_MODE_DOWN) {
				startScrollIfNeeded(y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
                mVelocityTracker.addMovement(event);
				scrollList(y - mTouchStartY);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			float velocity = 0;
			// 如果當前觸摸沒有觸發滾動，狀態依然是DOWN
			// 說明是點擊某一個Item
			if (mTouchMode == TOUCH_MODE_DOWN) {
				clickChildAt((int)event.getX(), y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);
                velocity = mVelocityTracker.getYVelocity();
            }
			
			endTouch(velocity);
			break;
		
		default:
			endTouch(0);
			break;
		}
		
		return true;
	}
	


	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			return super.drawChild(canvas, child, drawingTime);
		}
		
		// 當前Item左側頂部坐標值
		int left = child.getLeft();
		int top = child.getTop();
		
		// 當前Item中部偏移
		int centerX = child.getWidth() / 2;
		int centerY = child.getHeight() / 2;
		
		
		// 計算離中間位置的距離
		float centerScreen = getHeight() / 2;
		
		// 計算縮放
		// ?
		float distFromCenter = (top + centerY - centerScreen) / centerScreen;
        float scale = (float)(1 - SCALE_DOWN_FACTOR * (1 - Math.cos(distFromCenter)));
        
        // 計算旋轉
        float childRotation = mListRotation - 20 * distFromCenter;
        childRotation %= 90;
        if (childRotation < 0) {
			childRotation += 90;
		}
		
        
        // 繪制當前Item
        if (childRotation < 45) {
        	// 菱角朝上時 - 下側3D
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation - 90);
			// 正中心顯示的Item
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation);
		} else {
        	// 正中心顯示的Item
            drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation);
            // 菱角朝上時 - 上側3D
            drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation - 90);
		}
		
		return false;
	}




	// ===========================================================
	// Private Methods
	// ===========================================================


	/**
	 * 繪制3D界面塊
	 * 
	 * @param canvas  drawChild回調提供的Canvas對象
	 * @param view    
	 * @param top
	 * @param left
	 * @param centerX
	 * @param centerY
	 * @param scale
	 * @param rotation
	 */
	private void drawFace(final Canvas canvas, final Bitmap view, final int top, final int left,
			final int centerX, final int centerY, final float scale, final float rotation) {
		
		// 如果之前沒有創建新對象
		if (mCamera == null) {
			mCamera = new Camera();
		}
		
		// 保存，以免以下操作對之後系統使用的Canvas造成影響
		mCamera.save();
		
		// 平移和旋轉Camera
		mCamera.translate(0, 0, centerY);
		mCamera.rotateX(rotation);
		mCamera.translate(0, 0, -centerY);
		
		// 如果之前沒有Matrix創建新對象
		if (mMatrix == null) {
			mMatrix = new Matrix();
		}
		
		mCamera.getMatrix(mMatrix);
		mCamera.restore();
		
		// 平移和縮放Matrix
		mMatrix.preTranslate(-centerX, -centerY);
		mMatrix.postScale(scale, scale);
		mMatrix.postTranslate(left + centerX, top + centerY);
		
		// 創建和初始化
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
		}
		
		// 
		if (mLightEnabled) {
			mPaint.setColorFilter(calculateLight(rotation));
		} else {
			// 
			mPaint.setAlpha(0xFF - (int)(2 * Math.abs(rotation)));
		}
		
		
		// 繪制Bitmap
		canvas.drawBitmap(view, mMatrix, mPaint);
		
	}
	
	
    private LightingColorFilter calculateLight(final float rotation) {
        final double cosRotation = Math.cos(Math.PI * rotation / 180);
        int intensity = AMBIENT_LIGHT + (int)(DIFFUSE_LIGHT * cosRotation);
        int highlightIntensity = (int)(SPECULAR_LIGHT * Math.pow(cosRotation, SHININESS));

        if (intensity > MAX_INTENSITY) {
            intensity = MAX_INTENSITY;
        }
        if (highlightIntensity > MAX_INTENSITY) {
            highlightIntensity = MAX_INTENSITY;
        }

        final int light = Color.rgb(intensity, intensity, intensity);
        final int highlight = Color.rgb(highlightIntensity, highlightIntensity, highlightIntensity);

        return new LightingColorFilter(light, highlight);
    }
	
	
	
	/**
	 * ListView初始化
	 */
	private void initListView(Context context) {
		
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	/**
	 * 向當前ListView添加子視圖並負責Measure子視圖操作
	 * 
	 * @param child  需要添加的ListView子視圖(Item)  
	 * @param layoutMode  在頂部添加上面添加還是在底部下面添加子視圖 ， LAYOUT_MODE_ABOVE 或 LAYOUT_MODE_BELOW
	 */
	private void addAndMeasureChild(View child, int layoutMode) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		// addViewInLayout  index？
		final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0: -1;
		child.setDrawingCacheEnabled(true);
		addViewInLayout(child, index, params, true);
	
		final int itemWidth = (int) (getWidth() * ITEM_WIDTH);
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * 對所有子視圖進行layout操作，取得所有子視圖正確的位置
	 */
	private void positioinItems() {
		int top = mListTop + mListTopOffset;
        final float amplitude = getWidth() * AMPLITUDE;
        final float frequency = 1 / (getHeight() * WAVELENGTH);
		
		
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			
			final int offset = (int)(amplitude * Math.sin(2 * Math.PI * frequency * top));
			// 當前視圖未雖然添加到ViewGroup但是還未重新進行measure, layout, draw操作
			// 直接通過child.getWidth();獲取不到寬度
			final int width = child.getMeasuredWidth();
			final int height = child.getMeasuredHeight();
			final int left = offset + (getWidth() - width) / 2;
            final int margin = getChildMargin(child);
            final int childTop = top + margin;
			
			child.layout(left, childTop, left + width, childTop + height);
			// 上下都需要Margin
			top += height + 2 * margin;
		}
	}

	/**
	 * 初始化用於之後觸摸事件判斷處理的參數
	 * 
	 * @param event 
	 */
	private void startTouch(MotionEvent event) {
		removeCallbacks(mDynamicsRunnable);
		
		mTouchStartX = (int) event.getX();
		mMotionY = mTouchStartY = (int) event.getY();
		
		mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
		
		startLongPressCheck();
		
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
		
		mTouchMode = TOUCH_MODE_DOWN;
	}

	/**
	 * 是否滿足滾動條件
	 * 
	 * @param y     當前觸摸點Y軸的值
	 * @return true 可以滾動
	 */
	private boolean startScrollIfNeeded(int y) {
		// 不同，此處模擬AbsListView實現
		
		final int deltaY = y - mMotionY;
		final int distance = Math.abs(deltaY);
		
		// 只有移動一定距離之後才認為目的是想讓ListView滾動
		if (distance > mTouchSlop) {
		
			
			// 記錄當前處於滾動狀態
			mTouchMode = TOUCH_MODE_SCROLL;
			return true;
		}
		
		return false;
	}

	/**
	 * 通過觸摸點X,Y軸坐標獲取是點擊哪一個Item
	 * 
	 * @param x 觸摸點X軸值
	 * @param y 觸摸點Y軸值
	 * @return
	 */
	private int getContainingChildIndex(int x, int y) {
		if (mRect == null) {
			mRect = new Rect();
		}
		
		// 遍歷當前ListView所有Item
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).getHitRect(mRect);
			// x,y是否在當前視圖區域內
			if (mRect.contains(x, y)) {
				return i;
			}
		}
		
		return INVALID_POSITION;
	}
	
	/**
	 * 控制ListView進行滾動
	 * 
	 * @param scrolledDistance 當前手指坐在位置與剛觸摸到屏幕之間的距離,
	 * 								也就是當前手指在屏幕上Y軸總移動位置
	 */
	private void scrollList(final int scrolledDistance) { // scrollIfNeeded
		// 改變當前記錄的ListView頂部位置
		mListTop = mListTopStart + scrolledDistance;
		
        if (mRotationEnabled) {
            mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
        }
		
        setSnapPoint();
        
		// 關鍵，要想使相面的計算生效必須重新請求布局
		// 會觸發當前onLayout方法，指定Item位置與繪制先關還是在onLayout中
		requestLayout();
	}
	
	/**
	 * ListView向上或者向下移動後需要向頂部或者底部添加視圖
	 * 
	 * @param offset
	 */
	private void fillList(final int offset) {
		// 最後一個item的下邊界值就是當前ListView的下邊界值
		final int bottomEdge = getChildBottom(getChildAt(getChildCount() - 1));
		fillListDown(bottomEdge, offset);
		
		// 第一個Item的上邊界值就是ListVie的上邊界值
		final int topEdge = getChildTop(getChildAt(0));
		fillListUp(topEdge, offset);
	}
	
	
	/**
	 * 與fillListDown相反方向添加
	 * 
	 * @param topEdge 當前第一個子視圖頂部邊界值
	 * @param offset 顯示區域偏移量
	 */
	private void fillListUp(int topEdge, int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			// 現在添加的視圖時當前子視圖前面，所以位置-1
			mFirstItemPosition--;
			
			View newTopChild = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
			addAndMeasureChild(newTopChild, LAYOUT_MODE_ABOVE);
			int childHeight = getChildHeight(newTopChild);
			topEdge -= childHeight;
			
			// 在頂部添加視圖後，更新頂部偏移
			mListTopOffset -= childHeight;
		}
	}
	
	
	/**
	 * 向當前最後一個子視圖下面添加，填充到當前ListView底部無再可填充區域為止
	 * 
	 * @param bottomEdge 當前最後一個子視圖底部邊界值
	 * @param offset 顯示區域偏移量
	 */
	private void fillListDown(int bottomEdge, int offset) {
		while (bottomEdge + offset < getHeight() && mLastItemPosition < mAdapter.getCount() - 1) {
			// 現在添加的視圖時當前子視圖後面，所以位置+1
			mLastItemPosition++;
			
			// 數據和視圖通過Adapter適配，此處從Adapter獲取視圖。
			// 第二個參數傳入複用的View對象，先出入null，之後再添加View對象複用機制
			View newBottomChild = mAdapter.getView(mLastItemPosition, getCachedView(), this);
			// **具體添加視圖處理
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW);
			// 添加一個子視圖(Item)，隨之底部邊界也發生改變
			bottomEdge += getChildHeight(newBottomChild);
		}
	}

	
	/**
	 * 觸摸屏幕結束，進行清理操作
	 */
	private void endTouch(final float velocity) {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
		
		// 都結束了，無論是否執行了，幹掉長按子線程
		removeCallbacks(mLongPressRunnable);
		
        if (mDynamicsRunnable == null) {
            mDynamicsRunnable = new Runnable() {
                public void run() {
                    if (mDynamics == null) {
                        return;
                    }
                    mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
                    mDynamics.update(AnimationUtils.currentAnimationTimeMillis());

                    scrollList((int)mDynamics.getPosition() - mListTopStart);

                    if (!mDynamics.isAtRest(VELOCITY_TOLERANCE, POSITION_TOLERANCE)) {
                        postDelayed(this, 16);
                    }

                }
            };
        }

        if (mDynamics != null) {
            // update the dynamics with the correct position and start the
            // runnable
            mDynamics.setState(mListTop, velocity, AnimationUtils.currentAnimationTimeMillis());
            post(mDynamicsRunnable);
        }
		
		mTouchMode = TOUCH_MODE_REST;
	}

	/**
	 * 調用ItemClickListener提供當前點擊位置
	 * 
	 * @param x 觸摸點X軸值
	 * @param y 觸摸點Y軸值
	 */
	private void clickChildAt(int x, int y) {
		// 觸摸點在當前顯示所有Item中哪一個
		final int itemIndex = getContainingChildIndex(x, y);
		
		if (itemIndex != INVALID_INDEX) {
			final View itemView = getChildAt(itemIndex);
			// 當前Item在ListView所有Item中的位置
			final int position = mFirstItemPosition + itemIndex;
			final long id = mAdapter.getItemId(position);
			
			// 調用父類方法，會觸發ListView ItemClickListener
			performItemClick(itemView, position, id);
		}
	}

	/**
	 * 開啟異步線程，條件允許時調用LongClickListener
	 */
	private void startLongPressCheck() {
		// 創建子線程
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {
				
				@Override
				public void run() {
					if (mTouchMode == TOUCH_MODE_DOWN) {
						final int index = getContainingChildIndex(
								mTouchStartX, mTouchStartY);
						if (index != INVALID_INDEX) {
							longClickChild(index);
						}
					}
				}
			};
		}
		
		// ViewConfiguration.getLongPressTimeout() 獲取系統配置的長按的時間間隔
		// 如果點擊已經超過長按要求時間，才開始執行此線程
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	
	
	/**
	 * 調用ItemLongClickListener提供點擊位置等信息
	 * 
	 * @param index Item索引值
	 */
	private void longClickChild(final int index) {
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		// 從父類獲取綁定的OnItemLongClickListener
		OnItemLongClickListener listener = getOnItemLongClickListener();
		
		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}
	
	
	
	/**
	 * 刪除當前已經移除可視範圍的Item View
	 * 
	 * @param offset 可視區域偏移量
	 */
	private void removeNonVisibleViews(final int offset) {
		int childCount = getChildCount();
		
		/**  ListView向上滾動，刪除頂部移除可視區域的所有視圖  **/
		
		// 不在ListView底部，子視圖大於1
		if (mLastItemPosition != mAdapter.getCount() -1 && childCount > 1) {
			View firstChild = getChildAt(0);
			// 通過第二條件判斷當前最上面的視圖是否被移除可是區域
			while (firstChild != null && getChildBottom(firstChild) + offset < 0) {
				// 既然頂部第一個視圖已經移除可視區域從當前ViewGroup中刪除掉
				removeViewInLayout(firstChild);
				// 用於下次判斷，是否當前頂部還有需要移除的視圖
				childCount--;
				// View對象回收，目的是為了複用
				mCachedItemViews.addLast(firstChild);
				// 既然最上面的視圖被幹掉了，當前ListView第一個顯示視圖也需要+1
				mFirstItemPosition++;
				// 同上更新
				mListTopOffset += getChildHeight(firstChild);
				
				// 為下一次while遍歷獲取參數
				if (childCount > 1) {
					// 當前已經刪除第一個，再接著去除刪除後剩余的第一個
					firstChild = getChildAt(0);
				} else {
					// 沒啦
					firstChild = null;
				}
			}
		}
		
		
		/**  ListView向下滾動，刪除底部移除可視區域的所有視圖  **/
		// 與上面操作一樣，只是方向相反一個頂部操作一個底部操作
		if (mFirstItemPosition != 0 && childCount > 1) {
			View lastChild = getChildAt(childCount - 1);
			while (lastChild != null && getChildTop(lastChild) + offset > getHeight()) {
				removeViewInLayout(lastChild);
				childCount--;
				mCachedItemViews.addLast(lastChild);
				mLastItemPosition--;
				
				if (childCount > 1) {
					lastChild = getChildAt(childCount - 1);
				} else {
					 lastChild = null;
				}
			}
		}
		
	}
	
	
	/**
	 * 獲取一個可以複用的Item View
	 * 
	 * @return view 可以複用的視圖或者null
	 */
	private View getCachedView() {
		
		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}
		
		return null;
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}