package com.example.ceandroid;

import java.util.ArrayList;
import java.util.List;

import CEapi.rCause;
import CEapi.rEffect;

/**
 * The Rule classic is the underlying data structure for the entire program
 * Causes and Effects, as well as other rule information, is stored within this
 * object
 * 
 * @author CEandroid SMU
 */
public class Rule {

	/**
	 * The Rule's Unique Identifier
	 */
	int _id;
	/**
	 * _treeData Stores the boolean logic and Causes in a String _name Stores
	 * the Rule's name
	 */
	String _treeData, _name;
	/**
	 * Contains the List of rCauses
	 */
	ArrayList<rCause> _ruleCauses;
	/**
	 * Contains the List of rEffects
	 */
	ArrayList<rEffect> _ruleEffects;
	/**
	 * Stores whether or not the rule is currently active
	 */
	boolean Active;
	/**
	 * Stores whether or not the rule is edited and needs to be updated in the
	 * Database
	 */
	boolean edited = false;

	/**
	 * Default Constructor Creates a new rule
	 */
	public Rule() {
		this._ruleCauses = new ArrayList<rCause>();
		this._ruleEffects = new ArrayList<rEffect>();
		Active = true;
		edited = false;
		this._treeData = "";
		this._name = "Untitled";
	}

	/**
	 * Defined Rule Constructor without the Rule's ID, rCauses, and rEffects
	 * 
	 * @param treeData
	 * @param name
	 * @param Active
	 */
	public Rule(String treeData, String name, boolean Active) {
		this._treeData = treeData;
		if (name == null)
			this._name = "Untitled";
		else
			this._name = name;
		this.Active = Active;
		this._ruleCauses = null;
		this._ruleEffects = null;
		this._ruleCauses = new ArrayList<rCause>();
		this._ruleEffects = new ArrayList<rEffect>();
		edited = false;
	}

	/**
	 * Defined Rules Constructor without the Rule's ID
	 * 
	 * @param treeData
	 * @param name
	 * @param ruleCauses
	 * @param ruleEffects
	 */
	public Rule(String treeData, String name, ArrayList<rCause> ruleCauses,
			ArrayList<rEffect> ruleEffects) {
		this._treeData = treeData;
		if (name == null)
			this._name = "Untitled";
		else
			this._name = name;
		this.Active = true;
		this._ruleCauses = ruleCauses;
		this._ruleEffects = ruleEffects;
		edited = false;
	}

	/**
	 * Get _id
	 * 
	 * @return this._id
	 */
	public int getID() {
		return this._id;
	}

	/**
	 * Set _id
	 * 
	 * @param id
	 */
	public void setID(int id) {
		this._id = id;
	}

	/**
	 * Get _treeData
	 * 
	 * @return this._treeData
	 */
	public String getTreeData() {
		return this._treeData;
	}

	/**
	 * Set _treeData
	 * 
	 * @param treeData
	 */
	public void setTreeData(String treeData) {
		this._treeData = treeData;
	}

	/**
	 * Get's the BooleanSequence used for rCauses
	 * 
	 * @return ArrayList<EditRuleNode> The list of Booleans
	 */
	public ArrayList<EditRuleNode> getBoolSequence() {
		ArrayList<EditRuleNode> bools = new ArrayList<EditRuleNode>();
		int cCounter = 0;
		boolean aoFlag = false;
		ArrayList<Integer> andLocs = new ArrayList<Integer>();
		char cur = ' ';

		for (int i = 0; i < this._treeData.length(); i++) {
			cur = this._treeData.charAt(i);

			if (cur == '+' || cur == '&') {
				bools.add(new EditRuleNode("", false, true));
				if (cur == '&') {
					andLocs.add(cCounter - 2);
				}
				aoFlag = true;
			} else if (cur == ',') {
				if (aoFlag) {
					aoFlag = false;
				} else {
					cCounter++;
				}
			}
		}

		for (int i = 0; i < andLocs.size(); i++) {
			int loc = andLocs.get(i);
			bools.get(loc).value = "AND";
		}

		for (int i = 0; i < bools.size(); i++) {
			if (!bools.get(i).value.equals("AND")) {
				bools.get(i).value = "OR";
			}
		}

		return bools;
	}

	/**
	 * Updates the the treeData using a new list of booleans
	 * 
	 * @param bools
	 */
	public void updateTreeData(List<EditRuleNode> bools) {
		String tree = "";
		rCause cur;
		boolean andFlag = false;
		if (_ruleCauses.size() > 0) {
			if (bools.isEmpty()) {
				for (int i = 0; i < _ruleCauses.size(); i++) {
					tree += Integer.toString(_ruleCauses.get(i).getID());
				}
			} else {
				for (int i = 0; i < _ruleCauses.size(); i++) {
					cur = _ruleCauses.get(i);
					if (i == bools.size()) {
						tree += Integer.toString(cur.getID()) + ',';
						if (andFlag == true) {
							tree += "&,";
							andFlag = false;
						}
					} else {
						if (andFlag == true) {
							tree += (cur.getID() + ",&,");
							if (i < bools.size()
									&& bools.get(i).value.equals("OR")) {
								andFlag = false;
							}
						}

						else if (i < bools.size()
								&& bools.get(i).value.equals("AND")) {
							tree += Integer.toString(cur.getID()) + ',';
							andFlag = true;
						}

						else if (i < bools.size()
								&& bools.get(i).value.equals("OR")) {
							tree += Integer.toString(cur.getID()) + ',';
							andFlag = false;
						}
					}
				}

				for (int i = 0; i < bools.size(); i++) {
					if (bools.get(i).value.equals("OR")) {
						tree += "+,";
					}
				}
			} // end of else

			// dollar sign represents end of string
			tree += "$";
		}

		System.out.println("Tree is " + tree);
		setTreeData(tree);
	}

	/**
	 * Get _name
	 * 
	 * @return this._name
	 */
	public String getName() {
		return this._name;
	}

	/**
	 * Set _name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this._name = name;
		edited = true;
	}

	/**
	 * Get Active
	 * 
	 * @return this.Active
	 */
	public boolean getActive() {
		return this.Active;
	}

	/**
	 * Set Active
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		this.Active = active;
		edited = true;
	}

	/**
	 * Set _ruleCauses
	 * 
	 * @param ruleCauses
	 */
	public void setRCauses(ArrayList<rCause> ruleCauses) {
		this._ruleCauses = ruleCauses;
	}

	/**
	 * Add rCause to _ruleCauses
	 * 
	 * @param cause
	 */
	public void addRCause(rCause cause) {
		this._ruleCauses.add(cause);
		edited = true;
	}

	/**
	 * Get _ruleCauses
	 * 
	 * @return this._ruleCauses
	 */
	public ArrayList<rCause> getRCauses() {
		return this._ruleCauses;
	}

	/**
	 * Set _ruleEffects
	 * 
	 * @param ruleEffects
	 */
	public void setREffects(ArrayList<rEffect> ruleEffects) {
		this._ruleEffects = ruleEffects;
	}

	/**
	 * Add rEffect to _ruleEffects
	 * 
	 * @param effect
	 */
	public void addREffect(rEffect effect) {
		this._ruleEffects.add(effect);
		edited = true;
	}

	/**
	 * Get _ruleEffects
	 * 
	 * @return this._ruleEffects
	 */
	public ArrayList<rEffect> getREffects() {
		return this._ruleEffects;
	}
}