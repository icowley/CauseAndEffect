package CEapi;

/**
 * The rEffect class is the structure that stores information for an effect
 * associated to a rule
 * 
 * @author CEandroid SMU
 */
public class rEffect {
	/**
	 * _id The rEffect Identifier _ruleID The Rule Identifier _effectID The
	 * effect Identifier
	 */
	int _id, _ruleID, _effectID;
	/**
	 * _parameters Stores the information needed to execute this rEffect _type
	 * The type of rEffect used for the back end
	 */
	String _parameters, _type;
	/**
	 * _name The name of this rEffect
	 */
	private String _name;

	/**
	 * Default Constructor
	 */
	public rEffect() {

	}

	/**
	 * Defined rEffect Constructor
	 * 
	 * @param id
	 * @param ruleID
	 * @param effectID
	 * @param parameters
	 * @param type
	 */
	public rEffect(int id, int ruleID, int effectID, String parameters,
			String type) {
		this._id = id;
		this._ruleID = ruleID;
		this._effectID = effectID;
		this._parameters = parameters;
		this._type = type;
		this._name = "";
	}

	/**
	 * Defined rEffect Constructor without rEffect ID
	 * 
	 * @param ruleID
	 * @param effectID
	 * @param parameters
	 * @param type
	 */
	public rEffect(int ruleID, int effectID, String parameters, String type) {
		this._ruleID = ruleID;
		this._effectID = effectID;
		this._parameters = parameters;
		this._type = type;
		this._name = "";
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
	 * Get _id
	 * 
	 * @return this._id
	 */
	public int getID() {
		return this._id;
	}

	/**
	 * Set _ruleID
	 * 
	 * @param ruleID
	 */
	public void setRuleID(int ruleID) {
		this._ruleID = ruleID;
	}

	/**
	 * Get _ruleID
	 * 
	 * @return this._ruleID
	 */
	public int getRuleID() {
		return this._ruleID;
	}

	/**
	 * Set _effectID
	 * 
	 * @param effectID
	 */
	public void setEffectID(int effectID) {
		this._effectID = effectID;
	}

	/**
	 * Get _effectID
	 * 
	 * @return this._effectID
	 */
	public int getEffectID() {
		return this._effectID;
	}

	/**
	 * Set _parameters
	 * 
	 * @param parameters
	 */
	public void setParameters(String parameters) {
		this._parameters = parameters;
	}

	/**
	 * Get _parameters
	 * 
	 * @return this._parameters
	 */
	public String getParameters() {
		return this._parameters;
	}

	/**
	 * Set _type
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this._type = type;
	}

	/**
	 * Get _type
	 * 
	 * @return this._type
	 */
	public String getType() {
		return this._type;
	}

	/**
	 * Set _name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this._name = name;
	}

	/**
	 * Get _name
	 * 
	 * @return this._name
	 */
	public String getName() {
		return this._name;
	}
}
