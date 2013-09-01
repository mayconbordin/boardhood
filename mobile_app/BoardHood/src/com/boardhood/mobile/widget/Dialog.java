package com.boardhood.mobile.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialog {
	public static void OKDialog(String title, String message, Context ctx) {
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();  
		alertDialog.setTitle(title);  
	    alertDialog.setMessage(message);  
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
	      public void onClick(DialogInterface dialog, int which) {  
	        return;  
	    }});
	    alertDialog.show();
	}
	
	public static void OKDialog(String message, Context ctx) {
		Dialog.OKDialog(null, message, ctx);
	}
	
	public static void YesNoDialog(String title, String message, DialogInterface.OnClickListener listener, Context ctx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title)
			   .setMessage("Are you sure?")
			   .setPositiveButton("Yes", listener)
			   .setNegativeButton("No", listener)
			   .create()
			   .show();
	}
}
