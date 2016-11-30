package com.csce.tutorapp;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.format.Time;
        import android.text.style.TtsSpan;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.TimePicker;

        import org.w3c.dom.Text;

public class ScheduleSelectionActivity extends AppCompatActivity {

    Button btnSave, btnCancel;
    TextView tvStartTime, tvEndTime;
    TimePicker tpStartTime, tpEndTime;
    int startHour, endHour;
    String ampm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore last saved instance
        super.onCreate(savedInstanceState);

        // Load layout resources
        setContentView(R.layout.schedule_selection_activity);

        btnSave = (Button) findViewById(R.id.save_schedule_selection_button);
        btnCancel = (Button) findViewById(R.id.cancel_schedule_selection_button);
        tvStartTime = (TextView) findViewById(R.id.textview_start_time);
        tvEndTime = (TextView) findViewById(R.id.textview_end_time);
        tpStartTime = (TimePicker) findViewById(R.id.tp_start_time);
        tpEndTime = (TimePicker) findViewById(R.id.tp_end_time);
        ampm = "";
        createButtonListeners();
    }

    private void createButtonListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                startHour = tpStartTime.getCurrentHour();
                endHour = tpEndTime.getCurrentHour();
                ampm = startHour > 11 ? " PM" : " AM";
                StringBuilder startTime = new StringBuilder().append(tpStartTime.getCurrentHour()).append(":").append(tpStartTime.getCurrentMinute()).append(ampm);
                b.putString("Start Time", startTime.toString());
                ampm = endHour > 11 ? " PM" : " AM";
                StringBuilder endTime = new StringBuilder().append(tpEndTime.getCurrentHour()).append(":").append(tpEndTime.getCurrentMinute()).append(ampm);
                b.putString("End Time", endTime.toString());
                Intent i = new Intent(ScheduleSelectionActivity.this, FindTutorActivity.class);
                i.putExtras(b);
                Intent finishActivity = new Intent("finish_find_tutor");
                sendBroadcast(finishActivity);
                startActivity(i);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
