package com.example.ceandroid.Effects;

import android.content.Context;
import android.widget.Toast;

/**
 * Displays a Toast message
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionToast {

	/**
	 * global interface that allows access application resources
	 */
	private Context context;
	/**
	 * the content text of the toast
	 */
	private String text;

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context and the content of the
	 * toast.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param parameters
	 */
	public ActionToast(Context context, String parameters) {
		this.context = context;
		if (parameters.length() > 0) {
			this.text = " " + parameters.substring(5);
		}
		this.text += " ";
	}

	/**
	 * Executes the effect
	 * 
	 * Shows the toast with the intended text content.
	 */
	public void execute() {
		Toast.makeText(context.getApplicationContext(), text,
				Toast.LENGTH_SHORT).show();
	}
}
