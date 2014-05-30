package org.ixming.base.view.utils;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * This is a simple utility Class;
 * <br/>
 * 
 * just like {@link android.graphics.Rect},
 * 
 * but values of left, top, right, bottom  are float.
 * 
 * @author Yin Yong
 */
public final class FloatRect {
	public float left;
	public float top;
	public float right;
	public float bottom;

	public FloatRect() {
	}
	public FloatRect(float left, float top, float right, float bottom) {
		set(left, top, right, bottom);
	}
	public FloatRect(FloatRect src) {
		set(src);
	}

	public FloatRect(Rect src) {
		set(src);
	}
	
	public void set(FloatRect another) {
		set(another.left, another.top, another.right, another.bottom);
	}
	
	public void set(Rect another) {
		set(another.left, another.top, another.right, another.bottom);
	}
	
	public void set(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public final float width() {
		return right - left;
	}

	public final float height() {
		return bottom - top;
	}
	
	public final float centerX() {
		return (left + right) / 2 ;
	}
	
	public final float centerY() {
		return (top + bottom) / 2 ;
	}

	public void offset(float dx, float dy) {
		left += dx;
		top += dy;
		right += dx;
		bottom += dy;
	}
	
	/**
     * Inset the rectangle by (dx,dy). If dx is positive, then the sides are
     * moved inwards, making the rectangle narrower. If dx is negative, then the
     * sides are moved outwards, making the rectangle wider. The same holds true
     * for dy and the top and bottom.
     *
     * @param dx The amount to add(subtract) from the rectangle's left(right)
     * @param dy The amount to add(subtract) from the rectangle's top(bottom)
     */
    public void inset(float dx, float dy) {
        left += dx;
        top += dy;
        right -= dx;
        bottom -= dy;
    }
    
    public boolean contains(FloatPoint point) {
    	return contains(point.x, point.y);
    }
    
    public boolean contains(Point point) {
    	return contains(point.x, point.y);
    }
    
    public boolean contains(float x, float y) {
    	return this.left < this.right && this.top < this.bottom  // check for empty first
    			&& this.left <= x && this.top <= y
				&& this.right >= x && this.bottom >= y;
    }
    
    public boolean contains(FloatRect rect) {
    	return contains(rect.left, rect.top, rect.right, rect.bottom);
    }
    
    public boolean contains(Rect rect) {
        return contains(rect.left, rect.top, rect.right, rect.bottom);
    }
    
	public boolean contains(float left, float top, float right, float bottom) {
		return this.left < this.right && this.top < this.bottom // check for empty first
				// now check for containment
				&& this.left <= left && this.top <= top
				&& this.right >= right && this.bottom >= bottom;
	}
    
    public boolean intersects(FloatRect rect) {
        return intersects(rect.left, rect.top, rect.right, rect.bottom);
    }
    
    public boolean intersects(Rect rect) {
    	return intersects(rect.left, rect.top, rect.right, rect.bottom);
    }
    
    public boolean intersects(float left, float top, float right, float bottom) {
    	return this.left < this.right && this.top < this.bottom // check for empty first
    			// now check for containment
    			&& this.left <= right && left <= this.right
    			&& this.top <= bottom && top <= this.bottom;
    }
    
    @Override
    public String toString() {
        return "FloatRect(" + left + ", " + top + ", "
                      + right + ", " + bottom + ")";
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof FloatRect) {
    		FloatRect another = (FloatRect) o;
    		return eq0(another.left, another.top, another.right, another.bottom);
    	}
    	if (o instanceof Rect) {
    		Rect another = (Rect) o;
    		return eq0(another.left, another.top, another.right, another.bottom);
    	}
    	return super.equals(o);
    }
    
    private boolean eq0(float left, float top, float right, float bottom) {
    	return this.left == left && right == this.right
    			&& this.top == top && bottom == this.bottom;
    }
}
