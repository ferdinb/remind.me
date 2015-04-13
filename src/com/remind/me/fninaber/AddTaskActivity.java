package com.remind.me.fninaber;

import java.io.File;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.ImageUtil;
import com.remind.me.fninaber.util.RoundedTransform;
import com.remind.me.fninaber.util.TaskManager;
import com.remind.me.fninaber.widget.ArialText;
import com.remind.me.fninaber.widget.TransparentProgressDialog;
import com.squareup.picasso.Picasso;

public class AddTaskActivity extends FragmentActivity implements OnClickListener, OnCheckedChangeListener {
	private static final String TAG = AddTaskActivity.class.getSimpleName();
	private String mDate;
	private String mTime;
	private ArialText dateView;
	private ArialText timeView;
	private EditText editTitle;
	private EditText editNotes;
	private static final int GALLERY = 0;
	private static final int CAMERA = 1;
	private LinearLayout addAttachmentGroup;
	private RelativeLayout photoGroup;
	private ImageView photoAttachment;
	private Uri cameraUri;
	private String existTID;
	private LinearLayout repeatLayout;
	private Animation fadeIn;
	private Animation fadeOut;
	private View repeatDay;
	private View repeatWeek;
	private View repeatMonth;
	private View repeatYear;
	private Switch switchRepeat;
	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;
	private boolean isView;
	private TransparentProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);

		ActionBar actionBar = getActionBar();
		if (null != actionBar) {
			actionBar.setTitle(getResources().getString(R.string.add_task));
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setIcon(getResources().getDrawable(R.drawable.ic_actionbar));
		}

		repeatLayout = (LinearLayout) this.findViewById(R.id.repeat_group);
		// title = (TextView) this.findViewById(R.id.action_bar_title);
		// title.setOnClickListener(this);

		mDate = DateUtil.calendarDay();
		mTime = DateUtil.calendarTime();
		findViewById(R.id.add_task_date_group).setOnClickListener(this);

		repeatDay = (View) findViewById(R.id.action_bar_day);
		repeatDay.setOnClickListener(this);

		repeatWeek = (View) findViewById(R.id.action_bar_week);
		repeatWeek.setOnClickListener(this);

		repeatMonth = (View) findViewById(R.id.action_bar_month);
		repeatMonth.setOnClickListener(this);

		repeatYear = (View) findViewById(R.id.action_bar_year);
		repeatYear.setOnClickListener(this);
		switchRepeat = ((Switch) findViewById(R.id.add_task_repeat_btn));
		switchRepeat.setOnCheckedChangeListener(this);

		dateView = (ArialText) findViewById(R.id.add_task_date);
		dateView.setText(mDate);
		timeView = (ArialText) findViewById(R.id.add_task_time);
		timeView.setOnClickListener(this);
		timeView.setText(mTime);

		editTitle = (EditText) findViewById(R.id.add_task_title);
		editNotes = (EditText) findViewById(R.id.add_task_notes);

		// findViewById(R.id.action_bar_right).setOnClickListener(this);
		findViewById(R.id.activity_add_task_recorder).setOnClickListener(this);
		findViewById(R.id.activity_add_task_photo).setOnClickListener(this);
		findViewById(R.id.activity_add_task_gallery).setOnClickListener(this);
		findViewById(R.id.activity_add_task_maps).setOnClickListener(this);
		findViewById(R.id.add_task_image_remove).setOnClickListener(this);
		// leftButton = (Button) findViewById(R.id.activity_add_task_cancel);
		// leftButton.setOnClickListener(this);
		// rightButton = (Button) findViewById(R.id.activity_add_task_save);
		// rightButton.setOnClickListener(this);

		addAttachmentGroup = (LinearLayout) findViewById(R.id.add_task_attachments);
		photoGroup = (RelativeLayout) findViewById(R.id.add_task_image_group);
		photoAttachment = (ImageView) findViewById(R.id.add_task_image);
		photoAttachment.setOnClickListener(this);
		setRepeatTime(Constants.REPEAT_DAY);
		Task task = (Task) getIntent().getSerializableExtra(Constants.TASK);
		if (task != null) {
			setDataToView(task);
		}
		isView = getIntent().getBooleanExtra(Constants.VIEW, false);
		if (isView) {
			setViewEnableDisable(false);
		}
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
	}

	private void setViewEnableDisable(boolean isEnable) {
		editTitle.setFocusable(isEnable);
		editNotes.setFocusable(isEnable);
		editTitle.setFocusableInTouchMode(isEnable);
		editNotes.setFocusableInTouchMode(isEnable);
		findViewById(R.id.add_task_date_group).setEnabled(isEnable);
		findViewById(R.id.add_task_date_group).setOnClickListener(null);
		findViewById(R.id.activity_add_task_recorder).setEnabled(isEnable);
		findViewById(R.id.activity_add_task_photo).setEnabled(isEnable);
		findViewById(R.id.activity_add_task_gallery).setEnabled(isEnable);
		findViewById(R.id.activity_add_task_maps).setEnabled(isEnable);
		findViewById(R.id.add_task_image_remove).setOnClickListener(null);
		switchRepeat.setOnCheckedChangeListener(null);
		switchRepeat.setFocusable(isEnable);
		switchRepeat.setEnabled(isEnable);
		switchRepeat.setClickable(isEnable);
		repeatDay.setEnabled(isEnable);
		repeatWeek.setEnabled(isEnable);
		repeatMonth.setEnabled(isEnable);
		repeatYear.setEnabled(isEnable);
		timeView.setClickable(isEnable);
		timeView.setSelected(isEnable);
		timeView.setOnClickListener(null);

		if (isEnable) {
			timeView.setOnClickListener(this);
			switchRepeat.setOnCheckedChangeListener(this);
			findViewById(R.id.add_task_date_group).setOnClickListener(this);
			findViewById(R.id.add_task_image_remove).setOnClickListener(this);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isView) {
			menu.findItem(R.id.action_edit).setVisible(true);
			menu.findItem(R.id.action_delete).setVisible(true);
			menu.findItem(R.id.action_save).setVisible(false);
		} else {
			menu.findItem(R.id.action_delete).setVisible(false);
			menu.findItem(R.id.action_edit).setVisible(false);
			menu.findItem(R.id.action_save).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_task_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setDataToView(Task task) {
		dateView.setText(DateUtil.dateTimestamp(task.getTimestamp()));
		timeView.setText(DateUtil.timeTimestamp(task.getTimestamp()));
		editTitle.setText(task.getTitle());
		editNotes.setText(task.getNotes());

		String path = task.getPath();
		String type = task.getType();
		int repeat = task.getRepeat();
		if (repeat >= 0) {
			switchRepeat.setChecked(true);
			repeatLayout.setVisibility(View.VISIBLE);
			setRepeatTime(repeat);
		}

		if (type.equalsIgnoreCase(Type.PHOTO.toString()) && !TextUtils.isEmpty(path)) {
			Uri uri = Uri.parse(path);
			cameraUri = uri;

			int corner = (int) getResources().getDimension(R.dimen.padding_size_5dp);
			Picasso.with(AddTaskActivity.this).load(uri).skipMemoryCache().transform(new RoundedTransform(corner, 0)).into(photoAttachment);

			photoGroup.setVisibility(View.VISIBLE);
			addAttachmentGroup.setVisibility(View.GONE);
		}
		existTID = task.getTID();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.add_task_date_group: {
			int[] val = DateUtil.dayParseDate(mDate);
			DatePickerDialog datePicker = new DatePickerDialog(this, dateListener, val[2], val[1], val[0]);
			DatePicker picker = datePicker.getDatePicker();
			picker.setMinDate(System.currentTimeMillis() - 10000);
			datePicker.show();
			break;
		}
		case R.id.add_task_time: {
			String[] val = DateUtil.timeParseHour(mTime);
			TimePickerDialog timePicker = new TimePickerDialog(this, timeListener, Integer.valueOf(val[0]), Integer.valueOf(val[1]), true);
			timePicker.show();
			break;
		}
		case R.id.activity_add_task_photo: {
			takePhoto(CAMERA);
			break;
		}

		case R.id.activity_add_task_gallery: {
			takePhoto(GALLERY);
			break;
		}
		case R.id.activity_add_task_recorder: {
			Toast.makeText(this, getResources().getString(R.string.available_on_next_update), Toast.LENGTH_SHORT).show();
			break;
		}

		case R.id.activity_add_task_maps: {
			Toast.makeText(this, getResources().getString(R.string.available_on_next_update), Toast.LENGTH_SHORT).show();
			break;
		}

		case R.id.add_task_image_remove: {
			cameraUri = null;
			photoGroup.setVisibility(View.GONE);
			addAttachmentGroup.setVisibility(View.VISIBLE);
			photoAttachment.setImageBitmap(null);
			break;
		}

		case R.id.action_bar_day:
			setRepeatTime(Constants.REPEAT_DAY);
			break;
		case R.id.action_bar_week:
			setRepeatTime(Constants.REPEAT_WEEK);
			break;
		case R.id.action_bar_month:
			setRepeatTime(Constants.REPEAT_MONTH);
			break;
		case R.id.action_bar_year:
			setRepeatTime(Constants.REPEAT_YEAR);
			break;
		case R.id.add_task_image:
//			zoomImageFromThumb(photoAttachment, cameraUri);

			// findViewById(R.id.add_task_container).setVisibility(View.GONE);
			// ((ImageView)findViewById(R.id.expanded_image)).setImageURI(cameraUri);
			// ((ImageView)findViewById(R.id.expanded_image)).setVisibility(View.VISIBLE);;
			break;
		default:
			break;
		}
	}

	private OnDateSetListener dateListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mDate = DateUtil.calendarGivenDay(dayOfMonth, monthOfYear + 1, year);
			if (!TextUtils.isEmpty(mDate)) {
				dateView.setText(mDate);
			}
		}
	};

	private OnTimeSetListener timeListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mTime = DateUtil.calendarGivenTime(hourOfDay, minute);
			if (!TextUtils.isEmpty(mTime)) {
				timeView.setText(mTime);
			}
		}
	};

	private void takePhoto(int type) {
		switch (type) {
		case GALLERY: {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, GALLERY);
			break;
		}
		case CAMERA:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (intent.resolveActivity(getPackageManager()) != null) {
				File photoFile = null;
				try {
					photoFile = ImageUtil.createTempImageFile();
				} catch (Exception e) {
					Log.e("f.ninaber", "Error : " + e.getMessage());
				}
				if (photoFile != null) {
					cameraUri = Uri.fromFile(photoFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
					startActivityForResult(intent, CAMERA);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY && null != data) {
				Uri uri = data.getData();
				doResize(uri);
			} else if (requestCode == CAMERA && null != cameraUri) {
				doResize(cameraUri);
			}
		} else {
			cameraUri = null;
		}
	}

	private void doResize(Uri uri) {
		new AsyncTask<Uri, Void, Uri>() {
			@Override
			protected Uri doInBackground(Uri... params) {
				return ImageUtil.resizeWriteUrI(AddTaskActivity.this, params[0], true);
			}

			protected void onPostExecute(Uri result) {
				if (null != result) {
					cameraUri = result;
					photoGroup.setVisibility(View.VISIBLE);
					addAttachmentGroup.setVisibility(View.GONE);
					// photoAttachment.setImageURI(result);

					int corner = (int) getResources().getDimension(R.dimen.padding_size_5dp);
					Picasso.with(AddTaskActivity.this).load(result).skipMemoryCache().transform(new RoundedTransform(corner, 0)).into(photoAttachment);
				} else {
					cameraUri = null;
					Toast.makeText(AddTaskActivity.this, "Something wrong with image", Toast.LENGTH_SHORT).show();
				}

			};
		}.execute(uri);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Start - Update alarm
		TaskManager.getInstance(this).startTaskAlarm(this.getContentResolver(), System.currentTimeMillis());
		cameraUri = null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.add_task_repeat_btn) {
			setUpAnimation();

			if (repeatLayout.getVisibility() == View.VISIBLE) {
				repeatLayout.setAnimation(fadeOut);
				repeatLayout.setVisibility(View.GONE);
			} else {
				repeatLayout.setVisibility(View.VISIBLE);
				repeatLayout.setAnimation(fadeIn);
			}
		}
	}

	private void setUpAnimation() {
		if (fadeIn == null) {
			fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		}

		if (fadeOut == null) {
			fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
		}
	}

	private void setRepeatTime(int time) {
		repeatDay.setSelected(false);
		repeatWeek.setSelected(false);
		repeatMonth.setSelected(false);
		repeatYear.setSelected(false);

		switch (time) {
		case Constants.REPEAT_YEAR:
			repeatYear.setSelected(true);
			break;

		case Constants.REPEAT_MONTH:
			repeatMonth.setSelected(true);
			break;

		case Constants.REPEAT_WEEK:
			repeatWeek.setSelected(true);
			break;

		default:
			repeatDay.setSelected(true);
			break;
		}

	}

	private void zoomImageFromThumb(final View thumbView, Uri cameraUri) {
		// If there's an animation in progress, cancel it immediately and
		// proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.

		// ferdi
		final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
		expandedImageView.setImageURI(cameraUri);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step
		// involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the
		// final bounds are the global visible rectangle of the container view.
		// Also
		// set the container view's offset as the origin for the bounds, since
		// that's
		// the origin for the positioning animation properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		findViewById(R.id.container_fragment).getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the
		// "center crop" technique. This prevents undesirable stretching during
		// the animation.
		// Also calculate the start scaling factor (the end scaling factor is
		// always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations to the
		// top-left corner of
		// the zoomed-in view (the default is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left)).with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down to the
		// original bounds
		// and show the thumbnail instead of the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their
				// original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		case R.id.action_delete:
			if (!TextUtils.isEmpty(existTID)) {
				TaskHelper.getInstance().deleteByTID(getContentResolver(), existTID);
				this.finish();
			}
			break;

		case R.id.action_edit:
			isView = false;
			setViewEnableDisable(true);
			invalidateOptionsMenu();
			break;

		case R.id.action_save:
			String title = editTitle.getText().toString();
			String notes = editNotes.getText().toString();
			String day = dateView.getText().toString();
			String time = timeView.getText().toString();
			long timestamp = DateUtil.timestampDay(day, time);
			boolean isRepeat = switchRepeat.isChecked();

			Log.e("f.ninaber", "Title : " + title + "|| Timestamp : " + timestamp);

			if (!TextUtils.isEmpty(title) && timestamp > 0) {
				if (timestamp < System.currentTimeMillis() && DateUtil.getDate(System.currentTimeMillis()).contentEquals(DateUtil.getDate(timestamp))) {
					Toast.makeText(this, getResources().getString(R.string.set_time), Toast.LENGTH_SHORT).show();
					return false;
				}

				Task task = new Task();
				if (!TextUtils.isEmpty(existTID)) {
					task.setTID(existTID);
				} else {
					task.setTID(String.valueOf(System.currentTimeMillis() / 1000));
				}
				task.setTitle(title);
				task.setNotes(notes);
				task.setTimestamp(timestamp);
				task.setStatus(Constants.ON_GOING);

				if (isRepeat) {
					if (repeatDay.isSelected()) {
						task.setRepeat(Constants.REPEAT_DAY);
					} else if (repeatWeek.isSelected()) {
						task.setRepeat(Constants.REPEAT_WEEK);
					} else if (repeatMonth.isSelected()) {
						task.setRepeat(Constants.REPEAT_MONTH);
					} else {
						task.setRepeat(Constants.REPEAT_YEAR);
					}
				} else {
					task.setRepeat(-1);
				}

				if (null != cameraUri) {
					task.setPath(cameraUri.toString());
					task.setType(Type.PHOTO.toString());
				} else {
					task.setType(Type.TEXT.toString());
				}

				task.setSnooze(-1);

				saveTaskWriteImage(task, task.getPath() == null ? null : Uri.parse(task.getPath()));
			} else {
				Toast.makeText(this, getResources().getString(R.string.add_title), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveTaskWriteImage(final Task task, Uri uri) {
		if (null == uri) {
			TaskHelper.getInstance().insertAsync(getContentResolver(), task);
			finish();
		} else {
			new AsyncTask<Uri, Void, Uri>() {
				protected void onPreExecute() {
					if (null == dialog) {
						dialog = new TransparentProgressDialog(AddTaskActivity.this);
						dialog.show();
					}
				};

				@Override
				protected Uri doInBackground(Uri... params) {
					return ImageUtil.writeBitmap(params[0], AddTaskActivity.this);
				}

				protected void onPostExecute(Uri result) {
					if (null != dialog && dialog.isShowing()) {
						dialog.dismiss();
					}

					if (null != result) {
						task.setPath(result.toString());
						TaskHelper.getInstance().insertAsync(getContentResolver(), task);
						finish();
					} else {
						Toast.makeText(AddTaskActivity.this, getApplicationContext().getResources().getString(R.string.ups_something_wrong), Toast.LENGTH_SHORT).show();
					}
				};
			}.execute(uri);
		}
	}
}