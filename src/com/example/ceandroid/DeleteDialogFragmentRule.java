package com.example.ceandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Creates the DialogFragment for deleting rules from the MyRules Activity
 * 
 * @author CEandroid SMU
 */
public class DeleteDialogFragmentRule extends DialogFragment {

	/**
	 * Creates the DeleteDialogFragmentRule
	 * 
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle(R.string.delete)
				.setMessage(R.string.delete_confirmation_rule)
				// Set the action buttons
				.setPositiveButton(R.string.delete_confirm,
						new DialogInterface.OnClickListener() {
							/**
							 * Confirms the Delete of the Rule
							 * 
							 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
							 *      int)
							 */
							public void onClick(DialogInterface dialog, int id) {
								// Send the positive button event back to the
								// host activity
								mListener
										.onDialogPositiveClick(DeleteDialogFragmentRule.this);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							/**
							 * DeleteDialogFragmentRule Cancelled
							 * 
							 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
							 *      int)
							 */
							public void onClick(DialogInterface dialog, int id) {
								// Send the negative button event back to the
								// host activity
								mListener
										.onDialogNegativeClick(DeleteDialogFragmentRule.this);
							}
						});

		return builder.create();
	}

	/**
	 * DeleteDialogListenerRule Interface
	 * 
	 * @author CEandroid SMU
	 */
	public interface DeleteDialogListenerRule {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	/**
	 * DeleteDialogListenerRule that implements the DeleteDialogListenerRule
	 * Interface
	 */
	DeleteDialogListenerRule mListener;

	/**
	 * Attaches the DialogFragment to the MyRules Activity
	 * 
	 * @see android.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (DeleteDialogListenerRule) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DeleteDialogListenerRule");
		}
	}
}