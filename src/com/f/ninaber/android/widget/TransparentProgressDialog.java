package com.f.ninaber.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.f.ninaber.android.R;

public class TransparentProgressDialog extends Dialog {

	public TransparentProgressDialog(Context context) {
		super(context, R.style.transparentDialog);
		init(context, null);
	}

	public TransparentProgressDialog(Context context, String message) {
		super(context, R.style.transparentDialog);
		init(context, message);
	}

	public TransparentProgressDialog(Context context, int messageId) {
		super(context, R.style.transparentDialog);
		init(context, context.getResources().getString(messageId));
	}

	private void init(Context context, String message) {
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		
        ProgressBar progress = new ProgressBar(context);
        addContentView(progress, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
}
