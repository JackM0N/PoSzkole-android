package pl.kalisz.uk.prup.poszkole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView listView;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
    private Date selectedDate;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private List<String> eventIdList;
    private List<String> storedDates;
    private List<CalendarDay> calendarDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.listView);
        selectedDate = Calendar.getInstance().getTime();

        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        storedDates = new ArrayList<>();
        calendarDays = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, eventIdList);
        listView.setAdapter(eventAdapter);

        eventAdapter.setSelectedDate(selectedDate);

        fetchEventsForSelectedDate();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                storedDates.clear();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (date != null) {
                        storedDates.add(date);
                        CalendarDay calendarDay = convertToCalendarDay(date);
                        if (calendarDay != null) {
                            calendarDays.add(calendarDay);
                        }
                    }
                }

                calendarView.setCalendarDays(calendarDays);

                Log.d("CalendarDays", calendarDays.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting data failed, log a message
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });

        calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
            @Override
            public void onClick(@NonNull CalendarDay calendarDay) {
                selectedDate = calendarDay.getCalendar().getTime();
                eventAdapter.setSelectedDate(selectedDate);
                eventAdapter.setEventId(databaseReference.child(selectedDate.toString()).getKey());
                fetchEventsForSelectedDate();
            }
        });

        FloatingActionButton button = findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate == null) {
                    selectedDate = Calendar.getInstance().getTime();
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = dateFormat.format(selectedDate);

                Intent intent = new Intent(MainActivity.this, AddEvent.class);
                intent.putExtra("selectedDate", dateString);
                startActivity(intent);
            }
        });
    }

    private void fetchEventsForSelectedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(selectedDate);

        databaseReference.child(dateString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                eventIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                        eventIdList.add(snapshot.getKey());
                    } else {
                        Log.d("MainActivity", "Event jest null");
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Coś poszło nie tak, przepraszamy", Toast.LENGTH_LONG).show();
            }
        });
    }

    private CalendarDay convertToCalendarDay(String date) {
        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);

            CalendarDay calendarDay = new CalendarDay(calendar);
            calendarDay.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.eventday_background));

            return calendarDay;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}