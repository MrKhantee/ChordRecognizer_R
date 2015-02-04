package com.jzap.chordrecognizer_r;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Justin on 2/2/2015.
 */
public class Vector2D {

    private static String TAG = "Vector2D";

    private Point mOriginPoint;
    private double mLength;
    private int mAngle;
    private Point mEndPoint;

    public Vector2D(Point origin, double length, int angle) {
        mLength = length;
        mAngle = angle;
        mOriginPoint = origin;
        mEndPoint = new Point();
        calculateEndPoint();
    }

    private void calculateEndPoint() {
        mEndPoint.x = (int) ((double) mOriginPoint.x +  (Math.cos(Math.toRadians(mAngle)) * mLength));
        mEndPoint.y = (int) ((double) mOriginPoint.y +  (Math.sin(Math.toRadians(mAngle)) * mLength));
    }

    public Point getEndPoint() {
        return mEndPoint;
    }

}
