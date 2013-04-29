package com.example.ceandroid;

/**
 * Custom object used for AND/OR list manipulation in EditRule
 * 
 * @author CEandroid SMU
 * 
 */
public class EditRuleNode {
	/** Can be the rCause name, boolean value ("AND"/"OR"), or "+" */
	String value;

	/** Flag to represent what kind of node this is (rCause, boolean, or +) */
	boolean cFlag, bFlag;

	/**
	 * Constructor for node
	 * 
	 * @param value
	 *            rCause name, "AND"/"OR", or "+"
	 * @param cFlag
	 *            cause flag, true if rCause node (set bFlag to false, set both
	 *            to false if +)
	 * @param bFlag
	 *            boolean flag, true if bool node (set cFlag to false, set both
	 *            to false if +)
	 */
	public EditRuleNode(String value, boolean cFlag, boolean bFlag) {
		this.value = value;
		this.cFlag = cFlag;
		this.bFlag = bFlag;
	}

	/**
	 * @return true if cause node, false if bool node
	 */
	public boolean isCause() {
		return cFlag;
	}

	/**
	 * @return true if bool node, false if cause node
	 */
	public boolean isBool() {
		return bFlag;
	}

	/**
	 * @return true if plus node
	 */
	public boolean isPlus() {
		if (cFlag == false && bFlag == false) {
			return true;
		} else
			return false;
	}

	/**
	 * Returns this.value
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.value;
	}
}
