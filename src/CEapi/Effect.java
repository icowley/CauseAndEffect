package CEapi;

/**
 * The Effect class is the structure that stores information for a specific
 * effect in the backend No rule data is stored within this class
 * 
 * @author CEandroid SMU
 */
public class Effect {
	/**
	 * The unique identifier for this effect
	 */
	int _id;

	/**
	 * The _type refers to the grouping of effects within the back end The _name
	 * stores the name of this specific effect The _descripton is a more user
	 * friendly representation of the _name THe _category refers to the grouping
	 * of effects on the front end
	 */
	String _type, _name, _description, _category;

	/**
	 * Default Constructor
	 */
	public Effect() {

	}

	/**
	 * Defined Effect Constructor
	 * 
	 * @param id
	 * @param type
	 * @param name
	 * @param description
	 * @param category
	 */
	public Effect(int id, String type, String name, String description,
			String category) {
		this._id = id;
		this._type = type;
		this.setName(name);
		this._description = description;
		this._category = category;
	}

	/**
	 * Defined Effect Constructor without the _id
	 * 
	 * @param type
	 * @param name
	 * @param description
	 * @param category
	 */
	public Effect(String type, String name, String description, String category) {
		this._type = type;
		this.setName(name);
		this._description = description;
		this._category = category;
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

	/**
	 * Set _description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this._description = description;
	}

	/**
	 * Get _description
	 * 
	 * @return this._description
	 */
	public String getDescription() {
		return this._description;
	}

	/**
	 * Set _category
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		this._category = category;
	}

	/**
	 * Get _category
	 * 
	 * @return this._category
	 */
	public String getCategory() {
		return this._category;
	}
}
