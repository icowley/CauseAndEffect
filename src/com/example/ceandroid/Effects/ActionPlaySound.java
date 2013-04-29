package com.example.ceandroid.Effects;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Plays a sound Effect
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionPlaySound {

	/**
	 * global interface that allows access application resources
	 */
	private Context context;
	/**
	 * URI to the media file to be played
	 */
	private Uri parameters;

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context and location to the audio
	 * file. It also converts the location of the audio file from a String into
	 * a URI.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param parameters
	 *            the location of the audio file
	 */
	public ActionPlaySound(Context context, String parameters) {
		this.context = context;
		this.parameters = Uri.parse(parameters);
	}

	/**
	 * Execute the effect
	 * 
	 * Plays the intended audio file in the default media player for audio.
	 */
	public void execute() {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(this.parameters, "audio/*");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
}