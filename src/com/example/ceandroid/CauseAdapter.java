package com.example.ceandroid;

import java.util.List;

import CEapi.Cause;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CauseAdapter extends ArrayAdapter<String> {
	public enum cEnum {
		time, phoneCall, textMessage, ssid, wifiStatus, location
	}
	private String[] Elements;
	private List<Cause> causes;

	private int[] mIcons = { R.drawable.icon_clock, R.drawable.icon_location,
			R.drawable.icon_message, R.drawable.icon_phone,
			R.drawable.icon_wifi };

	public CauseAdapter(Context context, List<Cause> values) {
		super(context, android.R.layout.simple_list_item_activated_1);
		causes = values;
		Elements = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			Elements[i] = values.get(i).getName();
		}
		addAll(Elements);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = super.getView(position, convertView, parent);
		TextView text = (TextView) listItem.findViewById(android.R.id.text1);
		int iconIndex = 0;
		switch (cEnum.valueOf(causes.get(position).getType())) {
		case time: {
			iconIndex = 0;
			break;
		}
		case phoneCall: {
			iconIndex = 3;
			break;
		}
		case textMessage: {
			iconIndex = 2;
			break;
		}
		case ssid:
		case wifiStatus: {
			iconIndex = 4;
			break;
		}
		case location: {
			iconIndex = 1;
			break;
		}
		}
		text.setCompoundDrawablesWithIntrinsicBounds(mIcons[iconIndex], 0, 0, 0);
		text.setCompoundDrawablePadding(20);
		return listItem;
	}

}
