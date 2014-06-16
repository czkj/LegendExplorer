package com.example.legendexplorer.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.example.legendexplorer.model.FileItem;
import com.example.legendexplorer.view.FileItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileListAdapter extends BaseAdapter {
	private ArrayList<FileItem> list = new ArrayList<FileItem>();
	private Context mContext;
	private File currentDirectory;

	public FileListAdapter(Context Context) {
		mContext = Context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = new FileItemView(mContext);
			holder.fileItemView = (FileItemView) convertView;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.fileItemView.setFileItem(list.get(position), this);
		return holder.fileItemView;
	}

	class ViewHolder {
		FileItemView fileItemView;
	}

	public ArrayList<FileItem> getList() {
		return list;
	}

	public void setList(ArrayList<FileItem> list) {
		this.list = list;
	}

	/**
	 * 打开文件夹，更新文件列表
	 * 
	 * @param file
	 */
	public boolean openFolder(File file) {
		if (file != null && file.exists() && file.isDirectory()) {
			if (!file.equals(currentDirectory)) {
				// 与当前目录不同
				currentDirectory = file;
				list.clear();
				File[] files = file.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						list.add(new FileItem(files[i]));
					}
				}
				files = null;
				sortList();
				notifyDataSetChanged();
				return true;
			}
		}
		return false;
		// TODO
		// dialogView.getPathText().setText(file.getAbsolutePath());
	}

	/**
	 * 选择当前目录下所有文件
	 */
	public void selectAll() {
		for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			fileItem.setSelected(true);
		}
		notifyDataSetChanged();
	}

	/**
	 * 取消所有文件的选中状态
	 */
	public void unselectAll() {
		for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			fileItem.setSelected(false);
		}
		notifyDataSetChanged();
	}

	public void change2SelectMode() {
		for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			fileItem.setInSelectMode(true);
		}
		notifyDataSetChanged();
	}

	public void exitSelectMode() {
		for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			fileItem.setInSelectMode(false);
			fileItem.setSelected(false);
		}
		notifyDataSetChanged();
	}

	/**
	 * 只在选中时调用，取消选中不调用，且只由FileItemView调用
	 * 
	 * @param fileItem
	 */
	public void selectOne(FileItem fileItem) {
		fileItem.setSelected(true);
		notifyDataSetChanged();
	}

	public void sortList() {
		FileItemComparator comparator = new FileItemComparator();
		Collections.sort(list, comparator);
	}

	/**
	 * 取消一个的选择，其他逻辑都在FileItemView里面
	 */
	public void unselectOne() {
		// dialogView.unselectCheckBox();
	}

	/**
	 * @return 选中的文件列表
	 */
	public ArrayList<File> getSelectedFiles() {
		ArrayList<File> selectedFiles = new ArrayList<File>();
		for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();// 强制转换为File
			selectedFiles.add(file);
		}
		return selectedFiles;
	}

	public class FileItemComparator implements Comparator<FileItem> {

		@Override
		public int compare(FileItem lhs, FileItem rhs) {
			if (lhs.isDirectory() != rhs.isDirectory()) {
				// 如果一个是文件，一个是文件夹，优先按照类型排序
				if (lhs.isDirectory()) {
					return -1;
				} else {
					return 1;
				}
			} else {
				// 如果同是文件夹或者文件，则按名称排序
				return lhs.getName().toLowerCase()
						.compareTo(rhs.getName().toLowerCase());
			}
		}
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

}