package com.example.ceandroid;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * About page - accessed via Preferences
 * 
 * @author CEandroid SMU
 * 
 */
public class About extends Fragment {
	/**
	 * About Page is a fragment within Preferences
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_about, container, false);
	}
}