package com.example.ceandroid.Effects;

import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

/**
 * Changes the ring mode type
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionRingerMode {

	/**
	 * global interface that allows access application resources
	 */
	private Context context;
	/**
	 * the intended ring mode to be changed to
	 */
	private String mode;

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context and the intended ringer
	 * mode.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param parameters
	 *            the ring mode to be changed to
	 */
	public ActionRingerMode(Context context, String parameters) {
		this.context = context;
		this.mode = parameters.replace("Ring mode: ", "");
	}

	/**
	 * Execute the effect
	 * 
	 * Changes the ringer mode based on the intended parameter. Outputs an error
	 * if and incorrect parameter is sent.
	 */
	public void execute() {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (mode.equals("normal"))
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		else if (mode.equals("vibrate"))
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		else if (mode.equals("silent"))
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		else
			Toast.makeText(context.getApplicationContext(),
					"error: ringer mode parameters", Toast.LENGTH_SHORT).show();
	}
}