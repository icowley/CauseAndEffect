package com.example.ceandroid;

import java.util.ArrayList;
import java.util.List;

import CEapi.rEffect;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * Used for rule evaluation. Cycles through returned rules and calls
 * ActionExecuter if a rule returns true.
 * 
 * @author CEandroid SMU
 * 
 */
public class RulesEngine extends Service {

	/** List for holding rules fetched from the database */
	private List<Rule> rules;

	/** Represents the type of causes needing to be fetched */
	private String type;

	/** Needed for database handler creation */
	private Context context;

	/**
	 * Checks a rule's causes using AsyncTask
	 * 
	 * @author CEandroid SMU
	 * 
	 */
	private class CauseChecker extends AsyncTask<String, Void, Boolean> {
		/** Used for fetching effects for a rule after it returns true */
		private int ruleID;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			ExpressionTree tree = new ExpressionTree(params[0], context);
			this.ruleID = Integer.parseInt(params[1]);
			return tree.evaluate(context);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				// pass context and effect list to Tomin's code
				DatabaseHandler db = new DatabaseHandler(context);
				ArrayList<rEffect> rEffects = db.getAllREffects(ruleID);
				if (!rEffects.isEmpty()) {
					ActionExecuter ae = new ActionExecuter();
					ae.executeActions(context, rEffects);
				}
				db.close();
			}
		}
	}

	/**
	 * Default constructor
	 * 
	 * @param type
	 *            type of rule to be evaluated
	 * @param c
	 *            context that allows access to application resources
	 */
	public RulesEngine(String type, Context c) {
		this.type = type;
		this.context = c;
	}

	/**
	 * Tells the RulesEngine to grab all rules of a certain type and check if
	 * their causes return true
	 */
	public void start() {
		DatabaseHandler db = new DatabaseHandler(context);
		rules = db.getAllRulesByType(type);
		db.close();
		String sequence = "";
		for (Rule r : rules) {
			if (r.getActive() == true) {
				sequence = r.getTreeData();
				CauseChecker c = new CauseChecker();
				c.execute(sequence, "" + r.getID());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}