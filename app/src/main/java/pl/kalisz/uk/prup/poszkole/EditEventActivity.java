package pl.kalisz.uk.prup.poszkole;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {

    private EditText editTextName, editTextSubject, editTextFrom, editTextTo;
    private Switch switchCancelled;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private String eventId;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Modyfikuj zajęcia");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextName = findViewById(R.id.editTextName);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextFrom = findViewById(R.id.editTextFrom);
        editTextTo = findViewById(R.id.editTextTo);
        switchCancelled = findViewById(R.id.switchCancelled);
        buttonSave = findViewById(R.id.buttonSave);

        eventId = getIntent().getStringExtra("eventId");
        selectedDate = getIntent().getStringExtra("selectedDate");

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar").child(selectedDate).child(eventId);

        loadEventData();

        editTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextFrom);
            }
        });

        editTextTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextTo);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void showTimePickerDialog(final EditText timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadEventData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    editTextName.setText(event.getName());
                    editTextSubject.setText(event.getSubject());
                    editTextFrom.setText(event.getFrom());
                    editTextTo.setText(event.getTo());
                    switchCancelled.setChecked(event.isCancelled());
                } else {
                    Toast.makeText(EditEventActivity.this, "Nie znaleziono zajęć", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditEventActivity.this, "Błąd wczytywania danych", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEvent() {
        String name = editTextName.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String from = editTextFrom.getText().toString().trim();
        String to = editTextTo.getText().toString().trim();
        boolean cancelled = switchCancelled.isChecked();

        Event event = new Event(from, name, subject, to, cancelled);
        databaseReference.setValue(event);

        Toast.makeText(this, "Zapisano zmiany", Toast.LENGTH_SHORT).show();
        finish();
    }
}