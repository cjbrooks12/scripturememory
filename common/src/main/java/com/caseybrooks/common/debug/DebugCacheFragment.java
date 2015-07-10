package com.caseybrooks.common.debug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caseybrooks.common.R;
import com.caseybrooks.common.features.NavigationCallbacks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebugCacheFragment extends ListFragment {
	Context context;
	CacheAdapter adapter;
	NavigationCallbacks mCallbacks;
	EditText editText;

	public static Fragment newInstance() {
		return new DebugCacheFragment();
	}

	private static class ListItem implements Comparable<ListItem> {
		File file;

		String filename;
		String location;
		String filesize;

		@Override
		public int compareTo(ListItem another) {
			return filename.compareTo(another.filename);
		}
	}

//Lifecycle and Initialization
//------------------------------------------------------------------------------

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = getActivity();
		setHasOptionsMenu(true);

		editText = new EditText(context);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.filterBy(editText.getText().toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		getListView().addHeaderView(editText);

		initialize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		context = getActivity();

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		mCallbacks.setToolBar("Cache Contents", Color.parseColor("#607D8B"));
	}

	public static int getCacheCount(Context context) {
		int count = context.getCacheDir().listFiles().length;

		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String path = Environment.getExternalStorageDirectory().getPath() + "/scripturememory";
			File folder = new File(path);
			if(!folder.exists())
				folder.mkdir();

			count += folder.listFiles().length;
		}

		return count;
	}

	private void clearAll() {
		new AlertDialog.Builder(context)
				.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							File cache = context.getCacheDir();
							for(File file : cache.listFiles()) {
								file.delete();
							}

							if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
								String path = Environment.getExternalStorageDirectory().getPath() + "/scripturememory";
								File folder = new File(path);

								for(File file : folder.listFiles()) {
									file.delete();
								}

								folder.delete();
							}
						}
						catch(Exception e) {

						}

						initialize();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setTitle("Clear Cache")
				.setMessage("Are you sure you want to delete all files in the cache and external storage location? This cannot be undone.")
				.create()
				.show();
	}

	private void deleteFile(final File file) {
		new AlertDialog.Builder(context)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							file.delete();
						}
						catch(Exception e) {

						}

						initialize();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setTitle("Delete File")
				.setMessage("Are you sure you want to delete " + file.getName() +
						"? This cannot be undone.")
				.create()
				.show();
	}

	private void initialize() {
		ArrayList<ListItem> displayedItems = new ArrayList<>();

		for (File file : context.getCacheDir().listFiles()) {
			ListItem item = new ListItem();
			item.file = file;

			item.filename = file.getName();
			item.location = "Cache";

			long bytes = file.length();
			double kilobytes = bytes / 1024;
			double megabytes = kilobytes / 1024;

			if(megabytes >= 1.0) {
				item.filesize =
					bytes + " Bytes (" +
					megabytes + " MB)";
			}
			else {
				item.filesize =
					bytes + " Bytes (" +
					kilobytes + " KB)";
			}

			displayedItems.add(item);
		}

		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String path = Environment.getExternalStorageDirectory().getPath() + "/scripturememory";
			File folder = new File(path);
			if(!folder.exists())
				folder.mkdir();

			for(File file : folder.listFiles()) {
				ListItem item = new ListItem();
				item.file = file;

				item.filename = file.getName();
				item.location = "External Storage";

				long bytes = file.length();
				double kilobytes = bytes / 1024;
				double megabytes = kilobytes / 1024;

				if(megabytes >= 1.0) {
					item.filesize =
						bytes + " Bytes (" +
						megabytes + " MB)";
				}
				else {
					item.filesize =
						bytes + " Bytes (" +
						kilobytes + " KB)";
				}

				displayedItems.add(item);
			}
		}

		adapter = new CacheAdapter(context, displayedItems);
		adapter.filterBy(editText.getText().toString());
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(adapter.getItem(position - 1).file.getAbsolutePath());
				intent.setDataAndType(uri, "text/plain");

				PackageManager manager = context.getPackageManager();
				List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
				if(infos.size() > 0) {
					//Then there is application can handle your intent
					startActivity(intent);
				}
				else {
					//No Application can handle your intent
					Toast.makeText(context, "No installed applications can view file. Try downloading RootExplorer.", Toast.LENGTH_LONG).show();
				}
			}
		});

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				deleteFile(adapter.getItem(position - 1).file);
				return false;
			}
		});

		setListAdapter(adapter);
	}

	private static class CacheAdapter extends BaseAdapter {
		ArrayList<ListItem> allData;
		ArrayList<ListItem> filteredData;

		Context context;
		LayoutInflater layoutInflater;

		public CacheAdapter(Context context, ArrayList<ListItem> data) {
			super();
			this.allData = data;
			Collections.sort(this.allData);

			this.filteredData = new ArrayList<>();
			this.filteredData.addAll(allData);

			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		public void filterBy(String query) {
			filteredData.clear();

			if(query != null && query.length() > 0) {
				for(ListItem item : allData) {
					if(
							item.filename.toLowerCase().contains(query.toLowerCase()) ||
									item.location.toLowerCase().contains(query.toLowerCase())) {
						filteredData.add(item);
					}
				}
			}
			else {
				filteredData.addAll(allData);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return filteredData.size();
		}

		@Override
		public ListItem getItem(int position) {
			return filteredData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView= layoutInflater.inflate(R.layout.debug_cache_list_item, null);

			TextView key = (TextView) convertView.findViewById(R.id.cache_filename);
			key.setText(getItem(position).filename);

			TextView file = (TextView) convertView.findViewById(R.id.cache_location);
			file.setText(getItem(position).location);

			TextView value = (TextView) convertView.findViewById(R.id.cache_filesize);
			value.setText(getItem(position).filesize);

			return convertView;
		}
	}

	//Menu
//------------------------------------------------------------------------------
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_debug, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_debug_refresh) {
			initialize();
			return true;
		}
		else if(item.getItemId() == R.id.menu_debug_clear) {
			clearAll();
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}

	//Host Activity Interface
//------------------------------------------------------------------------------
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}
