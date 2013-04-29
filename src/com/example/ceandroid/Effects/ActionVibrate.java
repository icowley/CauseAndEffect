package com.example.ceandroid.Effects;

import android.content.Context;
import android.os.Vibrator;

/**
 * Vibrates the phone
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionVibrate {

	/**
	 * global interface that allows access application resources
	 */
	private Context context;

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param parameters
	 *            [unused] the length of the notification
	 */
	public ActionVibrate(Context context, String parameters) {
		this.context = context;
	}

	/**
	 * Executes the effect
	 * 
	 * Vibrates the device for a period of 1200 milliseconds.
	 */
	public void execute() {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(1200);
	}
}