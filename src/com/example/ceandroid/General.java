package com.example.ceandroid;

import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * "General settings" page
 * 
 * @author CEandroid SMU
 */
public class General extends Fragment {
	/**
	 * General Settings Page is a fragment within Preferences
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_general, container, false);
		ListView hList = (ListView) v.findViewById(R.id.genList);
		// List Click Adapters
		hList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getActivity().getApplicationContext(),
						"General Options not implemented", Toast.LENGTH_SHORT)
						.show();
			}
		});
		return v;
	}
}