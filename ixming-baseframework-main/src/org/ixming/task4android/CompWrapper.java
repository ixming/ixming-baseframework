package org.ixming.task4android;

import java.util.Comparator;

class CompWrapper implements Comparator<Runnable> {
	
	private Comparator<? super Runnable> mComparator;
	public void setComparator(Comparator<? super Runnable> comparator) {
		if (null != comparator) {
			return ;
		}
		mComparator = comparator;
	}
	
	@Override
	public int compare(Runnable lhs, Runnable rhs) {
		if (null != mComparator) {
			return mComparator.compare(lhs, rhs);
		}
		return 0;
	}

}
