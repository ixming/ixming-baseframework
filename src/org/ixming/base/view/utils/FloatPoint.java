package org.ixming.base.view.utils;

import android.graphics.Point;

/**
 * This is a simple utility Class;
 * <br/>
 * 
 * just like {@link android.graphics.Point},
 * but values of x, y  are float.
 * 
 * @author Yin Yong
 */
public final class FloatPoint {
	public float x;
	public float y;
	
	// >>>>>>>>>>>>>>>>>>>>>>
	// constructors
	public FloatPoint() {}

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public FloatPoint(FloatPoint src) {
        set(src);
    }
    
    public FloatPoint(Point src) {
        set(src);
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<
    
    public void set(FloatPoint src) {
    	set(src.x, src.y);
    }
    
    public void set(Point src) {
    	set(src.x, src.y);
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        x = -x;
        y = -y;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    @Override public boolean equals(Object o) {
        if (o instanceof FloatPoint) {
        	FloatPoint p = (FloatPoint) o;
            return equals(p.x, p.y);
        }
        if (o instanceof Point) {
        	Point p = (Point) o;
            return equals(p.x, p.y);
        }
        return false;
    }

    @Override public int hashCode() {
        return (int) (x * 32713 + y);
    }

    @Override public String toString() {
        return "FloatPoint(" + x + ", " + y+ ")";
    }

}
