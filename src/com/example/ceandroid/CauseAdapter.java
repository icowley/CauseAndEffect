package com.example.ceandroid;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CauseAdapter extends ArrayAdapter<EditRuleNode> {

	public CauseAdapter(Context context, int textViewResourceId, List<EditRuleNode> causes) {
		super(context, textViewResourceId);
	}
	
	

}
