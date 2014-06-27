package org.ixming.inject4android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.animation.AnimationUtils;

/**
 * 组员对象的种类。（请根据实际需要正确选择，否则将导致异常）
 * 
 * @author Yin Yong
 * @version 1.0
 */
public enum ResTargetType {

	/**
	 * 返回值类型为{@link android.graphics.Animation}
	 */
    Animation {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "anim";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return AnimationUtils.loadAnimation(context, id);
		}
	},
	/**
	 * 返回值类型为{@link android.graphics.Bitmap}
	 */
    Bitmap {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "drawable";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return BitmapFactory.decodeResource(context.getResources(), id);
		}
	},
	/**
	 * 返回值类型为{@link boolean}
	 */
    Boolean {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "value";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getBoolean(id);
		}
	},
	/**
	 * 返回值类型为{@link int}
	 */
    Color {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "color";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getColor(id);
		}
	},
	/**
	 * 返回值类型为{@link android.content.res.ColorStateList}
	 */
    ColorStateList {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "color";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getColorStateList(id);
		}
	},
	/**
	 * 返回值类型为{@link float}
	 */
    Dimension {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "dimen";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getDimension(id);
		}
	},
	/**
	 * 返回值类型为{@link int}
	 */
    DimensionPixelOffset {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "dimen";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getDimensionPixelOffset(id);
		}
	},
	/**
	 * 返回值类型为{@link int}
	 */
    DimensionPixelSize {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "dimen";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getDimensionPixelSize(id);
		}
	},
	/**
	 * 返回值类型为{@link android.graphics.drawable.Drawable}
	 */
    Drawable {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "drawable";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getDrawable(id);
		}
	},
	/**
	 * 返回值类型为{@link int}
	 */
    Integer {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "value";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getInteger(id);
		}
	},
	/**
	 * 返回值类型为{@link int[]}
	 */
    IntArray {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "array";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getIntArray(id);
		}
	},
	/**
	 * 返回值类型为{@link android.graphics.Movie}
	 */
    Movie {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "raw";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getMovie(id);
		}
	},
	/**
	 * 返回值类型为{@link java.lang.String}
	 */
    String {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "string";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getString(id);
		}
	},
	/**
	 * 返回值类型为{@link java.lang.String[]}
	 */
    StringArray {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "array";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getStringArray(id);
		}
	},
	/**
	 * 返回值类型为{@link java.lang.String}
	 */
    Text {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "string";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getText(id);
		}
	},
	/**
	 * 返回值类型为{@link java.lang.String[]}
	 */
    TextArray {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "array";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getTextArray(id);
		}
	},
	/**
	 * 返回值类型为{@link android.content.res.XmlResourceParser}
	 */
    Xml {
		@Override
		public java.lang.String getAsAndroidResType() {
			return "xml";
		}

		@Override
		public Object loadRes(Context context, int id) {
			return context.getResources().getXml(id);
		}
	};
    
	/**
     * 获取该种类型在Android中相应的资源Type
     */
    public abstract String getAsAndroidResType();
    
    /**
     * 该种类型是否支持Theme
     */
    public boolean isSupportTheme() {
    	return true;
    }
    
    /**
     * 加载相应的资源
     */
    public abstract Object loadRes(Context context, int id) ;
    
}