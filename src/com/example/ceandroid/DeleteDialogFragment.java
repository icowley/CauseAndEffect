package com.example.ceandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Delete Dialog displayed when an item is to be deleted from a List
 * 
 * @author CEandroid SMU
 */
public class DeleteDialogFragment extends DialogFragment {

	/**
	 * Creates the Delete Dialog
	 * 
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		CEapp app = (CEapp) this.getActivity().getApplication();
		// Set the dialog title
		if (app.editType)
			builder.setMessage(R.string.delete_confirmation_cause);
		else
			builder.setMessage(R.string.delete_confirmation_effect);
		builder.setTitle(R.string.delete)
				// Set the action buttons
				.setPositiveButton(R.string.delete_confirm,
						new DialogInterface.OnClickListener() {
							/**
							 * Delete Confirmed
							 * 
							 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
							 *      int)
							 */
							public void onClick(DialogInterface dialog, int id) {
								// Send the positive button event back to the
								// host activity
								mListener
										.onDialogPositiveClick(DeleteDialogFragment.this);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							/**
							 * Deleted Cancelled
							 * 
							 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface,
							 *      int)
							 */
							public void onClick(DialogInterface dialog, int id) {
								// Send the negative button event back to the
								// host activity
								mListener
										.onDialogNegativeClick(DeleteDialogFragment.this);
							}
						});

		return builder.create();
	}

	/**
	 * Delete Dialog Listener Interface
	 * 
	 * @author CEandroid SMU
	 */
	public interface DeleteDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	/**
	 * Delete Dialog Listener implements the DeleteDialogListener Interface
	 */
	DeleteDialogListener mListener;

	/**
	 * Attaches the Dialog to the Activity
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
			mListener = (DeleteDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DeleteDialogListener");
		}
	}
}