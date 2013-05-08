package com.example.ceandroid;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CauseFragment extends ListFragment {
	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app = (CEapp) getActivity().getApplication();

	/**
	 * The list of rCauses
	 */
	ArrayList<EditRuleNode> cList = new ArrayList<EditRuleNode>();

	/**
	 * The list of ANDs and ORs for rCauses
	 */
	ArrayList<EditRuleNode> bools = new ArrayList<EditRuleNode>();

	/**
	 * The position of the rCause or rEffect that is to be deleted
	 */
	static int delPos = 0;

	/**
	 * Generic size variable
	 */
	static int size = 0;

	/**
	 * The current rCause being updated
	 */
	ArrayAdapter<EditRuleNode> cA;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create Lists
		cA = new ArrayAdapter<EditRuleNode>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, cList);
		setListAdapter(cA);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			/**
			 * Cause List Item Clicked Calls the CauseView page for this Cause
			 * 
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
			 *      android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				EditRuleNode comp = (EditRuleNode) arg0.getAdapter().getItem(
						arg2);
				size = app.currentRule.getRCauses().size();
				// int size = arg0.getAdapter().getCount();
				app.editType = true;
				// if(size == (arg2+1))
				if (comp.isPlus()) {
					app.edit = false;
					Intent myIntent = new Intent(CauseFragment.this
							.getActivity(), CauseView.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				} else if (comp.isBool()) {
					DatabaseHandler db = new DatabaseHandler(CauseFragment.this
							.getActivity());
					if (comp.value.equals("OR")) {
						comp.value = "AND";
						bools.get(arg2 / 2).value = "AND";
						app.currentRule.updateTreeData(bools);
						cA.notifyDataSetChanged();
					} else {
						comp.value = "OR";
						bools.get(arg2 / 2).value = "OR";
						app.currentRule.updateTreeData(bools);
						cA.notifyDataSetChanged();
					}
					db.updateRule(app.currentRule);
					db.close();
				} else {
					delPos = arg2 / 2;
					app.edit = true;
					app.editID = convertTypeToCEnum(app.currentRule
							.getRCauses().get(arg2 / 2).getType(),
							app.currentRule.getRCauses().get(arg2 / 2)
									.getCauseID());
					app.editedNumber = app.currentRule.getRCauses()
							.get(arg2 / 2).getID();
					Intent myIntent = new Intent(getActivity(), CauseView.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			/**
			 * Called when a Cause has a long tap Delete dialog is called where
			 * appropriate
			 * 
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView,
			 *      android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// int size = arg0.getAdapter().getCount();
				EditRuleNode comp = (EditRuleNode) arg0.getAdapter().getItem(
						arg2);
				// if(size == (arg2+1))
				if (comp.isCause()) {
					app.editType = true;
					delPos = arg2 / 2;
					app.editedNumber = app.currentRule.getRCauses()
							.get(arg2 / 2).getID();
					DialogFragment newFragment = new DeleteDialogFragment();
					newFragment.show(getFragmentManager(), "delete");
				}
				return true;
			}
		});
	}

	/**
	 * Enum conversions for rCauses in the list
	 * 
	 * @param type
	 * @return int The type of rCause
	 */
	public int convertTypeToCEnum(String type, int cid) {
		if (type.equals("location")) {
			if (cid == 6)
				return 0;
			else
				return 1;
		}
		if (type.equals("phoneCall"))
			return 2;
		if (type.equals("textMessage"))
			return 3;
		if (type.equals("time"))
			return 4;
		if (type.equals("ssid"))
			return 5;
		if (type.equals("wifiStatus"))
			return 6;
		return -1;
	}

}
