package com.boardhood.mobile.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.boardhood.api.exception.ValidationError;
import com.boardhood.api.exception.ValidationException;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

public class Validator {
	private Context context;
	private List<Field> fields;
	
	public Validator(Context context) {
		this.context = context;
		fields = new ArrayList<Field>();
	}
	
	public Validator(Context context, Field...fields) {
		this.context = context;
		this.fields = new ArrayList<Field>(Arrays.asList(fields));
	}
	
	public Field addField(String name, EditText editText) {
		Field field = new Field(name, editText);
		fields.add(field);
		return field;
	}
	
	public Field getFieldByName(String name) {
		for (Field field : fields) {
			if (name.equals(field.getName())) {
				return field;
			}
		}
		
		return null;
	}
	
	public boolean validate(ValidationException e) {
		if (e != null) {
			for (ValidationError error : e.getErrors()) {
				Field field = getFieldByName(error.getField());
				if (field != null) {
					field.addError(getErrorMessage(error.getField(), error.getCode()));
				}
			}
		}
		
		boolean valid = true;
		for (Field field : fields) {
			if (field.hasErrors()) {
				valid = false;
				field.displayErrors();
			}
		}
		
		return valid;
	}
	
	public String getErrorMessage(String field, String code) {
		return getResourceString("field_" + field) + " " + getResourceString(code);
	}
	
	protected String getResourceString(String id) {
		return context.getString(context.getResources().getIdentifier(id, "string", context.getPackageName()));
	}
	
	public void clearErrors() {
		for (Field field : fields) {
			field.clearErrors();
		}
	}
	
	public static class Field {
		private String name;
		private EditText editText;
		private List<String> errors;
		
		public Field(String name, EditText editText) {
			this.name = name;
			this.editText = editText;
			this.errors = new ArrayList<String>();
		}

		public String getName() {
			return name;
		}

		public EditText getEditText() {
			return editText;
		}

		public String getErrorMessage() {
			String message = "";
			for (int i=0; i<errors.size(); i++) {
				if (i != 0) message += "\n";
				message += errors.get(i);
			}
			return message;
		}
		
		public void displayErrors() {
			String message = getErrorMessage();
			Log.i("Validator", "Error: " + message);
			editText.setError(message);
		}
		
		public void clearErrors() {
			errors.clear();
		}
		
		public void addError(String error) {
			errors.add(error);
		}
		
		public boolean hasErrors() {
			return errors.size() > 0;
		}
	}
}
