package com.boardhood.mobile.widget;

import com.boardhood.api.BoardHood;
import com.boardhood.api.util.BHHashMap;
import com.boardhood.api.util.BHMap;
import com.boardhood.mobile.BoardHoodSettings;
import com.boardhood.mobile.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FilterDialog extends android.app.Dialog {
	private RadioGroup orderByRadioGroup;
	private TextView radiusTextView;
	private SeekBar radiusSeekBar;
	private android.widget.Button cancelButton;
	private android.widget.Button applyButton;
	
	private OnApplyFilterListener onApplyFilterAction;
	private BHMap<String, Integer> orderByOptions;
	
	private String orderBy;
	private int radius;

	public FilterDialog(Context context) {
		super(context);
		
		setContentView(R.layout.dialog_filter);
		setTitle(context.getString(R.string.dialog_filter_title));

		LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.FILL_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
                
        initAttrs();
        initListeners();        
	}
	
	public void initAttrs() {
		orderByOptions = new BHHashMap<String, Integer>();
        orderByOptions.put(BoardHood.ORDER_POPULAR , R.id.filter_orderby_popularity);
        orderByOptions.put(BoardHood.ORDER_RECENT  , R.id.filter_orderby_recent);
        orderByOptions.put(BoardHood.ORDER_DISTANCE, R.id.filter_orderby_distance);
        
        orderBy = BoardHoodSettings.getInstance().getString(BoardHoodSettings.FILTER_ORDER_BY, BoardHood.ORDER_POPULAR);
		radius  = BoardHoodSettings.getInstance().getInt(BoardHoodSettings.FILTER_RADIUS, 0);
        
		orderByRadioGroup = (RadioGroup) findViewById(R.id.filter_orderby);
		radiusTextView = (TextView) findViewById(R.id.filter_radius_label);
		radiusSeekBar = (SeekBar) findViewById(R.id.filter_radius);
		cancelButton = (Button) findViewById(R.id.filter_cancel);
		applyButton = (Button) findViewById(R.id.filter_apply);

		orderByRadioGroup.check(orderByOptions.get(orderBy));
		radiusSeekBar.setProgress(radius);
		setRadiusProgress(radius);
	}

	public void initListeners() {
		orderByRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				orderBy = orderByOptions.getKeyByValue(checkedId);
			}
		});
		
		radiusSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setRadiusProgress(progress);
				radius = progress;
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		
		applyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onApplyFilterAction != null) {
					onApplyFilterAction.filterApplied(orderBy, radius);
				}
				
				SharedPreferences.Editor editor = BoardHoodSettings.getEditor();
				editor.putString(BoardHoodSettings.FILTER_ORDER_BY, orderBy);
				editor.putInt(BoardHoodSettings.FILTER_RADIUS, radius);
				editor.commit();
				
				dismiss();
			}
		});
	}
	
	public void setRadiusProgress(int progress) {
		if (progress == 0) {
			radiusTextView.setText(getContext().getString(R.string.filter_distance_disabled));
		} else {
			radiusTextView.setText(progress + getContext().getString(R.string.distance));
		}
	}
	
	public void setOnApplyFilterListener(OnApplyFilterListener listener) {
		onApplyFilterAction = listener;
	}
	
	public static interface OnApplyFilterListener {
		public void filterApplied(String sortBy, int radius);
	}
}
