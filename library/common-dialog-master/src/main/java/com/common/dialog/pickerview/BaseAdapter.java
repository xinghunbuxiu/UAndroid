package com.common.dialog.pickerview;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 自定义的baseAdapter
 * 
 * @author Administrator
 * @param <T>
 * 
 * @param <T>
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	protected List<T> list;

	protected Context context;


	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	/**
	 * <默认构造函数> 因为涉及到theme，布局需要当前activity的content，所以强制传Context
	 * 
	 * @see ['BaseAdapter(Context context, List<T> list)]
	 */
	public BaseAdapter(List<T> list) {
		this.list = list;
	}

	public BaseAdapter(Context context, List<T> list) {
		this.list = list;
		this.context = context;
	}

	public void clear() {
		this.list.clear();
		notifyDataSetChanged();
	}

	public void addAll(List<T> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public T getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected View inflate(int layoutResID) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(layoutResID, null);
		return view;
	}

	protected View inflate(int layoutResID, ViewGroup root) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(layoutResID, root, false);
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = onCreateView(parent);
		}
		ViewHolder viewHolder = ViewHolder.get(convertView);
		onBindViewHolder(viewHolder, position, getItem(position));
		return convertView;
	}


	public abstract void onBindViewHolder(ViewHolder viewHolder, int position,
			T t);

	public abstract View onCreateView(ViewGroup parent);

	public static class ViewHolder {
		private final SparseArray<View> views;
		private View convertView;

		private ViewHolder(View convertView) {
			this.views = new SparseArray<View>();
			this.convertView = convertView;
			convertView.setTag(this);
		}

		public static ViewHolder get(View convertView) {
			if (convertView.getTag() == null) {
				return new ViewHolder(convertView);
			}
			ViewHolder existedHolder = (ViewHolder) convertView.getTag();
			return existedHolder;
		}

		public <T extends View> T getView(int viewId) {
			View view = views.get(viewId);
			if (view == null) {
				view = convertView.findViewById(viewId);
				views.put(viewId, view);
			}
			return (T) view;
		}
	}

}
