package com.example.ceandroid.Causes;

import CEapi.rCause;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ceandroid.CEapp;
import com.example.ceandroid.CauseView;
import com.example.ceandroid.EditRulePager;
import com.example.ceandroid.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * The MapPicker allows the user to choose a Location for all Location based
 * rules This class uses the Google Maps 2.0 API
 * 
 * @author CEandroid SMU
 */
public class MapPicker extends android.support.v4.app.FragmentActivity {
	/**
	 * The Google Maps 2.0 API Object
	 */
	private GoogleMap myMap;
	/**
	 * Settings for the Map
	 */
	private UiSettings myUiSettings;
	/**
	 * General use point variable
	 */
	private LatLng p;
	/**
	 * Radius from the point
	 */
	private double r;
	/**
	 * Finished selecting a location, confirmed by the user
	 */
	private boolean finished;
	/**
	 * The name of the location, set by the user
	 */
	private String name;

	/**
	 * Creates and loads the Map on the screen
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ce_activity_map);

		Toast.makeText(MapPicker.this, "Tap the map to pick a location",
				Toast.LENGTH_LONG).show();

		p = new LatLng(0, 0);
		r = 0;
		setUpMapIfNeeded();
		finished = false;
		name = "no name";

		// Turn on "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Sets up the map only if it is not already loaded
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (myMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			myMap = ((SupportMapFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (myMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * Sets up and loads the Map
	 */
	private void setUpMap() {
		// The Map is verified. It is now safe to manipulate the map.
		myMap.setMyLocationEnabled(true);
		myUiSettings = myMap.getUiSettings();
		myUiSettings.setMyLocationButtonEnabled(true);
		myUiSettings.setAllGesturesEnabled(true);
		myUiSettings.setZoomControlsEnabled(true);

		// Tap Listener
		OnMapClickListener tap = new OnMapClickListener() {
			/**
			 * Adds a pin to a user selected location, then asks the user to
			 * name the location
			 * 
			 * @see com.google.android.gms.maps.GoogleMap.OnMapClickListener#onMapClick(com.google.android.gms.maps.model.LatLng)
			 */
			public void onMapClick(LatLng point) {
				p = point;
				myMap.addMarker(new MarkerOptions().position(new LatLng(
						point.latitude, point.longitude)));
				myMap.animateCamera(
						CameraUpdateFactory.newLatLngZoom(point, 16), 1000,
						null);
				android.os.SystemClock.sleep(1000);
				messageDialog();
			}
		};

		OnMapLongClickListener tapLong = new OnMapLongClickListener() {
			/**
			 * The user confirms a location
			 * 
			 * @see com.google.android.gms.maps.GoogleMap.OnMapLongClickListener#onMapLongClick(com.google.android.gms.maps.model.LatLng)
			 */
			public void onMapLongClick(LatLng point) {
				if (finished) {
					finished = false;
					finished();
				} else {
					Toast.makeText(MapPicker.this, "No location chosen",
							Toast.LENGTH_LONG).show();
				}
			}
		};

		// Add Listeners
		myMap.setOnMapClickListener(tap);
		myMap.setOnMapLongClickListener(tapLong);

		// Goto my location on load
		LocationManager lm = (LocationManager) MapPicker.this
				.getSystemService(Context.LOCATION_SERVICE);
		Location myLocation = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (myLocation == null) {
			myLocation = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (myLocation == null) {
				myLocation = lm
						.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			}
		}
		if (myLocation != null) {
			LatLng myLoc = new LatLng(myLocation.getLatitude(),
					myLocation.getLongitude());
			myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16),
					1000, null);
		} else {
			LatLng myLoc = new LatLng(32.986204, -96.702003);
			myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16),
					1000, null);
		}
		android.os.SystemClock.sleep(1000);
	}

	/**
	 * Reloads the map if needed when the application is resumed
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Creates the region around the pin location
	 */
	public void rDialog() {
		r = .002;
		PolygonOptions rectOptions = new PolygonOptions().add(new LatLng(
				p.latitude - r, p.longitude - r), new LatLng(p.latitude - r,
				p.longitude + r), new LatLng(p.latitude + r, p.longitude + r),
				new LatLng(p.latitude + r, p.longitude - r));

		// Set the rectangle's stroke color to red
		rectOptions.strokeColor(Color.argb(125, 51, 181, 229));
		// Set the rectangle's fill to blue
		rectOptions.fillColor(Color.argb(50, 51, 181, 229));

		myMap.addPolygon(rectOptions);

		Toast.makeText(MapPicker.this, "Tap and hold to confirm this area",
				Toast.LENGTH_LONG).show();
		finished = true;
	}

	/**
	 * Called when the user has confirmed a location
	 */
	public void finished() {
		// Call Edit Rule Again
		CEapp app = (CEapp) getApplication();
		rCause rc = new rCause();

		rc.setRuleID(app.currentRule.getID());
		rc.setType("location");
		if (app.getTemp() == 0) {
			rc.setCauseID(6);
		} else {
			rc.setCauseID(7);
		}
		rc.setName(name);

		String loc = "Location: " + name + "\n";
		String plat = "Lat: " + p.latitude + "\n";
		String plon = "Lng: " + p.longitude + "\n";
		String rad = "Radius: " + (r * 50) + " miles\n";
		String fired = "Fired: 0\n";
		String parameter = loc + plat + plon + rad + fired;
		rc.setParameters(parameter);
		app.currentRule.addRCause(rc);
		app.addedRCause = true;

		Intent myIntent = new Intent(MapPicker.this, EditRulePager.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(myIntent);
	}

	/**
	 * Asks the user to name a picked location
	 */
	private void messageDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Name the location");

		// Set up the input
		final EditText input = new EditText(MapPicker.this);
		// Specify the type of input expected; this, for example, sets the input
		// as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			/**
			 * Names the location and then draws the region around the pin
			 * location
			 * 
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
			 *      int)
			 */
			public void onClick(DialogInterface dialog, int which) {
				name = input.getText().toString();
				rDialog();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					/**
					 * Clears pins if the user doesn't like the selected
					 * location
					 * 
					 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
					 *      int)
					 */
					public void onClick(DialogInterface dialog, int which) {
						myMap.clear();
						p = new LatLng(0, 0);
						dialog.cancel();
					}
				});

		builder.show();
	}

	/**
	 * MapPicker Menu Actions
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			CEapp app = (CEapp) getApplication();
			if (app.edit) {
				Intent myIntent = new Intent(getApplicationContext(),
						EditRulePager.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
			} else {
				Intent parentActivityIntent = new Intent(this, CauseView.class);
				parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(parentActivityIntent);
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Overrides the back button
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 *      android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CEapp app = (CEapp) getApplication();
			if (app.edit) {
				Intent myIntent = new Intent(getApplicationContext(),
						EditRulePager.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);
			}
			return super.onKeyDown(keyCode, event);
		}

		return super.onKeyDown(keyCode, event);
	}
}