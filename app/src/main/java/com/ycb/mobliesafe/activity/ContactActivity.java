package com.ycb.mobliesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ycb.mobliesafe.R;

public class ContactActivity extends Activity {
	private ListView lvList;
	private ArrayList<HashMap<String, String>> readContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		lvList = (ListView) findViewById(R.id.lv);
		readContact = readContact();
		lvList.setAdapter(new SimpleAdapter(this, readContact,
				R.layout.contact_list_item, new String[] { "name", "phone" },
				new int[] { R.id.name, R.id.phone }));
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//读取当前item的号码
				String phone = readContact.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				//将数据放在intent中返回给上一个页面
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private ArrayList<HashMap<String, String>> readContact() {
		// 先从raw_contacts中读取到联系人id("contact_id")
		// 再根据contact_id到dada表中查询相应的电话号码和联系人名称
		// 最后根据mimetype来区分哪个是联系人，哪个是电话号码
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactsCursor != null) {
			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				// System.out.println(contactId);
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);
				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						// System.out.println(data1 + mimetype);
						if ("vnd.android.cursor.item/name".equals(mimetype)) {
							map.put("name", data1);
						}
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						}
					}
					list.add(map);
					dataCursor.close();
				}
			}
			rawContactsCursor.close();
		}
		return list;
	}

}
