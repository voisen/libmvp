package com.github.voisen.libmvp.utils;

import java.util.LinkedList;

public final class SpeedCounter {

    private long mLastUpdateTime = 0;
    private long mLastValue = 0;
    private final long mCalcDuration;
    private final long mMinUpdateTime;
    private int mMaxCapacity = 2000;

    private final LinkedList<Long> mTimeQueue = new LinkedList<>();
    private final LinkedList<Long> mValuesQueue = new LinkedList<>();

    public SpeedCounter(long calcDuration) {
        this.mCalcDuration = calcDuration;
        mMinUpdateTime = mCalcDuration/mMaxCapacity;
    }

    public SpeedCounter() {
        this(3000);
    }

    public void setMaxCapacity(int maxCapacity) {
        this.mMaxCapacity = maxCapacity;
    }

    public synchronized void add(long addValue){
        updateValue(mLastValue+addValue);
    }

    public synchronized void updateValue(long value){
        long currentTimeMillis = System.currentTimeMillis();
        if (mMinUpdateTime == 0 || currentTimeMillis - mLastUpdateTime > mMinUpdateTime){
            mTimeQueue.add(currentTimeMillis);
            mValuesQueue.add(value);
        }
        mLastValue = value;
        mLastUpdateTime = currentTimeMillis;
        checkQueue();
    }

    public synchronized long getSpeedPerSeconds(){
        checkQueue();
        if (mTimeQueue.isEmpty() || mValuesQueue.isEmpty()){
            return 0;
        }
        Long timeFirst = mTimeQueue.getFirst();
        Long valueFirst = mValuesQueue.getFirst();
        long time = mLastUpdateTime - timeFirst;
        long value = mLastValue - valueFirst;
        return (long) (value * 1000.0/time);
    }

    private void checkQueue() {
        if (mTimeQueue.size() != mValuesQueue.size()){
            mTimeQueue.clear();
            mValuesQueue.clear();
            return;
        }
        long millis = System.currentTimeMillis();
        while (!mTimeQueue.isEmpty()){
            Long first = mTimeQueue.getFirst();
            if (first == null){
                break;
            }
            if (millis - first > mCalcDuration || mTimeQueue.size() > mMaxCapacity){
                mTimeQueue.removeFirst();
                mValuesQueue.removeFirst();
                continue;
            }
            break;
        }
    }

}
