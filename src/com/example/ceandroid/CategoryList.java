package com.example.ceandroid;

import java.util.ArrayList;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Category List Fragment This class is used to organize Rules/Causes/Effects by
 * Category Currently not being used
 * 
 * @author CEandroid SMU
 */
public class CategoryList extends Fragment {

	/**
	 * Category List as the parent activity creates this fragment
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		TextView textview = new TextView(this.getActivity());
		textview.setText("This is the Category tab");

		DatabaseHandler db = new DatabaseHandler(this.getActivity());
		try {
			ArrayList<String> categories = db.getAllCauseCategories();
			ArrayList<Rule> rule = db.getAllRules();
			for (int i = 0; i < rule.size(); i++) {
				Log.d("rule " + i, "" + rule.get(i).getName());
			}
			for (int i = 0; i < categories.size(); i++) {
				ArrayList<Rule> rules = db.getAllRulesByCategory(categories
						.get(i));
				Log.d(categories.get(i), "" + rules.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();

		getActivity().setContentView(textview);
	}
}
