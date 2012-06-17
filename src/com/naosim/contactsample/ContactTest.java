package com.naosim.contactsample;

import java.util.ArrayList;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

public class ContactTest extends AndroidTestCase {
	public static final String TAG = "ContactTest";
	// 注意！：このテストは、電話帳に以下の環境がないとグリーンになりません。
	// 環境を作って、URIやLOOKUPを調整してください。

	// メールだけ登録されている場合
	public static final String MAILONLY_URI = "content://com.android.contacts/contacts/lookup/0r1-452D3D434947435D51353D452D3D434947435D45353D/1";
	public static final String MAILONLY_LOOKUP = "0r1-452D3D434947435D51353D452D3D434947435D45353D";
	public static final String MAILONLY_MAIL = "mailonly@naosim.com";
	public static final String MAILONLY_NAME_FRAGMENT = "Mail Only";

	// ほかに同じメアドで登録してあるデータがある(1)
	public static final String ANYSAMEMAIL1_URI = "";
	public static final String ANYSAMEMAIL1_MAIL = "anysame@naosim.com";
	public static final String ANYSAMEMAIL1_FRAGMENT = "AnySameMail1";

	// ほかに同じメアドで登録してあるデータがある(2)
	public static final String ANYSAMEMAIL2_URI = "";
	public static final String ANYSAMEMAIL2_MAIL = "anysame@naosim.com";
	public static final String ANYSAMEMAIL2_FRAGMENT = "AnySameMail2";

	// 電話だけ
	public static final String PHONEONLY_URI = "content://com.android.contacts/contacts/lookup/0r5-4B3B4947354947435D/5";
	public static final String PHONEONLY_LOOKUP = "0r5-4B3B4947354947435D";
	public static final String PHONEONLY_MAIL = "";
	public static final String PHONEONLY_FRAGMENT = "PhoneOnly";

	// 電話が２つ
	public static final String PHONEANY_URI = "content://com.android.contacts/contacts/lookup/0r2-4B3B4947352D475D/2";
	public static final String PHONEANY_LOOKUP = "0r2-4B3B4947352D475D";
	public static final String PHONEANY_MAIL = "";
	public static final String PHONEANY_FRAGMENT = "PhoneAny";

	MyContactDao dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dao = new MyContactDao();
		dao.cr = getContext().getContentResolver();
	}

	/**
	 * 通常
	 */
	public void test_getLookup() {
		String act = dao.getLookup(Uri.parse(MAILONLY_URI));
		Log.e(TAG, act);
		assertFalse(TextUtils.isEmpty(act));
	}

	/**
	 * 通常
	 */
	public void test_getLookups() {
		ArrayList<String> act = dao.getLookups(MAILONLY_MAIL);
		assertEquals(1, act.size());
		assertFalse(TextUtils.isEmpty(act.get(0)));
	}

	public void test_getLookups_any() {
		ArrayList<String> act = dao.getLookups(ANYSAMEMAIL1_MAIL);
		assertEquals(2, act.size());
		assertFalse(TextUtils.isEmpty(act.get(0)));
		assertFalse(TextUtils.isEmpty(act.get(1)));
	}

	public void test_getDisplayName() {
		String act = dao.getDisplayName(MAILONLY_MAIL);
		assertFalse(TextUtils.isEmpty(act));
	}

	/**
	 * 通常
	 */
	public void test_getPhoneNumbersFromLookup() {
		ArrayList<String> act = dao.getPhoneNumbersFromLookup(PHONEONLY_LOOKUP);
		assertEquals(1, act.size());
		assertFalse(TextUtils.isEmpty(act.get(0)));
		Log.e(TAG, act.get(0));
	}

	/**
	 * 複数電話場号
	 */
	public void test_getPhoneNumbersFromLookup_any() {
		ArrayList<String> act = dao.getPhoneNumbersFromLookup(PHONEANY_LOOKUP);
		assertEquals(2, act.size());
		assertFalse(TextUtils.isEmpty(act.get(0)));
		assertFalse(TextUtils.isEmpty(act.get(1)));
		Log.e(TAG, act.get(0));
	}

	public void test_getPhoneNumbersFromAddress() {
		ArrayList<String> act = dao
				.getPhoneNumbersFromAddress(ANYSAMEMAIL1_MAIL);

		// おなじアドレスの人が２人いる
		assertEquals(2, act.size());
		assertFalse(TextUtils.isEmpty(act.get(0)));
		assertFalse(TextUtils.isEmpty(act.get(1)));
		Log.e(TAG, act.get(0));
	}

}
