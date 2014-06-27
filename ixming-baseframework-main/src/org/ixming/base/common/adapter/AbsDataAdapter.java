package org.ixming.base.common.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ixming.base.utils.android.AndroidUtils;
import org.ixming.inject4android.InjectConfigure;
import org.ixming.inject4android.InjectorUtils;


import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * <p>
 * we recommend that from data source to UI, contains: model--controller--view.
 * <br/>
 * that is MVC.(so CursorAdapter is not what we suggest)
 * </p>
 * this is a common reusable adapter for ListView or GridView;
 * it simplifies some operations.
 * <p>
 * data is basic, with which we can do UI updating or modifying;<br/>
 * view holder is a bridge, with which we can reuse view, and more quickly
 * change properties of UI;
 * </p>
 * <p>
 * 	<b>it's not a full MVC, but view holder is like a Model-2-View bridge
 * (but not a real controller), AbsAdapter is the real simplest controller.
 * 	</b>
 * </p>
 * 
 * <p>
 * you can get Context with {@link #getContext()}; <br/>
 * you can change data set with {@link #setData(Collection)}, {@link #appendData(Object)},
 * {@link #appendDataList(Collection)}, {@link #appendDataMap(Map)}; <br/>
 * </p>
 * @param <D> data Class, which we don't care about in abstract class
 * @param <H> view holder, to hold views, with which we will 
 * change UI properties while reusing convertView
 */
public abstract class AbsDataAdapter<D, H> extends BaseAdapter
implements AdapterChangeable<D>{

	/**创建Adapter时，生成一个Handler，以便调用*/
	private Handler mHandler = new Handler();
	private Context mContext;
	private ArrayList<D> mContentList;
	private OnDataSetChangedLisener mOnDataSetChangedLisener;
	
	/**
	 * @param context we recommend you to use an activity context
	 */
	public AbsDataAdapter(Context context) {
		mContext = context;
		mContentList = new ArrayList<D>();
	}
	
	private boolean mIsFinalized = false;
	private final void ensureNotFinalized() {
		if (mIsFinalized) {
			throw new IllegalStateException("the adapter has been finalized!");
		}
	}
	
	/**
	 * 获得该Adapter中的Context对象
	 */
	protected final Context getContext() {
		return mContext;
	}
	
	/**
	 * 获得该Adapter中的Handler对象
	 */
	protected final Handler getHandler() {
		return mHandler;
	}
	
	public synchronized void finalizeAdapter() {
		if (null == mContentList) {
			 return ;
		}
		mContentList.clear();
		mContentList = null;
		mContext = null;
		mIsFinalized = true;
	}
	
	public void setOnDataSetChangedLisener(OnDataSetChangedLisener listener) {
		mOnDataSetChangedLisener = listener;
	}
	
	@Override
	public int getCount() {
		ensureNotFinalized();
		return mContentList.size();
	}

	@Override
	public D getItem(int position) {
		ensureNotFinalized();
		return mContentList.get(position);
	}

	/**
	 * @return default result is position
	 */
	@Override
	public long getItemId(int position) {
		ensureNotFinalized();
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ensureNotFinalized();
		H holder;
		if (null == convertView) {
			convertView = newView(mContext, position, parent);
			holder = newHolder(position, convertView);
			convertView.setTag(holder);
		} else {
			holder = (H) convertView.getTag();
		}
		bindView(holder, getItem(position), position, convertView);
		return convertView;
	}
	
	/**
	 * create a new view holder, using the view of a specific position
	 * @param position the specific position
	 * @param view the content view
	 * @return a new view holder, specified by specific Derived classes
	 */
	protected abstract View newView(Context context, int position, ViewGroup parent) ;
	
	/**
	 * create a new view holder, using the view of a specific position
	 * @param position the specific position
	 * @param view the content view
	 * @return a new view holder, specified by specific Derived classes
	 */
	protected abstract H newHolder(int position, View contentView) ;
	
	/**
	 * based on the holder view, set UI properties with the item
	 * @param holder view holder
	 * @param data item object
	 * @param position item index, child view index
	 * @param view a parameter from {@link #getView(int, View, ViewGroup)}
	 */
	protected abstract void bindView(H holder, D data, int position, View view) ;

	/**
	 * 通知外界内部数据的个数发生了变化。
	 * @param oldCount 旧的数据个数
	 * @param newCount 新的数据个数
	 */
	protected final void postDataSetChanged(final int oldCount, final int newCount) {
		if (null == mOnDataSetChangedLisener) {
			return ;
		}
		if (AndroidUtils.isMainThread()) {
			mOnDataSetChangedLisener.OnDataSetChanged(oldCount, newCount);
		} else {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (null != mOnDataSetChangedLisener) {
						mOnDataSetChangedLisener.OnDataSetChanged(
								oldCount, newCount);
					}
				}
			});
		}
	}
	
	@Override
	public AbsDataAdapter<D, H> setData(Collection<D> c) {
		ensureNotFinalized();
		final int oldCount = getCount();
		mContentList.clear();
		if (null != c) {
			mContentList.addAll(c);
		}
		final int newCount = getCount();
		postDataSetChanged(oldCount, newCount);
		return this;
	}
	
	/**
	 * @return an unmodifiable copy of data list
	 */
	public final List<D> getDataList(){
		ensureNotFinalized();
		return Collections.unmodifiableList(mContentList);
	}
	
	protected final List<D> getContentList() {
		ensureNotFinalized();
		return mContentList;
	}

	@Override
	public final AbsDataAdapter<D, H> removeData(int position) {
		ensureNotFinalized();
		if (position >= 0 && position < mContentList.size()) {
			final int oldCount = getCount();
			mContentList.remove(position);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}

	@Override
	public final AbsDataAdapter<D, H> appendData(D t) {
		ensureNotFinalized();
		if (null != t) {
			final int oldCount = getCount();
			mContentList.add(t);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}

	@Override
	public final AbsDataAdapter<D, H> appendDataList(Collection<D> c) {
		ensureNotFinalized();
		if (null != c && !c.isEmpty()) {
			final int oldCount = getCount();
			mContentList.addAll(c);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}

	@Override
	public final AbsDataAdapter<D, H> appendDataMap(Map<?, D> m) {
		ensureNotFinalized();
		if (null != m) {
			appendDataList(m.values());
		}
		return this;
	}

	
	@Override
	public final AbsDataAdapter<D, H> prependData(D t) {
		ensureNotFinalized();
		if (null != t) {
			final int oldCount = getCount();
			mContentList.add(0, t);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}
	
	@Override
	public final AbsDataAdapter<D, H> prependDataList(Collection<D> c) {
		ensureNotFinalized();
		if (null != c && !c.isEmpty()) {
			final int oldCount = getCount();
			mContentList.addAll(0, c);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}
	
	@Override
	public final AbsDataAdapter<D, H> prependDataMap(Map<?, D> m) {
		ensureNotFinalized();
		if (null != m) {
			prependDataList(m.values());
		}
		return this;
	}
	
	public AbsDataAdapter<D, H> update(int position, D t) {
		ensureNotFinalized();
		if (position > -1 && position < getCount()) {
			final int oldCount = getCount();
			mContentList.set(position, t);
			final int newCount = getCount();
			postDataSetChanged(oldCount, newCount);
		}
		return this;
	}
	
	protected void injectView(Object target, View contentView) {
		InjectorUtils.instanceBuildFrom(InjectConfigure.InjectViewConfigure).inject(target, contentView);
	}
	
	protected void injectRes(Object target) {
		InjectorUtils.instanceBuildFrom(InjectConfigure.InjectResConfigure)
			.inject(target, null, getContext());
	}
}
