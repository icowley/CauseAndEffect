package com.example.ceandroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import CEapi.Cause;
import CEapi.Effect;
import CEapi.rCause;
import CEapi.rEffect;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Database class used for everything Database-related
 * 
 * @author CEandroid SMU
 * 
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * Context variable for holding the context of the application
	 */
	private Context context;

	/**
	 * Database version number
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Database name
	 */
	private static final String DATABASE_NAME = "CEdb";

	/**
	 * The Location of the Database on the Device
	 */
	@SuppressLint("SdCardPath")
	private static final String DB_FILEPATH = "/data/data/com.example.ceandroid/databases/database.db";

	/**
	 * Rules table name
	 * 
	 * @see Rule
	 */
	private static final String TABLE_RULES = "Rules";
	/**
	 * Causes table name
	 * 
	 * @see Cause
	 */
	private static final String TABLE_CAUSES = "Causes";
	/**
	 * Effects table name
	 * 
	 * @see Effect
	 */
	private static final String TABLE_EFFECTS = "Effects";
	/**
	 * rCauses table name
	 * 
	 * @see rCause
	 */
	private static final String TABLE_R_CAUSES = "rCauses";
	/**
	 * rEffects table name
	 * 
	 * @see rEffect
	 */
	private static final String TABLE_R_EFFECTS = "rEffects";

	/**
	 * Rule ID column
	 * 
	 * @see Rule#_id
	 * @see rCause#_ruleID
	 * @see rEffect#_ruleID
	 */
	private static final String KEY_RULE_ID = "ruleID";
	/**
	 * Tree Data column
	 * 
	 * @see Rule#_treeData
	 */
	private static final String KEY_TREE_DATA = "treeData";
	/**
	 * Rule name column
	 * 
	 * @see Rule#_name
	 */
	private static final String KEY_RULE_NAME = "ruleName";
	/**
	 * Rule active column
	 * 
	 * @see Rule#Active
	 */
	private static final String KEY_RULE_ACTIVE = "ruleActive";

	/**
	 * Cause ID column
	 * 
	 * @see Cause#_id
	 * @see rCause#_causeID
	 */
	private static final String KEY_CAUSE_ID = "causeID";
	/**
	 * Cause type column
	 * 
	 * @see Cause#_type
	 */
	private static final String KEY_CAUSE_TYPE = "causeType";
	/**
	 * Cause name column
	 * 
	 * @see Cause#_name
	 */
	private static final String KEY_CAUSE_NAME = "causeName";
	/**
	 * Cause description column
	 * 
	 * @see Cause#_description
	 */
	private static final String KEY_CAUSE_DESCRIPTION = "causeDescription";
	/**
	 * Cause category column
	 * 
	 * @see Cause#_category
	 */
	private static final String KEY_CAUSE_CATEGORY = "causeCategory";

	/**
	 * Effect ID column
	 * 
	 * @see Effect#_id
	 * @see rEffect#_effectID
	 */
	private static final String KEY_EFFECT_ID = "effectID";
	/**
	 * Effect type column
	 * 
	 * @see Effect#_type
	 */
	private static final String KEY_EFFECT_TYPE = "effectType";
	/**
	 * Effect name column
	 * 
	 * @see Effect#_name
	 */
	private static final String KEY_EFFECT_NAME = "effectName";
	/**
	 * Effect description column
	 * 
	 * @see Effect#_description
	 */
	private static final String KEY_EFFECT_DESCRIPTION = "effectDescription";
	/**
	 * Effect category column
	 * 
	 * @see Effect#_category
	 */
	private static final String KEY_EFFECT_CATEGORY = "effectCategory";

	/**
	 * rCause ID column
	 * 
	 * @see rCause#_id
	 */
	private static final String KEY_R_CAUSE_ID = "rCauseID";
	/**
	 * rCause parameter column
	 * 
	 * @see rCause#_parameters
	 */
	private static final String KEY_R_CAUSE_PARAMETER = "causeParameter";
	/**
	 * rCause rule ID column
	 * 
	 * @see rCause#_ruleID
	 */
	private static final String KEY_R_CAUSE_RULE_ID = "rCauseRuleID";

	/**
	 * rEffect ID column
	 * 
	 * @see rEffect#_id
	 */
	private static final String KEY_R_EFFECT_ID = "rEffectID";
	/**
	 * rEffect parameter column
	 * 
	 * @see rEffect#_parameters
	 */
	private static final String KEY_R_EFFECT_PARAMETER = "effectParameter";
	/**
	 * rEffect rule ID column
	 * 
	 * @see rEffect#_ruleID
	 */
	private static final String KEY_R_EFFECT_RULE_ID = "rEffectRuleID";

	/**
	 * Copies the database file at the specified location over the current
	 * internal application database.
	 * */
	public boolean importDatabase(String dbPath) throws IOException {

		// Close the SQLiteOpenHelper so it will commit the created empty
		// database to internal storage.
		close();
		File newDb = new File(dbPath);
		File oldDb = new File(DB_FILEPATH);
		if (newDb.exists()) {
			FileUtils.copyFile(new FileInputStream(newDb),
					new FileOutputStream(oldDb));
			// Access the copied database so SQLiteHelper will cache it and mark
			// it as created.
			getWritableDatabase().close();
			return true;
		}

		return false;
	}

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context, database name, and
	 * database version.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	/**
	 * Creating Tables
	 * 
	 * Runs all of the SQLite instructions to create all of the tables.
	 * 
	 * @param db
	 *            current installation of SQLiteDatabase
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_RULES_TABLE = "CREATE TABLE " + TABLE_RULES + " ("
				+ KEY_RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_TREE_DATA + " TEXT, " + KEY_RULE_NAME + " TEXT, "
				+ KEY_RULE_ACTIVE + " BOOLEAN" + ");";
		String CREATE_CAUSES_TABLE = "CREATE TABLE " + TABLE_CAUSES + " ("
				+ KEY_CAUSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_CAUSE_TYPE + " TEXT, " + KEY_CAUSE_NAME + " TEXT, "
				+ KEY_CAUSE_DESCRIPTION + " TEXT, " + KEY_CAUSE_CATEGORY
				+ " TEXT" + ");";
		String CREATE_EFFECTS_TABLE = "CREATE TABLE " + TABLE_EFFECTS + " ("
				+ KEY_EFFECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_EFFECT_TYPE + " TEXT, " + KEY_EFFECT_NAME + " TEXT, "
				+ KEY_EFFECT_DESCRIPTION + " TEXT, " + KEY_EFFECT_CATEGORY
				+ " TEXT" + ");";
		String CREATE_R_CAUSES_TABLE = "CREATE TABLE " + TABLE_R_CAUSES + "("
				+ KEY_R_CAUSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_R_CAUSE_RULE_ID + " INT NOT NULL, " + KEY_CAUSE_ID
				+ " INT NOT NULL, " + KEY_R_CAUSE_PARAMETER + " TEXT, "
				+ "FOREIGN KEY(" + KEY_R_CAUSE_RULE_ID + ") REFERENCES "
				+ TABLE_RULES + "(" + KEY_RULE_ID + ")" + ");";
		String CREATE_R_EFFECTS_TABLE = "CREATE TABLE " + TABLE_R_EFFECTS + "("
				+ KEY_R_EFFECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_R_EFFECT_RULE_ID + " INT NOT NULL, " + KEY_EFFECT_ID
				+ " INT NOT NULL, " + KEY_R_EFFECT_PARAMETER + " TEXT, "
				+ "FOREIGN KEY(" + KEY_R_EFFECT_RULE_ID + ") REFERENCES "
				+ TABLE_RULES + "(" + KEY_RULE_ID + ")" + ");";
		db.execSQL(CREATE_RULES_TABLE);
		db.execSQL(CREATE_CAUSES_TABLE);
		db.execSQL(CREATE_EFFECTS_TABLE);
		db.execSQL(CREATE_R_CAUSES_TABLE);
		db.execSQL(CREATE_R_EFFECTS_TABLE);
	}

	/**
	 * Upgrading Database
	 * 
	 * The method used to upgrade the database if needed.
	 * 
	 * @param db
	 *            current running SQLiteDatabase
	 * @param oldVersion
	 *            the old version database number
	 * @param newVersion
	 *            the new version database number
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_CAUSES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_EFFECTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAUSES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EFFECTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Add a rule to the Database
	 * 
	 * Adds a rule to the Database that is passed in.
	 * 
	 * @param rule
	 *            the rule to be added to the database
	 * @see Rule
	 */
	public void addRule(Rule rule) {
		if (!ruleExists(rule)) {
			ContentValues ruleValues = new ContentValues();
			// ContentValues rCauseValues = new ContentValues();
			// ContentValues rEffectValues = new ContentValues();
			ruleValues.put(KEY_TREE_DATA, rule.getTreeData());
			ruleValues.put(KEY_RULE_NAME, rule.getName());
			ruleValues.put(KEY_RULE_ACTIVE, true);
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_RULES, null, ruleValues);
			rule.setID(getAllRules().get(getRulesCount() - 1).getID());
			System.out.println("Rule id is " + rule.getID());
			db.close(); // Closing database connection
			for (int i = 0; i < rule.getRCauses().size(); i++) {
				addRCause(rule.getRCauses().get(i), rule);
			}
			// rEffectValues.put(KEY_R_EFFECT_RULE_ID, getRulesCount() + 1);
			for (int i = 0; i < rule.getREffects().size(); i++) {
				addREffect(rule.getREffects().get(i), rule);
			}
		} else {
			Toast.makeText(null, "Rule already exists", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Get all of a rule's causes
	 * 
	 * Gets all of a rule's causes that is passed in.
	 * 
	 * @param rule
	 *            the rule to get the causes for
	 * @see Rule
	 * @return ArrayList<rCause> of rCause from given Rule
	 */
	public ArrayList<rCause> getRuleCauses(Rule rule) {
		ArrayList<rCause> causes = new ArrayList<rCause>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_ID
				+ ", " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_RULE_ID + ", "
				+ TABLE_R_CAUSES + "." + KEY_CAUSE_ID + ", " + TABLE_R_CAUSES
				+ "." + KEY_R_CAUSE_PARAMETER + ", " + TABLE_CAUSES + "."
				+ KEY_CAUSE_TYPE + " FROM " + TABLE_R_CAUSES + " INNER JOIN "
				+ TABLE_CAUSES + " ON " + TABLE_R_CAUSES + "." + KEY_CAUSE_ID
				+ "=" + TABLE_CAUSES + "." + KEY_CAUSE_ID + " WHERE "
				+ KEY_R_CAUSE_RULE_ID + "=" + rule.getID();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				causes.add(new rCause(Integer.parseInt(cursor.getString(1)),
						Integer.parseInt(cursor.getString(2)), cursor
								.getString(3), cursor.getString(4)));
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();

		return causes;
	}

	/**
	 * Get all of a rule's effects
	 * 
	 * Gets all of a rule's effects that is passed in.
	 * 
	 * @param rule
	 *            the rule to get the effects for
	 * @see Rule
	 * @see rEffect
	 * @return ArrayList<rEffect> of rEffect from given Rule
	 */
	public ArrayList<rEffect> getRuleEffects(Rule rule) {
		ArrayList<rEffect> effects = new ArrayList<rEffect>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_RULE_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_PARAMETER + ", " + TABLE_EFFECTS + "."
				+ KEY_EFFECT_TYPE + " FROM " + TABLE_R_EFFECTS + " INNER JOIN "
				+ TABLE_EFFECTS + " ON " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + "=" + TABLE_EFFECTS + "." + KEY_EFFECT_ID
				+ " WHERE " + KEY_R_EFFECT_RULE_ID + "=" + rule.getID();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				effects.add(new rEffect(rule.getID(), Integer.parseInt(cursor
						.getString(2)), cursor.getString(3), cursor
						.getString(4)));
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();

		return effects;
	}

	/**
	 * Get a rule
	 * 
	 * Returns a rule given the rule ID.
	 * 
	 * @param id
	 *            the ID of the rule
	 * @see Rule
	 * @return Rule with given ID or null if not found
	 */
	public Rule getRule(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_RULES, new String[] { KEY_RULE_ID,
				KEY_TREE_DATA, KEY_RULE_NAME, KEY_RULE_ACTIVE }, KEY_RULE_ID
				+ "=?", new String[] { String.valueOf(id) }, null, null, null,
				null);
		if (cursor != null)
			cursor.moveToFirst();

		Rule rule;
		if (cursor.getString(3).equals("0")) {
			rule = new Rule(cursor.getString(1), cursor.getString(2), false);
			rule.setID(Integer.parseInt(cursor.getString(0)));
		} else {
			rule = new Rule(cursor.getString(1), cursor.getString(2), true);
			rule.setID(Integer.parseInt(cursor.getString(0)));
		}

		db.close();
		rule.setRCauses(this.getRuleCauses(rule));
		rule.setREffects(this.getRuleEffects(rule));
		return rule;
	}

	/**
	 * Get all rules
	 * 
	 * Returns all of the Database's rules
	 * 
	 * @see Rule
	 * @return ArrayList<Rule> of all Rule in Database
	 */
	public ArrayList<Rule> getAllRules() {
		ArrayList<Rule> ruleList = new ArrayList<Rule>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RULES + " ORDER BY "
				+ KEY_RULE_ID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Rule rule = new Rule();
				rule.setID(Integer.parseInt(cursor.getString(0)));
				rule.setTreeData(cursor.getString(1));
				rule.setName(cursor.getString(2));
				if (cursor.getString(3).equals("0")) {
					rule.setActive(false);
				} else {
					rule.setActive(true);
				}
				// Adding rule to list
				ruleList.add(rule);
				rule.edited = false;
			} while (cursor.moveToNext());
		}
		close();
		// return rule list
		return ruleList;
	}

	/**
	 * Get all rules of a certain type
	 * 
	 * Returns all of the Database's rules that have the type given.
	 * 
	 * @param type
	 *            the type of the rules you want returned
	 * @see Rule
	 * @see Cause#_type
	 * @return ArrayList<Rule> of Rule that have the type given
	 */
	public ArrayList<Rule> getAllRulesByType(String type) {
		ArrayList<Rule> ruleList = new ArrayList<Rule>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CAUSES + " WHERE "
				+ KEY_CAUSE_TYPE + "=?";
		Cursor cursor = db.rawQuery(selectQuery, new String[] { type });

		// looping through all rows and adding to list of ints (cause id's)
		List<Integer> tempList = new ArrayList<Integer>();

		try {
			if (cursor.moveToFirst()) {
				do {
					tempList.add(Integer.parseInt(cursor.getString(0)));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		List<Integer> tempList2 = new ArrayList<Integer>();
		for (int i = 0; i < tempList.size(); i++) {
			selectQuery = "SELECT * FROM " + TABLE_R_CAUSES + " WHERE "
					+ KEY_CAUSE_ID + "=?";
			cursor = db.rawQuery(selectQuery,
					new String[] { "" + tempList.get(i) });
			try {
				if (cursor.moveToFirst()) {
					do {
						if (!tempList2.contains(Integer.parseInt(cursor
								.getString(1))))
							tempList2
									.add(Integer.parseInt(cursor.getString(1)));
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}

		for (int i = 0; i < tempList2.size(); i++) {
			selectQuery = "SELECT * FROM " + TABLE_RULES + " WHERE "
					+ KEY_RULE_ID + "=? AND " + KEY_RULE_ACTIVE;
			cursor = db.rawQuery(selectQuery,
					new String[] { "" + tempList2.get(i) });
			try {
				if (cursor.moveToFirst()) {
					Rule rule = new Rule();
					rule.setID(Integer.parseInt(cursor.getString(0)));
					rule.setTreeData(cursor.getString(1));
					rule.setName(cursor.getString(2));
					if (cursor.getString(3).equals("0")) {
						rule.setActive(false);
					} else {
						rule.setActive(true);
					}
					// Adding rule to list
					ruleList.add(rule);
					rule.edited = false;
				}
			} finally {
				cursor.close();
			}
		}
		close();
		// return rule list
		return ruleList;
	}

	/**
	 * Get all rules of a certain category
	 * 
	 * Returns all of the Database's rules that have the category given.
	 * 
	 * @param category
	 *            the type of the rules you want returned
	 * @see Rule
	 * @see Cause#_category
	 * @return ArrayList<Rule> of Rule that have category given
	 */
	public ArrayList<Rule> getAllRulesByCategory(String category) {
		ArrayList<Rule> ruleList = new ArrayList<Rule>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CAUSES + " WHERE "
				+ KEY_CAUSE_CATEGORY + "=?"; // ORDER BY " + KEY_RULE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, new String[] { category });

		// looping through all rows and adding to list of ints (cause id's)
		List<Integer> tempList = new ArrayList<Integer>();
		if (cursor.moveToFirst()) {
			do {
				tempList.add(Integer.parseInt(cursor.getString(0)));
			} while (cursor.moveToNext());
		}

		List<Integer> tempList2 = new ArrayList<Integer>();
		for (int i = 0; i < tempList.size(); i++) {
			selectQuery = "SELECT * FROM " + TABLE_R_CAUSES + " WHERE "
					+ KEY_CAUSE_ID + "=?";
			cursor = db.rawQuery(selectQuery,
					new String[] { String.valueOf(tempList.get(i)) });
			if (cursor.moveToFirst()) {
				do {
					tempList2.add(Integer.parseInt(cursor.getString(1)));
				} while (cursor.moveToNext());
			}
		}

		for (int i = 0; i < tempList2.size(); i++) {
			selectQuery = "SELECT * FROM " + TABLE_RULES + " WHERE "
					+ KEY_RULE_ID + "=? AND " + KEY_RULE_ACTIVE;
			cursor = db.rawQuery(selectQuery,
					new String[] { String.valueOf(tempList2.get(i)) });
			if (cursor.moveToFirst()) {
				Rule rule = new Rule();
				rule.setID(Integer.parseInt(cursor.getString(0)));
				rule.setTreeData(cursor.getString(1));
				rule.setName(cursor.getString(2));
				if (cursor.getString(3).equals("0")) {
					rule.setActive(false);
				} else {
					rule.setActive(true);
				}
				// Adding rule to list
				ruleList.add(rule);
				rule.edited = false;
			}
		}
		close();
		// return rule list
		return ruleList;
	}

	/**
	 * Get the number of Rules
	 * 
	 * Returns the number of Rules in the Database.
	 * 
	 * @see Rule
	 * @return number of Rule objects in Database
	 */
	public int getRulesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_RULES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/**
	 * Get the number of rCauses
	 * 
	 * Returns the number of rCauses in the Database.
	 * 
	 * @see rCause
	 * @return returns number of rCause objects in Database
	 */
	public int getRCausesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_R_CAUSES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/**
	 * Get the number of rEffects
	 * 
	 * Returns the number of rEffects in the Database.
	 * 
	 * @see rEffect
	 * @return returns number of rEffect objects in Database
	 */
	public int getREffectsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_R_EFFECTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/**
	 * Update an rCause
	 * 
	 * Updates an rCause in the Database that correlates to the rCause and rule
	 * given. Returns the number of rCauses updated in the Database.
	 * 
	 * @param rc
	 *            the rCause to be updated
	 * @param r
	 *            the rule that correlates to the rCause
	 * @see rCause
	 * @see Rule
	 * @return returns number of rCauses updated
	 */
	public int updateRCause(rCause rc, Rule r) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues rCauseValues = new ContentValues();
		rCauseValues.put(KEY_R_CAUSE_RULE_ID, r.getID());
		rCauseValues.put(KEY_CAUSE_ID, rc.getCauseID());
		rCauseValues.put(KEY_R_CAUSE_PARAMETER, rc.getParameters());
		return db.update(TABLE_R_CAUSES, rCauseValues, KEY_R_CAUSE_ID + " = ?",
				new String[] { String.valueOf(rc.getID()) });
	}

	/**
	 * Update an rEffect
	 * 
	 * Updates an rEffect in the Database that correlates to the rEffect and
	 * rule given. Returns the number of rEffect updated in the Database.
	 * 
	 * @param re
	 *            the rEffect to be updated
	 * @param r
	 *            the rule that correlates to the rEffect
	 * @see rEffect
	 * @see Rule
	 * @return returns the number of rEffect updated
	 */
	public int updateREffect(rEffect re, Rule r) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues rEffectValues = new ContentValues();
		rEffectValues.put(KEY_R_EFFECT_RULE_ID, r.getID());
		rEffectValues.put(KEY_EFFECT_ID, re.getEffectID());
		rEffectValues.put(KEY_R_EFFECT_PARAMETER, re.getParameters());
		return db.update(TABLE_R_EFFECTS, rEffectValues, KEY_R_EFFECT_ID
				+ " = ?", new String[] { String.valueOf(re.getID()) });
	}

	/**
	 * Update a Rule
	 * 
	 * Updates a Rule in the Database. Returns the number of Rules updated in
	 * the Database.
	 * 
	 * @param rule
	 *            the Rule to be updated
	 * @see Rule
	 * @return returns the number of Rules updated
	 */
	public int updateRule(Rule rule) {
		int valuesChanged = 0;

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_TREE_DATA, rule.getTreeData());

		// updating row
		if (db.update(TABLE_RULES, values, KEY_RULE_ID + " = ?",
				new String[] { String.valueOf(rule.getID()) }) < 1) {
			addRule(rule);
			valuesChanged = 1;
		} else {
			ArrayList<rCause> causes = rule.getRCauses();
			for (int i = 0; i < causes.size(); i++) {
				if (updateRCause(causes.get(i), rule) < 1) {
					addRCause(causes.get(i), rule);
				}
			}
			ArrayList<rEffect> effects = rule.getREffects();
			for (int i = 0; i < effects.size(); i++) {
				if (updateREffect(effects.get(i), rule) < 1) {
					addREffect(effects.get(i), rule);
				}
			}
		}
		db.close();
		return valuesChanged;
	}

	/**
	 * Update a Rule's name
	 * 
	 * Updates an Rule's name in the Database. Returns the number of Rules
	 * updated in the Database.
	 * 
	 * @param ruleID
	 *            the ID of the Rule to be updated
	 * @param name
	 *            the new name of the Rule
	 * @see Rule
	 * @return returns the number of Rule updated
	 */
	public int updateName(int ruleID, String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues ruleValues = new ContentValues();
		ruleValues.put(KEY_RULE_NAME, name);
		return db.update(TABLE_RULES, ruleValues, KEY_RULE_ID + "=" + ruleID,
				null);
	}

	/**
	 * Update a Rule's active
	 * 
	 * Updates whether a Rule's active in the Database. Returns the number of
	 * Rules updated in the Database.
	 * 
	 * @param ruleID
	 *            the ID of the Rule to be updated
	 * @param isActive
	 *            boolean of whether the rule is now active or not
	 * @see Rule
	 * @return returns the number of Rules updated
	 */
	public int updateActive(int ruleID, boolean isActive) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues ruleValues = new ContentValues();
		ruleValues.put(KEY_RULE_ACTIVE, isActive);
		return db.update(TABLE_RULES, ruleValues, KEY_RULE_ID + "=" + ruleID,
				null);
	}

	/**
	 * Delete a Rule
	 * 
	 * Deletes a Rule in the Database.
	 * 
	 * @param rule
	 *            the Rule to be deleted
	 * @see Rule
	 */
	public void deleteRule(Rule rule) {
		for (int i = rule.getRCauses().size() - 1; i >= 0; i--) {
			deleteRCause(rule.getRCauses().get(i));
		}
		for (int i = rule.getREffects().size() - 1; i >= 0; i--) {
			deleteREffect(rule.getREffects().get(i));
		}
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RULES, KEY_RULE_ID + " = ?",
				new String[] { String.valueOf(rule.getID()) });
		db.close();
	}

	/**
	 * Delete an rCause
	 * 
	 * Deletes an rCause in the Database.
	 * 
	 * @param rc
	 *            the rCause to be deleted
	 * @see rCause
	 */
	public void deleteRCause(rCause rc) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_R_CAUSES, KEY_R_CAUSE_ID + " = ?",
				new String[] { String.valueOf(rc.getID()) });
		db.close();
	}

	/**
	 * Delete an rEffect
	 * 
	 * Deletes an rEffect in the Database.
	 * 
	 * @param re
	 *            the rEffect to be deleted
	 * @see rEffect
	 */
	public void deleteREffect(rEffect re) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_R_EFFECTS, KEY_R_EFFECT_ID + " = ?",
				new String[] { String.valueOf(re.getID()) });
		db.close();
	}

	/**
	 * Create a Cause
	 * 
	 * Adds a Cause to the Database.
	 * 
	 * @param cause
	 *            the Cause to be created
	 * @see Cause
	 */
	public void addCause(Cause cause) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues causeValues = new ContentValues();
		causeValues.put(KEY_CAUSE_TYPE, cause.getType());
		causeValues.put(KEY_CAUSE_NAME, cause.getName());
		causeValues.put(KEY_CAUSE_DESCRIPTION, cause.getDescription());
		causeValues.put(KEY_CAUSE_CATEGORY, cause.getCategory());
		db.insert(TABLE_CAUSES, null, causeValues);
		db.close();
	}

	/**
	 * Rule exists checker
	 * 
	 * Determines whether a Rule exists or not in the Database.
	 * 
	 * @param rule
	 *            the Rule to be checked to see if it exists
	 * @see Rule
	 * @returns true = returns whether a rule exists or not
	 */
	public boolean ruleExists(Rule rule) {
		boolean exists = true;
		for (int rules = 1; (rules <= getRulesCount()) && exists; rules++) {
			if (getRule(rules).getRCauses().size() != rule.getRCauses().size()
					|| getRule(rules).getREffects().size() != rule
							.getREffects().size()) {
				exists = false;
			}
			for (int rcs = 0; (rcs < getRule(rules).getRCauses().size())
					&& exists; rcs++) {
				if (!rCauseExists(getRule(rules).getRCauses().get(rcs), rule)) {
					exists = false;
				}
			}
			for (int res = 0; (res < getRule(rules).getREffects().size())
					&& exists; res++) {
				if (!rEffectExists(getRule(rules).getREffects().get(res), rule)) {
					exists = false;
				}
			}
		}
		if (getRulesCount() == 0) {
			exists = false;
		}
		return exists;
	}

	/**
	 * rCause exists checker
	 * 
	 * Determines whether a rCause exists or not in the Database. Needs the Rule
	 * since the rCause is not in the Database yet.
	 * 
	 * @param rc
	 *            the rCause to be checked to see if it exists
	 * @param rule
	 *            the Rule that the rCause may exist inside of
	 * @see rCause
	 * @see Rule
	 * @return returns whether an rCause exists or not
	 */
	public boolean rCauseExists(rCause rc, Rule rule) {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_R_CAUSES + " WHERE "
				+ KEY_R_CAUSE_RULE_ID + "=? AND " + KEY_CAUSE_ID + "=? AND "
				+ KEY_R_CAUSE_PARAMETER + "=?";
		Cursor cursor = db.rawQuery(
				selectQuery,
				new String[] { "" + rule.getID(), "" + rc.getCauseID(),
						rc.getParameters() });
		if (cursor.moveToFirst()) {
			db.close();
			return true;
		}
		db.close();
		return false;
	}

	/**
	 * rEffect exists checker
	 * 
	 * Determines whether a rEffect exists or not in the Database. Needs the
	 * Rule since the rEffect is not in the Database yet.
	 * 
	 * @param re
	 *            the rEffect to be checked to see if it exists
	 * @param rule
	 *            the Rule that the rEffect may exist inside of
	 * @see rEffect
	 * @see Rule
	 * @return returns whether an rEffect exists or not
	 */
	public boolean rEffectExists(rEffect re, Rule rule) {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_R_EFFECTS + " WHERE "
				+ KEY_R_EFFECT_RULE_ID + "=? AND " + KEY_EFFECT_ID + "=? AND "
				+ KEY_R_EFFECT_PARAMETER + "=?";
		Cursor cursor = db.rawQuery(
				selectQuery,
				new String[] { "" + rule.getID(), "" + re.getEffectID(),
						re.getParameters() });
		if (cursor.moveToFirst()) {
			db.close();
			return true;
		}
		db.close();
		return false;
	}

	/**
	 * Create an rCause
	 * 
	 * Checks to see if a rCause already exists from
	 * {@link #rCauseExists(rCause, Rule)} and if returns false will add the
	 * rCause to the Database.
	 * 
	 * @param rc
	 *            the rCause to be added to the Database
	 * @param rule
	 *            the Rule that the rCause is inside of
	 * @see rCause
	 * @see Rule
	 * @see #rCauseExists(rCause, Rule)
	 * @return returns whether the rCause was added or not
	 */
	public boolean addRCause(rCause rc, Rule rule) {
		boolean b = rCauseExists(rc, rule);
		if (!b) {
			ContentValues rCauseValues = new ContentValues();
			rCauseValues.put(KEY_R_CAUSE_RULE_ID, rule.getID());
			rCauseValues.put(KEY_CAUSE_ID, rc.getCauseID());
			rCauseValues.put(KEY_R_CAUSE_PARAMETER, rc.getParameters());
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_R_CAUSES, null, rCauseValues);
			db.close(); // Closing database connection
		} else {
			Toast.makeText(this.context, "You cannot have duplicate causes!",
					Toast.LENGTH_SHORT).show();
		}
		return b;
	}

	/**
	 * Create an rEffect
	 * 
	 * Checks to see if a rEffect already exists from
	 * {@link #rEffectExists(rEffect, Rule)} and if returns false will add the
	 * rEffect to the Database.
	 * 
	 * @param re
	 *            the rEffect to be added to the Database
	 * @param rule
	 *            the Rule that the rEffect is inside of
	 * @see rEffect
	 * @see Rule
	 * @see #rEffectExists(rEffect, Rule)
	 * @return returns whether the rCause was added or not
	 */
	public void addREffect(rEffect re, Rule rule) {
		if (!rEffectExists(re, rule)) {
			ContentValues rEffectValues = new ContentValues();
			rEffectValues.put(KEY_R_EFFECT_RULE_ID, rule.getID());
			rEffectValues.put(KEY_EFFECT_ID, re.getEffectID());
			rEffectValues.put(KEY_R_EFFECT_PARAMETER, re.getParameters());
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_R_EFFECTS, null, rEffectValues);
			db.close(); // Closing database connection
		} else {
			Toast.makeText(this.context, "You cannot have duplicate effects!",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Create an Effect
	 * 
	 * Creates an Effect in the Database.
	 * 
	 * @param effect
	 *            the Effect to be added
	 * @see Effect
	 */
	public void addEffect(Effect effect) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues effectValues = new ContentValues();
		effectValues.put(KEY_EFFECT_TYPE, effect.getType());
		effectValues.put(KEY_EFFECT_NAME, effect.getName());
		effectValues.put(KEY_EFFECT_DESCRIPTION, effect.getDescription());
		effectValues.put(KEY_EFFECT_CATEGORY, effect.getCategory());
		db.insert(TABLE_EFFECTS, null, effectValues);
		db.close();
	}

	/**
	 * Get all Causes
	 * 
	 * Finds and returns all Cause objects in the Database.
	 * 
	 * @see Cause
	 * @return List full of all Cause objects in Database
	 */
	public List<Cause> getAllCauses() {
		List<Cause> causeList = new ArrayList<Cause>();
		String selectQuery = "SELECT  * FROM " + TABLE_CAUSES + " ORDER BY "
				+ KEY_CAUSE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Cause cause = new Cause();
				cause.setID(cursor.getColumnIndex("_id"));
				cause.setType(cursor.getString(1));
				cause.setName(cursor.getString(2));
				cause.setDescription(cursor.getString(3));
				cause.setCategory(cursor.getString(4));
				causeList.add(cause);
			} while (cursor.moveToNext());
		}
		close();
		return causeList;
	}

	/**
	 * Get all Causes with given category
	 * 
	 * Finds and returns all Cause objects in the Database with given category.
	 * 
	 * @see Cause
	 * @see Cause#_category
	 * @return List full of all Cause objects in Database with given category
	 */
	public List<Cause> getAllCauses(String category) {
		List<Cause> causeList = new ArrayList<Cause>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CAUSES + " WHERE "
				+ KEY_CAUSE_CATEGORY + " =? ORDER BY " + KEY_CAUSE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, new String[] { category });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Cause cause = new Cause();
				cause.setID(cursor.getColumnIndex("_id"));
				cause.setType(cursor.getString(1));
				cause.setName(cursor.getString(2));
				cause.setDescription(cursor.getString(3));
				cause.setCategory(cursor.getString(4));
				causeList.add(cause);
			} while (cursor.moveToNext());
		}
		close();
		return causeList;
	}

	/**
	 * Get all Effects
	 * 
	 * Finds and returns all Effect objects in the Database.
	 * 
	 * @see Effect
	 * @return List full of all Effect objects in Database
	 */
	public List<Effect> getAllEffects() {
		List<Effect> effectList = new ArrayList<Effect>();
		String selectQuery = "SELECT  * FROM " + TABLE_EFFECTS + " ORDER BY "
				+ KEY_EFFECT_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Effect effect = new Effect();
				effect.setID(cursor.getColumnIndex("_id"));
				effect.setType(cursor.getString(1));
				effect.setName(cursor.getString(2));
				effect.setDescription(cursor.getString(3));
				effect.setCategory(cursor.getString(4));
				effectList.add(effect);
			} while (cursor.moveToNext());
		}
		close();
		return effectList;
	}

	/**
	 * Get all Effects with given category
	 * 
	 * Finds and returns all Effect objects in the Database with given category.
	 * 
	 * @see Effect
	 * @see Effect#_category
	 * @return List<Effect> of Effect objects in Database with given category
	 */
	public List<Effect> getAllEffects(String category) {
		List<Effect> effectList = new ArrayList<Effect>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_EFFECTS + " WHERE "
				+ KEY_EFFECT_CATEGORY + " =? ORDER BY " + KEY_EFFECT_NAME;
		Cursor cursor = db.rawQuery(selectQuery, new String[] { category });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Effect effect = new Effect();
				effect.setID(cursor.getColumnIndex("_id"));
				effect.setType(cursor.getString(1));
				effect.setName(cursor.getString(2));
				effect.setDescription(cursor.getString(3));
				effect.setCategory(cursor.getString(4));
				effectList.add(effect);
			} while (cursor.moveToNext());
		}
		close();
		return effectList;
	}

	/**
	 * Get rCause with given ID
	 * 
	 * Finds and returns the rCause with the given ID.
	 * 
	 * @see rCause
	 * @return rCause with given ID or null if not found
	 */
	public rCause getRCauseByID(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_ID
				+ ", " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_RULE_ID + ", "
				+ TABLE_R_CAUSES + "." + KEY_CAUSE_ID + ", " + TABLE_R_CAUSES
				+ "." + KEY_R_CAUSE_PARAMETER + ", " + TABLE_CAUSES + "."
				+ KEY_CAUSE_TYPE + " FROM " + TABLE_R_CAUSES + " INNER JOIN "
				+ TABLE_CAUSES + " ON " + TABLE_R_CAUSES + "." + KEY_CAUSE_ID
				+ "=" + TABLE_CAUSES + "." + KEY_CAUSE_ID + " WHERE "
				+ KEY_R_CAUSE_ID + "=" + id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		rCause cause = null;
		if (cursor.moveToFirst()) {
			cause = new rCause(Integer.parseInt(cursor.getString(0)),
					Integer.parseInt(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)), cursor.getString(3),
					cursor.getString(4));
		}
		close();
		// return contact
		return cause;
	}

	/**
	 * Get rEffect with given ID
	 * 
	 * Finds and returns the rEffect with the given ID.
	 * 
	 * @see rEffect
	 * @return rEffect with given ID or null if not found
	 */
	public rEffect getREffectByID(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_RULE_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_PARAMETER + ", " + TABLE_EFFECTS + "."
				+ KEY_EFFECT_TYPE + " FROM " + TABLE_R_EFFECTS + " INNER JOIN "
				+ TABLE_EFFECTS + " ON " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + "=" + TABLE_EFFECTS + "." + KEY_EFFECT_ID
				+ " WHERE " + KEY_R_EFFECT_ID + "=" + id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		rEffect effect = null;
		if (cursor.moveToFirst()) {
			effect = new rEffect(Integer.parseInt(cursor.getString(0)),
					Integer.parseInt(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)), cursor.getString(3),
					cursor.getString(4));
		}
		close();
		// return contact
		return effect;
	}

	/**
	 * Get rCauses with given Rule ID
	 * 
	 * Finds and returns all rCauses with the given Rule ID.
	 * 
	 * @see Rule
	 * @see rCause
	 * @return ArrayList<rCause> of rCause with given rule ID
	 */
	public ArrayList<rCause> getAllRCauses(int ruleID) {
		ArrayList<rCause> rCauseList = new ArrayList<rCause>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_ID
				+ ", " + TABLE_R_CAUSES + "." + KEY_R_CAUSE_RULE_ID + ", "
				+ TABLE_R_CAUSES + "." + KEY_CAUSE_ID + ", " + TABLE_R_CAUSES
				+ "." + KEY_R_CAUSE_PARAMETER + ", " + TABLE_CAUSES + "."
				+ KEY_CAUSE_TYPE + ", " + TABLE_CAUSES + "." + KEY_CAUSE_NAME
				+ " FROM " + TABLE_R_CAUSES + " INNER JOIN " + TABLE_CAUSES
				+ " ON " + TABLE_R_CAUSES + "." + KEY_CAUSE_ID + "="
				+ TABLE_CAUSES + "." + KEY_CAUSE_ID + " WHERE "
				+ TABLE_R_CAUSES + "." + KEY_R_CAUSE_RULE_ID + "=" + ruleID;
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				rCause cause = new rCause();
				cause.setID(Integer.parseInt(cursor.getString(0)));
				cause.setRuleID(Integer.parseInt(cursor.getString(1)));
				cause.setCauseID(Integer.parseInt(cursor.getString(2)));
				cause.setParameters(cursor.getString(3));
				cause.setType(cursor.getString(4));
				cause.setName(cursor.getString(5));
				rCauseList.add(cause);
			} while (cursor.moveToNext());
		}
		close();
		return rCauseList;
	}

	/**
	 * Get rEffects with given Rule ID
	 * 
	 * Finds and returns all rEffects with the given Rule ID.
	 * 
	 * @see Rule
	 * @see rEffect
	 * @return ArrayList<rEffect> of rEffect with given rule ID
	 */
	public ArrayList<rEffect> getAllREffects(int ruleID) {
		ArrayList<rEffect> rEffectList = new ArrayList<rEffect>();

		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_RULE_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + ", " + TABLE_R_EFFECTS + "."
				+ KEY_R_EFFECT_PARAMETER + ", " + TABLE_EFFECTS + "."
				+ KEY_EFFECT_TYPE + ", " + TABLE_EFFECTS + "."
				+ KEY_EFFECT_NAME + " FROM " + TABLE_R_EFFECTS + " INNER JOIN "
				+ TABLE_EFFECTS + " ON " + TABLE_R_EFFECTS + "."
				+ KEY_EFFECT_ID + "=" + TABLE_EFFECTS + "." + KEY_EFFECT_ID
				+ " WHERE " + TABLE_R_EFFECTS + "." + KEY_R_EFFECT_RULE_ID
				+ "=" + ruleID;
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				rEffect effect = new rEffect();
				effect.setID(Integer.parseInt(cursor.getString(0)));
				effect.setRuleID(Integer.parseInt(cursor.getString(1)));
				effect.setEffectID(Integer.parseInt(cursor.getString(2)));
				effect.setParameters(cursor.getString(3));
				effect.setType(cursor.getString(4));
				effect.setName(cursor.getString(5));
				rEffectList.add(effect);
			} while (cursor.moveToNext());
		}
		close();
		return rEffectList;
	}

	/**
	 * Get all Cause categories
	 * 
	 * Finds and returns all categories found within any Cause.
	 * 
	 * @see Cause#_category
	 * @return ArrayList<String> of categories
	 */
	public ArrayList<String> getAllCauseCategories() {
		ArrayList<String> list = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + TABLE_CAUSES + " ORDER BY "
				+ KEY_CAUSE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if (!list.contains(cursor.getString(4)))
					list.add(cursor.getString(4));
			} while (cursor.moveToNext());
		}
		close();
		return list;
	}

	/**
	 * Get all Effect categories
	 * 
	 * Finds and returns all categories found within any Effect.
	 * 
	 * @see Effect#_category
	 * @return ArrayList<String> of categories
	 */
	public ArrayList<String> getAllEffectCategories() {
		ArrayList<String> list = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + TABLE_EFFECTS + " ORDER BY "
				+ KEY_EFFECT_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if (!list.contains(cursor.getString(4)))
					list.add(cursor.getString(4));
			} while (cursor.moveToNext());
		}
		close();
		return list;
	}

	/**
	 * Get all categories
	 * 
	 * Finds and returns all categories found within any Cause or Effect.
	 * 
	 * @see Cause#_category
	 * @see Effect#_category
	 * @return ArrayList<String> of categories
	 */
	public ArrayList<String> getAllCategories() {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(getAllCauseCategories());
		list.addAll(getAllEffectCategories());
		return list;
	}

	/**
	 * Get all Rule categories
	 * 
	 * Finds and returns all categories found within any Rule.
	 * 
	 * @see Cause#_category
	 * @see Effect#_category
	 * @return ArrayList<String> of categories
	 */
	public ArrayList<String> getAllRuleCategories() {
		ArrayList<String> list = new ArrayList<String>();
		String selectQuery = "SELECT " + TABLE_CAUSES + "."
				+ KEY_CAUSE_CATEGORY + " FROM " + TABLE_R_CAUSES
				+ " INNER JOIN " + TABLE_CAUSES + " ON " + TABLE_R_CAUSES + "."
				+ KEY_CAUSE_ID + "=" + TABLE_CAUSES + "." + KEY_CAUSE_ID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if (!list.contains(cursor.getString(0)))
					list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		selectQuery = "SELECT " + KEY_EFFECT_CATEGORY + " FROM "
				+ TABLE_R_EFFECTS + " INNER JOIN " + TABLE_EFFECTS + " ON "
				+ TABLE_R_EFFECTS + "." + KEY_EFFECT_ID + "=" + TABLE_EFFECTS
				+ "." + KEY_EFFECT_ID;
		cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				if (!list.contains(cursor.getString(0)))
					list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		close();
		return list;
	}

	/**
	 * Print all Tables
	 * 
	 * Prints all of the Tables (all rows and columns) in the Database.
	 */
	public void seeTables() {
		String selectQuery = "SELECT * FROM " + TABLE_RULES;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.d("rules table", "");
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnNames().length; i++)
					Log.d("see table",
							cursor.getColumnName(i) + " : "
									+ cursor.getString(i));

			} while (cursor.moveToNext());
		}
		Log.d("Causes table", "");
		selectQuery = "SELECT * FROM " + TABLE_CAUSES;
		cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnNames().length; i++)
					Log.d("see table",
							cursor.getColumnName(i) + " : "
									+ cursor.getString(i));

			} while (cursor.moveToNext());
		}
		Log.d("Effects table", "");
		selectQuery = "SELECT * FROM " + TABLE_EFFECTS;
		cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnNames().length; i++)
					Log.d("see table",
							cursor.getColumnName(i) + " : "
									+ cursor.getString(i));

			} while (cursor.moveToNext());
		}
		Log.d("rCauses table", "");
		selectQuery = "SELECT * FROM " + TABLE_R_CAUSES;
		cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnNames().length; i++)
					Log.d("see table",
							cursor.getColumnName(i) + " : "
									+ cursor.getString(i));

			} while (cursor.moveToNext());
		}
		Log.d("rEffects table", "");
		selectQuery = "SELECT * FROM " + TABLE_R_EFFECTS;
		cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				for (int i = 0; i < cursor.getColumnNames().length; i++)
					Log.d("see table",
							cursor.getColumnName(i) + " : "
									+ cursor.getString(i));

			} while (cursor.moveToNext());
		}
		close();
	}

	/**
	 * Duplicate rCause checker
	 * 
	 * Checks the Database for rCause duplicates.
	 * 
	 * @see rCause
	 * @return returns <code>true</code> if there are duplicates
	 */
	public boolean duplicateRCauses(Rule rule) {
		String selectQuery = "SELECT * FROM " + TABLE_R_CAUSES + " WHERE "
				+ KEY_R_CAUSE_RULE_ID + "=? AND " + KEY_CAUSE_ID + "=? AND "
				+ KEY_R_CAUSE_PARAMETER + "=?";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		boolean duplicate = false;
		for (int i = 0; (i < rule.getRCauses().size() - 1) && !duplicate; i++) {
			cursor = db.rawQuery(selectQuery, new String[] { "" + rule.getID(),
					"" + rule.getRCauses().get(i).getCauseID(),
					"" + rule.getRCauses().get(i).getParameters() });
			if (cursor.getCount() > 1)
				duplicate = true;
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return duplicate;
	}

	/**
	 * Duplicate rEffect checker
	 * 
	 * Checks the Database for rEffect duplicates.
	 * 
	 * @see rEffect
	 * @return returns <code>true</code> if there are duplicates
	 */
	public boolean duplicateREffects(Rule rule) {
		String selectQuery = "SELECT * FROM " + TABLE_R_EFFECTS + " WHERE "
				+ KEY_R_EFFECT_RULE_ID + "=? AND " + KEY_EFFECT_ID + "=? AND "
				+ KEY_R_EFFECT_PARAMETER + "=?";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		boolean duplicate = false;
		for (int i = 0; (i < rule.getREffects().size() - 1) && !duplicate; i++) {
			cursor = db.rawQuery(selectQuery, new String[] { "" + rule.getID(),
					"" + rule.getREffects().get(i).getEffectID(),
					"" + rule.getREffects().get(i).getParameters() });
			if (cursor.getCount() > 1)
				duplicate = true;
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return duplicate;
	}
}