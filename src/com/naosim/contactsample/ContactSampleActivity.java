package com.naosim.contactsample;

import java.util.ArrayList;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ContactSampleActivity extends Activity implements Runnable {
	public Handler handler = new Handler();
	public MyAdapter adapter;
	public Uri data;
	public ListView lv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		adapter = new MyAdapter();
		lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(adapter);

		findViewById(R.id.button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_PICK,
								ContactsContract.Contacts.CONTENT_URI);
						try {
							startActivityForResult(i, 2);
						} catch (Exception e) {
						}

					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.e("onActivityResult", data.getDataString());
		this.data = data.getData();
		new Thread(this).start();

		// Toast.makeText(this, data.getData().toString(),
		// Toast.LENGTH_LONG).show();

		// ArrayList<String> addresses = getAddresses(data.getData());
		//
		// Toast.makeText(this, addresses.toString(), Toast.LENGTH_LONG).show();
	}

	/**
	 * 電話帳取得から受けたUriよりLookupを取得する
	 * 
	 * @param data
	 * @return
	 */
	public String getLookup(Uri data) {
		Cursor c = getContentResolver().query(data, new String[] { Email.LOOKUP_KEY }, null,
				null, null);
//		Cursor c = managedQuery(data, new String[] { Email.LOOKUP_KEY }, null,
//				null, null);
		c.moveToFirst();
		String lookup = c.getString(c.getColumnIndex(Email.LOOKUP_KEY));
		c.close();

		return lookup;
	}

	/**
	 * メールアドレスからLookupを取得する
	 * 
	 * @param address
	 * @return
	 */
	public ArrayList<String> getLookups(String address) {
		ArrayList<String> result = new ArrayList<String>();

		ContentResolver cr = getContentResolver();
		String selection = Email.DATA1 + "== ?";
		String[] selectionArgs = new String[] { address };
		Cursor c = cr.query(Email.CONTENT_URI,
				new String[] { Email.LOOKUP_KEY }, selection, selectionArgs,
				null);
		c.moveToFirst();
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			String lookup = c.getString(c.getColumnIndex(Email.LOOKUP_KEY));
			result.add(lookup);
			c.moveToNext();
		}
		c.close();

		return result;
	}

	// public Cursor getAddressCursor(Uri data) {
	// String lookup = getLookup(data);
	//
	// ContentResolver cr = getContentResolver();
	// String selection = "" + Email.LOOKUP_KEY + "== ?";
	// String[] selectionArgs = new String[] { lookup };
	// Cursor c2 = cr.query(Email.CONTENT_URI, new String[] { Email.DATA1 },
	// selection, selectionArgs, null);
	//
	// return c2;
	// }

	public ArrayList<String> getAddresses(String lookup) {
		Cursor c = getCursor(Email.CONTENT_URI, lookup);
		return getItems(c);
	}

	public Cursor getCursor(Uri uri, String lookup) {
		ContentResolver cr = getContentResolver();
		String selection = "lookup == ?";
		String[] selectionArgs = new String[] { lookup };
		return cr.query(uri, null, selection, selectionArgs, null);
	}

	public static void log(Cursor c) {
		c.moveToFirst();

		int count = c.getCount();
		Log.e("count", "" + count);

		String[] names = c.getColumnNames();

		for (int i = 0; i < names.length; i++) {
			Log.e("aaaa", "" + names[i]);
			try {
				String a = c.getString(c.getColumnIndex(names[i]));
				Log.e("aaaa", "" + a);
			} catch (Exception e) {
			}
			try {
				int a = c.getInt(c.getColumnIndex(names[i]));
				Log.e("aaaa", "" + a);
			} catch (Exception e) {
			}
		}
		c.close();
	}

	ArrayList<String> items;

	@Override
	public void run() {
		ArrayList<String> items = new ArrayList<String>();
		
		MyContactDao dao = new MyContactDao();
		dao.cr = getContentResolver();
		String hoge = dao.getLookup(data);

//		Cursor c = managedQuery(data, null, null, null, null);
		Cursor c = getContentResolver().query(data, null, null, null, null);
		// BASE
		items.add("" + data.toString() + " ###############");
		items.addAll(getItems(c));

		// lookup
		items.add("lookup ###############");
		String lookup = getLookup(data);
		items.add(lookup);

		// Email
		items.add("" + Email.CONTENT_URI + " ###############");
		items.addAll(getItems(getCursor(Email.CONTENT_URI, lookup)));

		// Phone
		items.add("" + Phone.CONTENT_URI + " ###############");
		items.addAll(getItems(getCursor(Phone.CONTENT_URI, lookup)));

		this.items = items;
		handler.postDelayed(refresh, 200);

	}

	Runnable refresh = new Runnable() {

		@Override
		public void run() {
			adapter.listItems = items;
			// lv.
			adapter.notifyDataSetChanged();

		}
	};

	public ArrayList<String> getItems(Cursor c) {
		ArrayList<String> result = new ArrayList<String>();
		c.moveToFirst();
		int count = c.getCount();
		String[] names = c.getColumnNames();
		for (int n = 0; n < count; n++, result.add("----------------------")) {
			for (int i = 0; i < names.length; i++) {
				String item = names[i] + "\n";
				try {
					// item += "str:" + c.getString(c.getColumnIndex(names[i]))
					// +
					// " ";
					item += "" + c.getString(c.getColumnIndex(names[i]));
				} catch (Exception e) {
				}
				// try {
				// item += "int:" + c.getInt(c.getColumnIndex(names[i])) + " ";
				// } catch (Exception e) {
				// }
				result.add(item);
			}
			c.moveToNext();
		}
		c.close();
		return result;
	}

	public class MyAdapter extends BaseAdapter {
		ArrayList<String> listItems;

		@Override
		public int getCount() {
			if (listItems == null) {
				return 0;
			}
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout lv = (LinearLayout) convertView;
			if (convertView == null) {
				lv = (LinearLayout) getLayoutInflater().inflate(R.layout.row,
						null);
			}
			TextView v = (TextView) lv.findViewById(R.id.text);
			v.setText(listItems.get(position));

			return lv;
		}

	}
}