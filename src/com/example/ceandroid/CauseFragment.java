package com.example.ceandroid;

import java.util.ArrayList;

import CEapi.rCause;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CauseFragment extends ListFragment {

	/**
	 * CEapp contains the globally accessible variables
	 */
	private CEapp app;

	/**
	 * Activity this view is associated with.
	 */
	private FragmentActivity mCallback;

	/**
	 * Generic size variable
	 */
	static int size = 0;

	/**
	 * The position of the rCause or rEffect that is to be deleted
	 */
	static int delPos = 0;

	/**
	 * The list of rCauses
	 */
	ArrayList<EditRuleNode> cList = new ArrayList<EditRuleNode>();

	/**
	 * The list of ANDs and ORs for rCauses
	 */
	ArrayList<EditRuleNode> bools = new ArrayList<EditRuleNode>();

	/**
	 * The current rCause being updated
	 */
	ArrayAdapter<EditRuleNode> cA;

	/**
	 * The ListView containing all of the rCauses
	 */
	ListView cListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cA = new ArrayAdapter<EditRuleNode>(mCallback,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, cList);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (FragmentActivity) activity;
		app = (CEapp) activity.getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cause,
				container, false);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		cListView = getListView();
		cListView.setAdapter(cA);
		setListeners();
	}

	private void setListeners() {
		// List Click Adapters
		cListView.setOnItemClickListener(new OnItemClickListener() {
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
					Intent myIntent = new Intent(mCallback, CauseView.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				} else if (comp.isBool()) {
					DatabaseHandler db = new DatabaseHandler(mCallback);
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
					Intent myIntent = new Intent(mCallback, CauseView.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(myIntent);
				}
			}
		});
		cListView.setOnItemLongClickListener(new OnItemLongClickListener() {
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
					newFragment.show(mCallback.getSupportFragmentManager(),
							"delete");
				}
				return true;
			}
		});

		populateCauses();
	}

	public void populateCauses() {
		bools = app.currentRule.getBoolSequence();
		if (app.edit) // editing a cause or effect
		{
			DatabaseHandler db = new DatabaseHandler(mCallback);
			if (app.editType) // Cause
			{
				size = app.currentRule.getRCauses().size();
				bools = app.currentRule.getBoolSequence();

				// added check - if > 0 then can't get negative index
				if (size > 0) {
					app.currentRule.getRCauses()
							.get(app.currentRule.getRCauses().size() - 1)
							.setID(app.editedNumber);
					db.updateRCause(
							app.currentRule.getRCauses().get(
									app.currentRule.getRCauses().size() - 1),
							app.currentRule);
					if (db.duplicateRCauses(app.currentRule)) {
						size = app.currentRule.getRCauses().size();
						db.deleteRCause(db.getRCauseByID(app.editedNumber));
					}
				}
			}
			app.currentRule
					.setRCauses(db.getAllRCauses(app.currentRule.getID()));
			app.currentRule.setREffects(db.getAllREffects(app.currentRule
					.getID()));

			// remove bools until right amount in rule
			while (app.currentRule.getRCauses().size() - bools.size() != 1
					&& !bools.isEmpty()) {
				bools.remove(bools.size() - 1);
			}
			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
			db.close();
		}

		if (app.currentRule.edited) // general adding of causes or effects
		{
			DatabaseHandler db = new DatabaseHandler(mCallback);
			bools = app.currentRule.getBoolSequence();
			if (db.updateRule(app.currentRule) > 0) {
				app.currentRule.setID(db.getAllRules()
						.get(db.getRulesCount() - 1).getID());
			}
			app.currentRule
					.setRCauses(db.getAllRCauses(app.currentRule.getID()));
			app.currentRule.setREffects(db.getAllREffects(app.currentRule
					.getID()));

			// add bools if rCause added
			if (app.addedRCause && !app.edit
					&& (size != app.currentRule.getRCauses().size())) {
				bools.add(new EditRuleNode("OR", false, true));
				size = app.currentRule.getRCauses().size();
				app.addedRCause = false;
			}

			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
			app.currentRule.edited = false;
			app.edit = false;
			db.close();
		}

		// clear bools if only 1 cause
		if (app.currentRule.getRCauses().size() <= 1) {
			bools.clear();
		}

		DatabaseHandler db = new DatabaseHandler(mCallback);
		if (app.currentRule != null) {
			app.currentRule.updateTreeData(bools);
			db.updateRule(app.currentRule);
			bools = app.currentRule.getBoolSequence();
		}
		db.close();

		// add everything to lists
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
	
	public void dialogDelete(FragmentActivity activity, DialogFragment dialog) {
		mCallback = activity;
		app = (CEapp) mCallback.getApplication();
		DatabaseHandler db = new DatabaseHandler(mCallback);
		if (app.editType) // cause
		{
			db.deleteRCause(db.getRCauseByID(app.editedNumber));
			if (!bools.isEmpty() && bools.size() == delPos) {
				bools.remove(bools.size() - 1);
			} else if (!bools.isEmpty()) {
				bools.remove(delPos);
			}
		} 
		app.currentRule.setRCauses(db.getAllRCauses(app.currentRule.getID()));
		app.currentRule.updateTreeData(bools);
		bools = app.currentRule.getBoolSequence();
	}
}
