package com.remind.me.fninaber;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.remind.me.fninaber.db.TaskHelper;
import com.remind.me.fninaber.model.Task;
import com.remind.me.fninaber.model.Type;
import com.remind.me.fninaber.util.AudioUtil;
import com.remind.me.fninaber.util.DateUtil;
import com.remind.me.fninaber.util.ImageUtil;
import com.remind.me.fninaber.util.RecordingUtil;
import com.remind.me.fninaber.util.RoundedSelectedTransform;
import com.remind.me.fninaber.util.RoundedTransform;
import com.remind.me.fninaber.util.TaskManager;
import com.remind.me.fninaber.widget.FninaberText;
import com.remind.me.fninaber.widget.TransparentProgressDialog;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;

public class AddTaskActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnTouchListener {
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private String mDate;
    private String mTime;
    private FninaberText dateView;
    private FninaberText timeView;
    private EditText editTitle;
    private EditText editNotes;
    private static final int GALLERY = 0;
    private static final int CAMERA = 1;
    private LinearLayout addAttachmentGroup;
    private RelativeLayout photoGroup;
    private ImageView photoAttachment;
    private Uri cameraUri = null;
    // private String existTID;
    private LinearLayout repeatLayout;
    private Animation fadeIn;
    private Animation fadeOut;
    private View repeatDay;
    private View repeatWeek;
    private View repeatMonth;
    private View repeatYear;
    private android.support.v7.widget.SwitchCompat switchRepeat;
    private boolean isView;
    private TransparentProgressDialog dialog;
    private Task existTask;
    private LinearLayout recordButton;
    private Rect rect;
    private boolean isRecording;
    private File audioPath;
    private Toolbar toolbar;
    private static final String TIME_TAG = "TIME_TAG";
    private static final String DATE_TAG = "DATE_TAG";
    private TextInputLayout titleWrapper;
    private TextInputLayout noteWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();

        titleWrapper = (TextInputLayout) findViewById(R.id.add_task_title_wrapper);
        noteWrapper = (TextInputLayout) findViewById(R.id.add_task_notes_wrapper);
        titleWrapper.setHint(getResources().getString(R.string.title));
        noteWrapper.setHint(getResources().getString(R.string.notes));

        repeatLayout = (LinearLayout) this.findViewById(R.id.repeat_group);

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
        switchRepeat = ((android.support.v7.widget.SwitchCompat) findViewById(R.id.add_task_repeat_btn));
        switchRepeat.setOnCheckedChangeListener(this);

        dateView = (FninaberText) findViewById(R.id.add_task_date);
        dateView.setText(mDate);
        timeView = (FninaberText) findViewById(R.id.add_task_time);
        timeView.setOnClickListener(this);
        timeView.setText(mTime);

        editTitle = (EditText) findViewById(R.id.add_task_title);
        editNotes = (EditText) findViewById(R.id.add_task_notes);

        findViewById(R.id.activity_add_task_recorder).setOnClickListener(this);
        findViewById(R.id.activity_add_task_photo).setOnClickListener(this);
        findViewById(R.id.activity_add_task_gallery).setOnClickListener(this);
        findViewById(R.id.activity_add_task_maps).setOnClickListener(this);
        findViewById(R.id.add_task_image_remove).setOnClickListener(this);
        findViewById(R.id.add_task_record_remove).setOnClickListener(this);
        findViewById(R.id.add_task_play_remove).setOnClickListener(this);
        findViewById(R.id.add_task_play_stop).setOnClickListener(this);


        recordButton = (LinearLayout) findViewById(R.id.activity_add_task_sound_recording);
        recordButton.setOnTouchListener(this);

        addAttachmentGroup = (LinearLayout) findViewById(R.id.add_task_attachments);
        photoGroup = (RelativeLayout) findViewById(R.id.add_task_image_group);
        photoAttachment = (ImageView) findViewById(R.id.add_task_image);

        setRepeatTime(Constants.REPEAT_DAY);
        Task task = (Task) getIntent().getSerializableExtra(Constants.TASK);
        if (task != null) {
            setDataToView(task);
        }
        isView = getIntent().getBooleanExtra(Constants.VIEW, false);
        if (isView) {
            setViewEnableDisable(false);
        }
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_previous_item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(getResources().getString(R.string.add_task));
    }

    private void setViewEnableDisable(boolean isEnable) {

        Log.e("f.ninaber", "Test");
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
        findViewById(R.id.add_task_play_remove).setOnClickListener(null);
        findViewById(R.id.add_task_play_remove).setVisibility(View.INVISIBLE);
        findViewById(R.id.add_task_image_remove).setVisibility(View.INVISIBLE);

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
            findViewById(R.id.add_task_container).setBackgroundColor(getResources().getColor(R.color.yellow_100));
            findViewById(R.id.add_task_play_remove).setOnClickListener(this);
            findViewById(R.id.add_task_image_remove).setVisibility(View.VISIBLE);
            findViewById(R.id.add_task_play_remove).setVisibility(View.VISIBLE);
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
            Picasso.with(AddTaskActivity.this).load(uri).skipMemoryCache().transform(new RoundedTransform(0, corner)).into(photoAttachment);

            photoGroup.setVisibility(View.VISIBLE);
            addAttachmentGroup.setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase(Type.AUDIO.toString()) && !TextUtils.isEmpty(path)) {
            audioPath = new File(path);
            addAttachmentGroup.setVisibility(View.GONE);
            initPlayRecord();
        }
        existTask = task;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_date_group: {
                int[] val = DateUtil.dayParseDate(mDate);
                DatePickerDialog dpd = DatePickerDialog.newInstance(dateListener, val[2], val[1], val[0]);
                dpd.setAccentColor(getResources().getColor(R.color.yellow_900));
                dpd.show(getFragmentManager(), DATE_TAG);
                break;
            }
            case R.id.add_task_time: {
                String[] val = DateUtil.timeParseHour(mTime);
                TimePickerDialog tpd = TimePickerDialog.newInstance(timeListener, Integer.valueOf(val[0]), Integer.valueOf(val[1]), Constants.is24HoursFormat);
                tpd.setAccentColor(getResources().getColor(R.color.yellow_900));
                tpd.show(getFragmentManager(), TIME_TAG);
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
                initViewRecord();
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

            case R.id.add_task_record_remove: {
                Log.e("f.ninaber", "Remove ");
                isRecording = false;
                audioPath = null;
                findViewById(R.id.activity_add_task_recording_group).setVisibility(View.GONE);
                addAttachmentGroup.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.add_task_play_stop: {
                ImageView button = ((ImageView) findViewById(R.id.add_task_play_stop));
                TextView time = ((TextView) findViewById(R.id.activity_add_task_play_time));
                SeekBar seekBar = ((SeekBar) findViewById(R.id.add_task_seekbar));


                Log.e("f.ninaber", "isPlaying : " + AudioUtil.getInstance().isPlaying() + " || audioPath : " + audioPath);

                if (AudioUtil.getInstance().isPlaying()) {
                    AudioUtil.getInstance().stopPlaying();
                } else {
                    AudioUtil.getInstance().setDefaultView(this, audioPath, button, seekBar, time);
                    AudioUtil.getInstance().playAudio();
                }
                break;
            }

            case R.id.add_task_play_remove: {
                if (existTask == null) {
                    if (!AudioUtil.getInstance().isPlaying()) {
                        findViewById(R.id.activity_add_task_play_audio_group).setVisibility(View.GONE);
                        addAttachmentGroup.setVisibility(View.VISIBLE);
                        RecordingUtil.getInstance().deleteAudioFile(audioPath);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.cant_close_still_playing), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    findViewById(R.id.activity_add_task_play_audio_group).setVisibility(View.GONE);
                    addAttachmentGroup.setVisibility(View.VISIBLE);
                }
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
            default:
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
            mDate = DateUtil.calendarGivenDay(dayOfMonth, monthOfYear + 1, year);
            if (!TextUtils.isEmpty(mDate)) {
                dateView.setText(mDate);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            if (!Constants.is24HoursFormat) {
                mTime = DateUtil.calendarGiven12Time(hourOfDay, minute);
            } else {
                mTime = DateUtil.calendarGiven24Time(hourOfDay, minute);
            }


            if (!TextUtils.isEmpty(mTime)) {
                timeView.setText(mTime);
            }
        }
    };

    private void takePhoto(int type) {
        switch (type) {
            case GALLERY: {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
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
                        Log.e("f.ninaber", "photoFile : " + photoFile.getAbsolutePath());
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

                Log.e("f.ninaber", "CameraUri : " + cameraUri);
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
                return ImageUtil.resizeWriteUrI(AddTaskActivity.this, params[0], false);
            }

            protected void onPostExecute(Uri result) {
                if (null != result) {
                    cameraUri = result;
                    photoGroup.setVisibility(View.VISIBLE);
                    addAttachmentGroup.setVisibility(View.GONE);

                    int corner = (int) getResources().getDimension(R.dimen.padding_size_5dp);
                    Picasso.with(AddTaskActivity.this).load(result).skipMemoryCache().transform(new RoundedTransform(corner, 0)).into(photoAttachment);
                } else {
                    cameraUri = null;
                    Toast.makeText(AddTaskActivity.this, "Something wrong with image", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != existTask) {
            ImageUtil.deleteTempFile(null);
        } else {
            ImageUtil.deleteTempFile(cameraUri);
        }

        // Start - Update alarm
        TaskManager.getInstance(this).startTaskAlarm(this.getContentResolver(), System.currentTimeMillis());
        cameraUri = null;
        isRecording = false;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.action_delete:
                if (null != existTask && !TextUtils.isEmpty(existTask.getTID())) {
                    TaskHelper.getInstance().deleteByTID(getContentResolver(), existTask.getTID());
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

                if (!TextUtils.isEmpty(title) && timestamp > 0) {
                    if (timestamp < System.currentTimeMillis() && DateUtil.getDate(System.currentTimeMillis()).contentEquals(DateUtil.getDate(timestamp))) {
                        Toast.makeText(this, getResources().getString(R.string.set_time), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    int repeat = TaskManager.getInstance(this).repeatVar(isRepeat, repeatDay, repeatWeek, repeatMonth, repeatYear);
                    Task task = TaskManager.getInstance(this).buildTask(existTask, title, notes, cameraUri, audioPath, timestamp, repeat);
                    if (task != null) {
                        saveTaskWriteImageAudio(task, task.getPath() == null ? null : Uri.parse(task.getPath()));
                    } else {
                        Toast.makeText(AddTaskActivity.this, getResources().getString(R.string.ups_something_wrong), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    titleWrapper.setError(getResources().getString(R.string.add_title));
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTaskWriteImageAudio(final Task task, Uri uri) {
        if (existTask != null && existTask.getPath() != null && !existTask.getPath().equalsIgnoreCase(task.getPath())) {
            if (existTask.getType().equalsIgnoreCase(Type.AUDIO.toString())) {
                new File(existTask.getPath()).delete();
            } else if (existTask.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
                Uri existingUri = Uri.parse(existTask.getPath());
                new File(existingUri.getPath()).delete();
            }
        }


        if (task.getType().equalsIgnoreCase(Type.TEXT.toString()) || TextUtils.isEmpty(task.getPath())) {
            TaskHelper.getInstance().insertAsync(getContentResolver(), task);
            finish();
        } else if (task.getType().equalsIgnoreCase(Type.AUDIO.toString())) {
            TaskHelper.getInstance().insertAsync(getContentResolver(), task);
            finish();
        } else if (task.getType().equalsIgnoreCase(Type.PHOTO.toString())) {
            if (existTask != null && existTask.getPath() != null && existTask.getPath().equalsIgnoreCase(task.getPath())) {
                TaskHelper.getInstance().insertAsync(getContentResolver(), task);
                finish();
            } else {
                new AsyncTask<Uri, Void, Uri>() {
                    protected void onPreExecute() {
                        if (null == dialog) {
                            dialog = new TransparentProgressDialog(AddTaskActivity.this);
                            dialog.show();
                        }
                    }

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
                    }

                }.execute(uri);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.activity_add_task_sound_recording: {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    audioPath = RecordingUtil.getInstance().prepareRecording(this);
                    isRecording = audioPath != null ? true : false;
                }

                if (isRecording) {
                    if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                        RecordingUtil.getInstance().ReleaseRecording(this, true);
                        isRecording = false;
                        audioPath = null;
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            FninaberText timeText = (FninaberText) this.findViewById(R.id.activity_add_task_recording_time);
                            if (!timeText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.time_00))) {
                                RecordingUtil.getInstance().ReleaseRecording(this, false);
                                isRecording = false;
                                initPlayRecord();
                            } else {
                                RecordingUtil.getInstance().ReleaseRecording(this, true);
                                isRecording = false;
                                audioPath = null;
                            }
                        }
                    }
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    private void initPlayRecord() {
        findViewById(R.id.activity_add_task_recording_group).setVisibility(View.GONE);
        findViewById(R.id.activity_add_task_play_audio_group).setVisibility(View.VISIBLE);

    }

    private void initViewRecord() {
        addAttachmentGroup.setVisibility(View.GONE);
        findViewById(R.id.activity_add_task_recording_group).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String title = editTitle.getText().toString();
        String notes = editNotes.getText().toString();
        String day = dateView.getText().toString();
        String time = timeView.getText().toString();
        long timestamp = DateUtil.timestampDay(day, time);
        boolean isRepeat = switchRepeat.isChecked();

        int repeat = TaskManager.getInstance(this).repeatVar(isRepeat, repeatDay, repeatWeek, repeatMonth, repeatYear);
        Task task = TaskManager.getInstance(this).buildTask(existTask, title, notes, cameraUri, audioPath, timestamp, repeat);
        outState.putSerializable(Constants.TASK, task);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Task task = (Task) savedInstanceState.getSerializable(Constants.TASK);
        setDataToView(task);
    }

}