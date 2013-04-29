package com.example.ceandroid;

import android.app.Application;

/**
 * CEapp stores all globally accessible information for the application
 * 
 * @author CEandroid SMU
 */
public class CEapp extends Application {
	/**
	 * The List Type is used to determine which list data should be retrieved at
	 * any given time. 1:My Rules 2:Causes 3:Effects
	 */
	private int listType = 0;
	/**
	 * Stores information on the rule currently being created
	 */
	public Rule currentRule = null;
	/**
	 * Stores information of the position of an item selected from a List
	 */
	private int itemSelected = 0;
	/**
	 * Stores whether a cause or effect is currently being edited
	 */
	public boolean edit = false;
	/**
	 * Stores the Identifier for edit modes
	 */
	int editID = 0;
	/**
	 * Stores whether the edit type is to influence application logic
	 */
	boolean editType = true;
	/**
	 * Stores whether a rCause has been added, so that ANDs and ORs can be
	 * updated
	 */
	public boolean addedRCause = false;
	/**
	 * Stores the edited number of items to update in the database
	 */
	protected int editedNumber;
	/**
	 * Temporary Variable
	 */
	private int temp = 0;

	/**
	 * Get listType
	 * 
	 * @return this.listType
	 */
	public int getListType() {
		return listType;
	}

	/**
	 * Set listType
	 * 
	 * @param listType
	 */
	public void setListType(int listType) {
		this.listType = listType;
	}

	/**
	 * Get itemSelected
	 * 
	 * @return this.itemSelected
	 */
	public int getItemSelected() {
		return itemSelected;
	}

	/**
	 * Set itemSelected
	 * 
	 * @param itemSelected
	 */
	public void setItemSelected(int itemSelected) {
		this.itemSelected = itemSelected;
	}

	/**
	 * Get temp
	 * 
	 * @return this.temp
	 */
	public int getTemp() {
		return temp;
	}

	/**
	 * Set temp
	 * 
	 * @param temp
	 */
	public void setTemp(int temp) {
		this.temp = temp;
	}
}