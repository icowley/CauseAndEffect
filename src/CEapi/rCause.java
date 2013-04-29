package CEapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ceandroid.DatabaseHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.CallLog;

/**
 * The rCause class is the structure that stores information for an effect
 * associated to a rule
 * 
 * @author CEandroid SMU
 */
public class rCause {
	/**
	 * _id The rCause Identifier _ruleID The Rule Identifier _causeID The cause
	 * Identifier
	 */
	int _id, _ruleID, _causeID;
	/**
	 * _parameters Stores the information needed to evaluate this rCause _type
	 * The type of rCause used for the back end
	 */
	String _parameters, _type;
	/**
	 * _name The name of this rCause
	 */
	private String _name;

	/**
	 * Default Constructor
	 */
	public rCause() {

	}

	/**
	 * Defined rCause Constructor
	 * 
	 * @param id
	 * @param ruleID
	 * @param causeID
	 * @param parameters
	 * @param type
	 */
	public rCause(int id, int ruleID, int causeID, String parameters,
			String type) {
		this._id = id;
		this._ruleID = ruleID;
		this._causeID = causeID;
		this._parameters = parameters;
		this._type = type;
		this._name = "";
	}

	/**
	 * Defined rCause Constructor without the rCause id
	 * 
	 * @param ruleID
	 * @param causeID
	 * @param parameters
	 * @param type
	 */
	public rCause(int ruleID, int causeID, String parameters, String type) {
		this._ruleID = ruleID;
		this._causeID = causeID;
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
	 * Set _causeID
	 * 
	 * @param causeID
	 */
	public void setCauseID(int causeID) {
		this._causeID = causeID;
	}

	/**
	 * Get _causeID
	 * 
	 * @return this._causeID
	 */
	public int getCauseID() {
		return this._causeID;
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

	/**
	 * Evaluates the rCause as true or false
	 * 
	 * @param c
	 * @return boolean The result of the rCause evaluation
	 */
	@SuppressLint("SimpleDateFormat")
	public boolean isTrue(Context c) {
		// return true;
		boolean result = false;

		if (this._type == null) {
			result = false;
		} else {
			types t = types.valueOf(this._type);
			switch (t) {
			case time: {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

				// make a string of the current time
				Date d = new Date();
				String curTime = sdf.format(d);

				if (curTime.equals(this._parameters)) {
					result = true;
				} else {
					result = false;
				}

				break;
			}

			case phoneCall: {
				// Code here right now will just check the parameter for the
				// contact name and compare it contact name stored in this
				// rCause
				Uri calls = android.provider.CallLog.Calls.CONTENT_URI;// Uri.parse("content://call_log/calls");
				Cursor callCursor = c.getContentResolver().query(calls, null,
						null, null, null);
				callCursor.moveToFirst();
				@SuppressWarnings("unused")
				String name = "", duration, type, number = "";
				if (callCursor.getCount() > 0) {
					do {
						name = callCursor.getString(callCursor
								.getColumnIndex(CallLog.Calls.CACHED_NAME));
						number = callCursor.getString(callCursor
								.getColumnIndex(CallLog.Calls.NUMBER));
						type = callCursor
								.getString(callCursor
										.getColumnIndex(android.provider.CallLog.Calls.TYPE));
						duration = callCursor
								.getString(callCursor
										.getColumnIndex(android.provider.CallLog.Calls.DURATION));
						if ((Integer.parseInt(duration) == 0)) {
							if (Integer.parseInt(type) == android.provider.CallLog.Calls.MISSED_TYPE) {
								callCursor.moveToLast();
							} else {
								// System.out.println("*SKIP* Text Message: " +
								// name);
							}
						} else {
							callCursor.moveToLast();
						}

					} while (callCursor.moveToNext());
				}
				callCursor.close();
				// int commaCounter = 0;
				// char cur;
				String parsedNumber = "";
				// String parse1 = "";
				// String parse2 = "";

				for (int i = 0; i < this._parameters.length(); i++) {
					// cur = this._parameters.charAt(i);
					parsedNumber += Character.toString(this._parameters
							.charAt(i));
				}

				// if the passed in parameter's contact name is the same as the
				// stored contact name, return true
				result = parsedNumber.equals(name);
				break;
			}

			// right now textMessage checks for the same thing as phoneCall
			case textMessage: {
				// Code here right now will just check the parameter for the
				// contact name and compare it contact name stored in this
				// rCause
				Uri calls = android.provider.CallLog.Calls.CONTENT_URI;// Uri.parse("content://call_log/calls");
				Cursor callCursor = c.getContentResolver().query(calls, null,
						null, null, null);
				callCursor.moveToFirst();
				@SuppressWarnings("unused")
				String name = "", duration, type, number = "";
				if (callCursor.getCount() > 0) {
					do {
						name = callCursor.getString(callCursor
								.getColumnIndex(CallLog.Calls.CACHED_NAME));
						number = callCursor.getString(callCursor
								.getColumnIndex(CallLog.Calls.NUMBER));
						type = callCursor
								.getString(callCursor
										.getColumnIndex(android.provider.CallLog.Calls.TYPE));
						duration = callCursor
								.getString(callCursor
										.getColumnIndex(android.provider.CallLog.Calls.DURATION));
						if ((Integer.parseInt(duration) == 0)) {
							if (Integer.parseInt(type) == android.provider.CallLog.Calls.MISSED_TYPE) {
								// System.out.println("*SKIP* Missed Phone Call: "
								// + name);
							} else {
								callCursor.moveToLast();
								System.out.println("Text Message: " + name);
							}
						} else {
							// System.out.println("*SKIP* Phone Call: " + name);
						}

					} while (callCursor.moveToNext());
				}
				callCursor.close();
				// int commaCounter = 0;
				// char cur;
				String parsedNumber = "";
				// String parse1 = "";
				// String parse2 = "";

				for (int i = 0; i < this._parameters.length(); i++) {
					// cur = this._parameters.charAt(i);
					parsedNumber += Character.toString(this._parameters
							.charAt(i));
				}

				// if the passed in parameter's contact name is the same as the
				// stored contact name, return true
				result = parsedNumber.equals(name);
				break;
			}
			case ssid: {
				@SuppressWarnings("static-access")
				WifiManager wm = (WifiManager) c
						.getSystemService(c.WIFI_SERVICE);
				WifiInfo wi = wm.getConnectionInfo();
				String SSID = wi.getSSID();

				if (SSID != null) {
					SSID.trim();
					// System.out.println("# if characters = " + SSID.)

					if (SSID.equals(this.getParameters())) {
						result = true;
					}
				} else {
					return false;
				}

				break;
			}

			case wifiStatus: {
				@SuppressWarnings("static-access")
				WifiManager wm = (WifiManager) c
						.getSystemService(c.WIFI_SERVICE);
				boolean res = wm.isWifiEnabled();
				if (res == true && this.getParameters().equals("on")) {
					result = true;
				} else if (res == false && this.getParameters().equals("off")) {
					result = true;
				}
				break;
			}

			// Location based rules. Currently evaluates Arrivals and Departures
			// only
			case location: {
				LocationManager locManager = (LocationManager) c
						.getSystemService(Context.LOCATION_SERVICE);
				Location myLocation = locManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (myLocation == null) {
					myLocation = locManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (myLocation == null) {
						myLocation = locManager
								.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
					}
				}
				if (myLocation != null) {
					String p = this.getParameters();
					p = p.substring(p.indexOf("Lat: "));
					p = p.replace("Lat: ", "").replace("Lng: ", "")
							.replace("Radius: ", "").replace(" miles", "")
							.replace("Fired: ", "").replace("\n", " ");
					String[] params = p.split("[ ]+");
					Location loc = new Location("dummy");
					double r = 0;
					if (params.length > 2) {
						System.out.println("Param 0 is " + params[0]);
						System.out.println("Param 1 is " + params[1]);
						System.out.println("Param 2 is " + params[2]);
						System.out.println("Param 3 is " + params[3]);
						loc.setLatitude(Double.parseDouble(params[0]));
						loc.setLongitude(Double.parseDouble(params[1]));
						r = (Double.parseDouble(params[2]));
					}

					System.out.println("r: " + r + " dist:"
							+ myLocation.distanceTo(loc));
					if (this.getCauseID() == 6) {
						// Arrivals
						if (r < myLocation.distanceTo(loc)) {
							result = false;
							params[3] = "0";
							StringBuilder myParams = new StringBuilder(
									this._parameters);
							myParams.setCharAt(myParams.length() - 2, '0');
							this.setParameters(myParams.toString());
							DatabaseHandler db = new DatabaseHandler(c);
							db.updateRCause(this, db.getRule(this._ruleID));
							db.close();
						} else if (params[3].equals("0")) {
							result = true;
							params[3] = "1";
							StringBuilder myParams = new StringBuilder(
									this._parameters);
							myParams.setCharAt(myParams.length() - 2, '1');
							this.setParameters(myParams.toString());
							DatabaseHandler db = new DatabaseHandler(c);
							db.updateRCause(this, db.getRule(this._ruleID));
							db.close();
						}
					} else {
						// Departures
						if (r > myLocation.distanceTo(loc)) {
							result = false;
							params[3] = "0";
							StringBuilder myParams = new StringBuilder(
									this._parameters);
							myParams.setCharAt(myParams.length() - 2, '0');
							this.setParameters(myParams.toString());
							DatabaseHandler db = new DatabaseHandler(c);
							db.updateRCause(this, db.getRule(this._ruleID));
							db.close();
						} else if (params[3].equals("0")) {
							result = true;
							params[3] = "1";
							StringBuilder myParams = new StringBuilder(
									this._parameters);
							myParams.setCharAt(myParams.length() - 2, '1');
							this.setParameters(myParams.toString());
							DatabaseHandler db = new DatabaseHandler(c);
							db.updateRCause(this, db.getRule(this._ruleID));
							db.close();
						}
					}
				} else {
					System.out.println("My Location was null");
					result = false;
				}

				break;
			}

			default:
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * Types of rCauses currently stored on the app This can be removed if using
	 * strings for the switch in isTrue(c)
	 * 
	 * @author CEandroid SMU
	 */
	public enum types {
		time, phoneCall, textMessage, ssid, wifiStatus, location
	}
}
