package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.rCause;
import android.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CauseFragment extends ListFragment  implements
DeleteDialogFragment.DeleteDialogListener {
	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app;

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
		app = (CEapp) getActivity().getApplication();
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
					newFragment.show(getSupportFragmentManager(), "delete");
				}
				return true;
			}
		});

		// Add the causes to the list.
		for (int i = 0; i < app.currentRule.getRCauses().size(); i++) {
			rCause r = app.currentRule.getRCauses().get(i);

			if (!r.getType().equals("location")) {
				cList.add(new EditRuleNode(r.getName() + "\n"
						+ r.getParameters(), true, false));
			} else {
				String param = r.getParameters();
				String finalParam = "";
				char cur = ' ';
				int start = 0;
				while (cur != '\n') {
					cur = param.charAt(start);
					finalParam += cur;
					start++;
				}
				cList.add(new EditRuleNode(r.getName() + "\n" + finalParam,
						true, false));
			}

			if (i < bools.size() && !bools.isEmpty()) {
				cList.add(bools.get(i));
			}
		}
		
		cList.add(new EditRuleNode("+", false, false));
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
	
	/**
	 * This is called when a delete of a rCause or rEffect is confirmed
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragment.DeleteDialogListener#onDialogPositiveClick(android.app.DialogFragment)
	 */
	public void onDialogPositiveClick(DialogFragment dialog) {
		// clicked to delete the cause or effect
		DatabaseHandler db = new DatabaseHandler(getActivity());
		if (app.editType) // cause
		{
			db.deleteRCause(db.getRCauseByID(app.editedNumber));
			if (!bools.isEmpty() && bools.size() == delPos) {
				bools.remove(bools.size() - 1);
			} else if (!bools.isEmpty()) {
				bools.remove(delPos);
			}
		} else // effect
		{
			db.deleteREffect(db.getREffectByID(app.editedNumber));
		}
		app.currentRule.setRCauses(db.getAllRCauses(app.currentRule.getID()));
		app.currentRule.setREffects(db.getAllREffects(app.currentRule.getID()));
		app.currentRule.updateTreeData(bools);
		bools = app.currentRule.getBoolSequence();
		db.close();
		getActivity().finish();
		startActivity(getActivity().getIntent());
	}

	/**
	 * This is called when the delete of a rCause or rEffect is cancelled
	 * 
	 * @see com.example.ceandroid.DeleteDialogFragment.DeleteDialogListener#onDialogNegativeClick(android.app.DialogFragment)
	 */
	public void onDialogNegativeClick(DialogFragment dialog) {
		// clicked cancel, do not do anything
	}

}
