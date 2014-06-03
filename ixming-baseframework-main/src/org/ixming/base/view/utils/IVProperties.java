package org.ixming.base.view.utils;

import android.widget.ImageView;

public final class IVProperties extends ViewProperties {
	
	private ImageView mImageView;

	public IVProperties(ImageView iv) {
		super(iv);
		mImageView = iv;
	}

	// drawable related
	public boolean hasImage() {
		return null != mImageView.getDrawable();
	}

	public float getImageWidth() {
		return hasImage() ? mImageView.getDrawable().getIntrinsicWidth() : 0;
	}

	public float getImageHeight() {
		return hasImage() ? mImageView.getDrawable().getIntrinsicHeight() : 0;
	}
	
	public float getViewVsDrawableScale() {
		if (!hasImage()) {
			return 1.0F;
		}
		float scaleX = getTrueHorizontalSpace() / getImageWidth();
		float scaleY = getTrueVerticalSpace() / getImageHeight();
		return Math.min(scaleX, scaleY);
	}
}
