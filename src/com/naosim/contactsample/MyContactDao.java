package com.naosim.contactsample;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class MyContactDao {
	public ContentResolver cr;
	
	/**
	 * 電話帳取得から受けたUriよりLookupを取得する
	 * 
	 * @param data
	 * @return
	 */
	public String getLookup(Uri data) {
		Cursor c = cr.query(data, new String[] { Email.LOOKUP_KEY }, null,
				null, null);
		c.moveToFirst();
		String lookup = c.getString(c.getColumnIndex(Email.LOOKUP_KEY));
		c.close();

		return lookup;
	}

	/**
	 * メールアドレスからLookupを取得する
	 * 同じメールアドレスを複数件の電話帳に登録してあることも考慮
	 * @param address
	 * @return
	 */
	public ArrayList<String> getLookups(String address) {
		ArrayList<String> result = new ArrayList<String>();

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
	
	/**
	 * 表示名取得
	 * 複数電話帳に登録されてる場合でも１個だけ名前を取得します。
	 * @param address
	 * @return
	 */
	public String getDisplayName(String address) {
		String result = null;
		
		// 表示名のカラム名は、ContactsContract.Data.DISPLAY_NAMEを使うこと！
		// Email.DISPLAY_NAMEはだめ。

		String selection = Email.DATA1 + "== ?";
		String[] selectionArgs = new String[] { address };
		Cursor c = cr.query(Email.CONTENT_URI,
				new String[] { ContactsContract.Data.DISPLAY_NAME }, selection, selectionArgs,
				null);
		c.moveToFirst();
		result = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)); 
		c.close();
		return result;
	}
	
	/**
	 * lookupから電話番号を取得する
	 * 電話番号を複数登録している可能性も考慮
	 * @param lookup
	 * @return
	 */
	public ArrayList<String> getPhoneNumbersFromLookup(String lookup) {
		ArrayList<String> result = new ArrayList<String>();
		
		String selection = Phone.LOOKUP_KEY + "== ?";
		String[] selectionArgs = new String[] { lookup };
		Cursor c = cr.query(Phone.CONTENT_URI,
				new String[] { Phone.NUMBER }, selection, selectionArgs,
				null);
		c.moveToFirst();
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			String number = c.getString(c.getColumnIndex(Phone.NUMBER));
			result.add(number);
			c.moveToNext();
		}
		c.close();
		
		return result;
		
	}
	
	/**
	 * メールアドレスから電話番号を取得する
	 * @param address
	 * @return
	 */
	public ArrayList<String> getPhoneNumbersFromAddress(String address) {
		
		ArrayList<String> lookups = getLookups(address);

		ArrayList<String> result = new ArrayList<String>();
		for(String lookup : lookups) {
			result.addAll(getPhoneNumbersFromLookup(lookup));
		}
		
		return result;
		
	}


}
