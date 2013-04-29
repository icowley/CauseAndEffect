package com.example.ceandroid;

import java.util.ArrayList;
import com.example.ceandroid.Effects.*;

import CEapi.rEffect;
import android.content.Context;

/**
 * The Action Executor Class executes Effects
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionExecuter {

	/**
	 * Blank constructor for ActionExecuter
	 */
	public ActionExecuter() {
		// blank constructor
	}

	/**
	 * Executes the effect list
	 * 
	 * Executes all of the passed effects. Determines which effect to execute
	 * using the rEffect enum type.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param effects
	 *            list of all rEffect objects to be executed.
	 */
	public void executeActions(Context context, ArrayList<rEffect> effects) {

		for (rEffect effect : effects) {
			switch (Type.valueOf(effect.getType())) {
			case vibrate: {
				new ActionVibrate(context, effect.getParameters()).execute(); // vibrate
				break;
			}
			case toast: {
				new ActionToast(context, effect.getParameters()).execute(); // toast
				break;
			}
			case notification: {
				new ActionShowNotification(context, effect.getParameters())
						.execute(); // notifications
				break;
			}
			case sound: {
				new ActionPlaySound(context, effect.getParameters()
						.split("\\n")[0]).execute(); // play sound
				break;
			}
			case ringer: {
				new ActionRingerMode(context, effect.getParameters()).execute(); // set
																					// ringer
																					// mode
			}
			default: {
				break;
			}
			}
		}

	}

	/**
	 * Currently Stored Effect Types This can be removed if the switch statement
	 * above uses strings instead
	 * 
	 * @author CEandroid SMU
	 */
	public enum Type {
		vibrate, toast, notification, sound, ringer
	}
}