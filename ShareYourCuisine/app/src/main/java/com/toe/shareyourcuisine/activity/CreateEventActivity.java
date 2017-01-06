package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.utils.SYCUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by HQu on 1/5/2017.
 */

public class CreateEventActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Validator.ValidationListener, EventService.CreateEventListener {

    private static final String TAG = "ToeCreateEvent:";
    @NotEmpty
    private MaterialEditText mTitleET;
    @NotEmpty
    private MaterialEditText mStartTimeET;
    @NotEmpty
    private MaterialEditText mEndTimeET;
    @NotEmpty
    private MaterialEditText mLocationET;
    @NotEmpty
    private MaterialEditText mMaxNumberOfGuestsET;
    @NotEmpty
    private MaterialEditText mDescET;
    private ImageView mDisplayIV;
    private Button mSelectImgBtn;
    private Button mSubmitBtn;
    private Long mStartDateTime;
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private Long mEndDateTime;
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    private String mDateTimeType;
    private String mDisplayImgUrl;
    private Validator mValidator;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(CreateEventActivity.this);
        mValidator.setValidationListener(CreateEventActivity.this);

        mTitleET = (MaterialEditText)findViewById(R.id.title_et);
        mStartTimeET = (MaterialEditText)findViewById(R.id.start_time_et);
        mEndTimeET = (MaterialEditText)findViewById(R.id.end_time_et);
        mLocationET = (MaterialEditText)findViewById(R.id.location_et);
        mMaxNumberOfGuestsET = (MaterialEditText) findViewById(R.id.max_number_of_guests_et);
        mDescET = (MaterialEditText)findViewById(R.id.desc_et);
        mDisplayIV = (ImageView) findViewById(R.id.display_img_iv);
        mSelectImgBtn = (Button) findViewById(R.id.select_img_btn);
        mSubmitBtn = (Button) findViewById(R.id.submit_btn);

        mStartTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimeType = "startDateTime";
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        mEndTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimeType = "endDateTime";
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(CreateEventActivity.this)
                        .setActionBarColor(Color.rgb(211, 47, 47), Color.rgb(211, 47, 47))
                        .setPickerCount(1)
                        .setCamera(true)
                        .startAlbum();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(mDateTimeType.equalsIgnoreCase("startDateTime")){
            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;
        } else {
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
        }
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(CreateEventActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if(mDateTimeType.equalsIgnoreCase("startDateTime")){
            mStartDateTime = new GregorianCalendar(mStartYear, mStartMonth, mStartDay, hourOfDay, minute).getTimeInMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            mStartTimeET.setText(simpleDateFormat.format(mStartDateTime));
        } else {
            mEndDateTime = new GregorianCalendar(mEndYear, mEndMonth, mEndDay, hourOfDay, minute).getTimeInMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            mEndTimeET.setText(simpleDateFormat.format(mEndDateTime));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ViewGroup.LayoutParams params = mDisplayIV.getLayoutParams();
                    params.width = (int) getResources().getDimension(R.dimen.img_dimen);
                    params.height = (int) getResources().getDimension(R.dimen.img_dimen);
                    mDisplayIV.setLayoutParams(params);
                    mDisplayImgUrl = data.getStringArrayListExtra(Define.INTENT_PATH).get(0);
                    Picasso.with(CreateEventActivity.this).load(new File(mDisplayImgUrl)).fit().centerCrop().into(mDisplayIV);
                }
        }
    }

    @Override
    public void onValidationSucceeded() {
        if(mEndDateTime <= mStartDateTime) {
            Toast.makeText(this, "End time should be later than start time!", Toast.LENGTH_LONG).show();
        } else if(mDisplayImgUrl == null || mDisplayImgUrl == "") {
            Toast.makeText(this, "Please upload a display image!", Toast.LENGTH_LONG).show();
        } else {
            mMaterialDialog = new MaterialDialog.Builder(CreateEventActivity.this)
                    .title("Creating event")
                    .content("Please wait")
                    .progress(true, 0)
                    .show();
            Event event = new Event();
            event.setTitle(mTitleET.getText().toString());
            event.setStartTime(mStartDateTime);
            event.setEndTime(mEndDateTime);
            event.setLocation(mLocationET.getText().toString());
            event.setDesc(mDescET.getText().toString());
            event.setDisplayImgUrl(mDisplayImgUrl);
            event.setMaxNumberOfGuests(Integer.parseInt(mMaxNumberOfGuestsET.getText().toString()));
            event.setCreatedAt(SYCUtils.getCurrentEST());
            event.setCreatedBy(mAuth.getCurrentUser().getUid());
            EventService eventService = new EventService(CreateEventActivity.this);
            eventService.setCreateEventListener(CreateEventActivity.this);
            eventService.createEvent(event);
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void createEventSucceed() {
        mMaterialDialog.dismiss();
        Toast.makeText(this, "Create event successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void createEventFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
