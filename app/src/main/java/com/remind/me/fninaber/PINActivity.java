package com.remind.me.fninaber;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.remind.me.fninaber.adapter.PINAdapter;

public class PINActivity extends Activity implements OnItemClickListener {
	private StringBuilder pinValueBuilder;
	private StringBuilder confirmValueBuilder;
	private StringBuilder xxxValueBuilder;
	private boolean isConfirmPin = false;
	private boolean switchOff = false;
	private boolean isLockScreen = false;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin);
		setFinishOnTouchOutside(false);

		isLockScreen = getIntent().getBooleanExtra(Constants.PIN_LOCK_SCREEN, false);

		GridView mGridView = (GridView) findViewById(R.id.pin_setup_gridview);
		PINAdapter mAdapater = new PINAdapter(this);
		mGridView.setAdapter(mAdapater);
		mGridView.setOnItemClickListener(this);

		pinValueBuilder = new StringBuilder();
		xxxValueBuilder = new StringBuilder();
		confirmValueBuilder = new StringBuilder();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String pin = prefs.getString(getResources().getString(R.string.setting_pin_key), null);

		if (!TextUtils.isEmpty(pin)) {
			if (!isLockScreen) {
				switchOff = true;
			}
			pinValueBuilder.append(pin);
			resetConfirmView(true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int pinVal = position + 1;

		if (!isConfirmPin) {
			if (pinValueBuilder.length() < 6) {
				pinValueBuilder.append(pinVal);

				xxxValueBuilder.append("x");
				((TextView) findViewById(R.id.pin_value_xxx)).setText(xxxValueBuilder.toString());

				if (pinValueBuilder.length() == 6) {
					resetConfirmView(false);
				}
			}
		} else {
			if (pinValueBuilder.length() == 6 && confirmValueBuilder.length() < 6) {
				confirmValueBuilder.append(pinVal);

				xxxValueBuilder.append("x");
				((TextView) findViewById(R.id.pin_value_xxx)).setText(xxxValueBuilder.toString());

				if (confirmValueBuilder.length() == 6) {
					if (pinValueBuilder.toString().equalsIgnoreCase(confirmValueBuilder.toString())) {
						if (!isLockScreen) {
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
							SharedPreferences.Editor edit = prefs.edit();
							String message = getResources().getString(R.string.pin_saved);
							int code = Activity.RESULT_OK;
							if (switchOff) {
								edit.clear();
								code = Activity.RESULT_CANCELED;
								message = getResources().getString(R.string.pin_set_off);
							} else {
								edit.putString(getResources().getString(R.string.setting_pin_key), pinValueBuilder.toString());
							}
							edit.commit();
							Toast.makeText(this, message, Toast.LENGTH_LONG).show();
							setResult(code);
						}else{
							setResult(HomeActivity.RESUlT_REFRESH);
						}
						finish();
					} else {
						Toast.makeText(this, getResources().getString(R.string.pin_dont_match), Toast.LENGTH_LONG).show();
						if (isLockScreen) {
							resetConfirmView(true);
						} else {
							finish();
						}

					}
				}
			}
		}
	}

	public void resetConfirmView(boolean inputPIN) {
		isConfirmPin = true;
		xxxValueBuilder.delete(0, xxxValueBuilder.length());
		confirmValueBuilder.delete(0, confirmValueBuilder.length());
		((TextView) findViewById(R.id.pin_value_xxx)).setText(null);
		if (!inputPIN) {
			((TextView) findViewById(R.id.pin_title)).setText(R.string.confirm_pin);
		}
	}

	@Override
	public void onBackPressed() {
		if (isLockScreen) {
			if (count > 0) {
				setResult(HomeActivity.RESUlT_CLOSE);
				super.onBackPressed();
			} else {
				Toast.makeText(this, getResources().getString(R.string.press_again_to_exit), Toast.LENGTH_LONG).show();
			}
			count++;
		} else {
			super.onBackPressed();
		}

	}
}
