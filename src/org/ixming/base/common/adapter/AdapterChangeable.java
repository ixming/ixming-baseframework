package org.ixming.base.common.adapter;

import java.util.Collection;
import java.util.Map;

interface AdapterChangeable<T> {
	
	/**
	 * set the primary adapter data, and then we do not call notifyDataSetChanged,
	 * because this is a complex case, we can not guess what will
	 * be called, notifyDataSetChanged or notifyDataSetInvalidate
	 * @param c new data set into the Adapter
	 */
	AdapterChangeable<T> setData(Collection<T> c);
	
	/**
	 * remove the specific item of data from, then refresh adapter view,
	 * and then call notifyDataSetChanged
	 * @param position position of the item in the adapter view 
	 */
	AdapterChangeable<T> removeData(int position);
	
	/**
	 * append single item to the AdapterView, and then call notifyDataSetChanged
	 * @param t new data to append
	 */
	AdapterChangeable<T> appendData(T t);
	
	/**
	 * append a collection of data to the AdapterView, and then call notifyDataSetChanged
	 * @param c new data to append
	 */
	AdapterChangeable<T> appendDataList(Collection<T> c);
	
	/**
	 * append a Map of data to the AdapterView, and then call notifyDataSetChanged
	 * @param m new data Map to append
	 */
	AdapterChangeable<T> appendDataMap(Map<?, T> m);
	
	
	
	/**
	 * pre-append single item to the AdapterView, and then call notifyDataSetChanged
	 * @param t new data to append
	 */
	AdapterChangeable<T> prependData(T t);
	
	/**
	 * pre-append a collection of data to the AdapterView, and then call notifyDataSetChanged
	 * @param c new data to append
	 */
	AdapterChangeable<T> prependDataList(Collection<T> c);
	
	/**
	 * pre-append a Map of data to the AdapterView, and then call notifyDataSetChanged
	 * @param m new data Map to append
	 */
	AdapterChangeable<T> prependDataMap(Map<?, T> m);
	
	
	/**
	 * update the specific position item
	 * @param m new data Map to append
	 */
	AdapterChangeable<T> update(int position, T t);
}
