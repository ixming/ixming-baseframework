package org.ixming.base.utils.debug;

import java.util.Comparator;

public final class DummyComparator<T> implements Comparator<T> {

	@Override
	public int compare(T lhs, T rhs) {
		
		return 0;
	}
	
}
