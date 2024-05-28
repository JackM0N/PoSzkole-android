package pl.kalisz.uk.prup.poszkole;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    private EditText editTextImie, editTextPrzedmiot, editTextGodzinaOd, editTextGodzinaDo;
    private Button buttonZapisz;
    private DatabaseReference databaseReference;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        selectedDate = getIntent().getStringExtra("selectedDate");
        editTextImie = findViewById(R.id.editTextImie);
        editTextPrzedmiot = findViewById(R.id.editTextPrzedmiot);
        editTextGodzinaOd = findViewById(R.id.editTextGodzinaOd);
        editTextGodzinaDo = findViewById(R.id.editTextGodzinaDo);
        buttonZapisz = findViewById(R.id.buttonZapisz);

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");

        editTextGodzinaOd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextGodzinaOd);
            }
        });

        editTextGodzinaDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextGodzinaDo);
            }
        });

        buttonZapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
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

    private void saveDataToFirebase() {
        String imie = editTextImie.getText().toString().trim();
        String przedmiot = editTextPrzedmiot.getText().toString().trim();
        String godzinaOd = editTextGodzinaOd.getText().toString().trim();
        String godzinaDo = editTextGodzinaDo.getText().toString().trim();

        if (imie.isEmpty() || przedmiot.isEmpty() || godzinaOd.isEmpty() || godzinaDo.isEmpty()) {
            Toast.makeText(AddEvent.this, "Należy wypełnić wszystkie pola formularza", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", imie);
        data.put("subject", przedmiot);
        data.put("from", godzinaOd);
        data.put("to", godzinaDo);

        databaseReference.child(selectedDate).push().setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEvent.this, "Zajęcia zostały poprawnie zapisane w bazie", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AddEvent.this, "Coś poszło nie tak, przepraszamy", Toast.LENGTH_LONG).show();
                    }
                });
    }
}