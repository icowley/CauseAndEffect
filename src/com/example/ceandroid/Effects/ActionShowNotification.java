package com.example.ceandroid.Effects;

import com.example.ceandroid.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Shows a Notification
 * 
 * @author CEandroid SMU
 * 
 */
public class ActionShowNotification {

	/**
	 * global interface that allows access application resources
	 */
	private Context context;
	/**
	 * the intended title of the notification
	 */
	private String contentTitle;
	/**
	 * the intended message inside the notification
	 */
	private String contentText;

	/**
	 * Constructor
	 * 
	 * The constructor stores the application context, notification title, and
	 * notification content. The parameters are split into the title and
	 * content, respectively, before being stored.
	 * 
	 * @param context
	 *            global interface that allows access application resources
	 * @param parameters
	 *            notification title and content in the format
	 *            "Content Title'Content Text"
	 */
	public ActionShowNotification(Context context, String parameters) {
		this.context = context;
		String[] params = parameters.split("\'");
		if (params.length > 0) {
			this.contentTitle = params[0];
			if (params.length > 1) {
				this.contentText = params[1];
			}
		}
	}

	/**
	 * Executes the effect
	 * 
	 * Shows a new notification based on the intended title and content. The
	 * notification manager identifies each notification using the notification
	 * ID, which is stored as the time in milliseconds at which the notification
	 * was created.
	 */
	public void execute() {
		PendingIntent pIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(pIntent).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int mId = (int) System.currentTimeMillis(); // notification ID is the
													// current time in
													// milliseconds

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(mId, notification);
	}
}
