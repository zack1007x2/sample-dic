package loveworld.listview3d.util;

/**
 * Fling效果輔助類
 */
public abstract class Dynamics {
    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MAX_TIMESTEP = 50;
    
    // ===========================================================
    // Fields
    // ===========================================================

    /** 當前位置 **/
    protected float mPosition;
    /** 當前速度 **/
    protected float mVelocity;
    /** 可移動最大位置 **/
    protected float mMaxPosition = Float.MAX_VALUE;
    /** 可移動最小位置 **/
    protected float mMinPosition = -Float.MAX_VALUE;
    /** 記錄上一次更新時的時間值 **/
    protected long mLastTime = 0;

    
    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Setter
    // ===========================================================
    
    /**
     * 設置初始狀態
     * 
     * @param position 當前位置
     * @param velocity 當前速度
     * @param now      當期時間
     */
    public void setState(final float position, final float velocity, final long now) {
        mVelocity = velocity;
        mPosition = position;
        mLastTime = now;
    }

    /**
     * 設置最大可移動到的位置
     * 
     * @param maxPosition
     */
    public void setMaxPosition(final float maxPosition) {
        mMaxPosition = maxPosition;
    }

    
    /**
     * 設置最小可移動到的位置
     * 
     * @param minPosition
     */
    public void setMinPosition(final float minPosition) {
        mMinPosition = minPosition;
    }
    
    // ===========================================================
    // Getter
    // ===========================================================
    
    /**
     * 獲取當前位置
     * 
     * @return
     */
    public float getPosition() {
        return mPosition;
    }

    /**
     * 獲取當前速度
     * 
     * @return
     */
    public float getVelocity() {
        return mVelocity;
    }

    
    /**
     * 是否在恢複狀態
     * 
     * @param velocityTolerance
     * @param positionTolerance
     * @return
     */
    public boolean isAtRest(final float velocityTolerance, final float positionTolerance) {
        final boolean standingStill = Math.abs(mVelocity) < velocityTolerance;
        final boolean withinLimits = mPosition - positionTolerance < mMaxPosition
                && mPosition + positionTolerance > mMinPosition;
        return standingStill && withinLimits;
    }


    /**
     * 更新
     * 
     * @param now
     */
    public void update(final long now) {
        int dt = (int)(now - mLastTime);
        if (dt > MAX_TIMESTEP) {
            dt = MAX_TIMESTEP;
        }

        onUpdate(dt);

        mLastTime = now;
    }

    
    /**
     * 獲取受限制後的可移動距離
     * 
     * @return
     */
    protected float getDistanceToLimit() {
        float distanceToLimit = 0;

        if (mPosition > mMaxPosition) {
            distanceToLimit = mMaxPosition - mPosition;
        } else if (mPosition < mMinPosition) {
            distanceToLimit = mMinPosition - mPosition;
        }

        return distanceToLimit;
    }
    
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    /**
     * @param dt
     */
    abstract protected void onUpdate(int dt);
    
    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

 
    

}