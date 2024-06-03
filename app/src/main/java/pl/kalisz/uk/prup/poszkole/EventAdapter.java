package pl.kalisz.uk.prup.poszkole;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private Date selectedDate;
    private String eventId;
    private List<String> eventIdList;
    public EventAdapter(@NonNull Context context, @NonNull List<Event> objects, List<String> eventIdList) {
        super(context, 0, objects);
        this.eventIdList = eventIdList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_event, parent, false);
        }

        Event event = getItem(position);

        TextView textViewImie = convertView.findViewById(R.id.textViewImie);
        TextView textViewPrzedmiot = convertView.findViewById(R.id.textViewPrzedmiot);
        TextView textViewGodzinaOd = convertView.findViewById(R.id.textViewGodzinaOd);
        TextView textViewGodzinaDo = convertView.findViewById(R.id.textViewGodzinaDo);
        TextView textViewStatus = convertView.findViewById(R.id.textViewStatus);

        if (event != null) {
            textViewImie.setText(event.getName());
            textViewPrzedmiot.setText(event.getSubject());
            textViewGodzinaOd.setText(event.getFrom());
            textViewGodzinaDo.setText(event.getTo());
            if (event.isCancelled()) {
                textViewStatus.setText("Odwołane!");
            } else {
                textViewStatus.setText("");
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EditEventActivity.class);
                    intent.putExtra("eventId", eventIdList.get(position));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = dateFormat.format(selectedDate);
                    intent.putExtra("selectedDate", dateString);
                    getContext().startActivity(intent);
                }
            });
        }else {
            Log.d("EventAdapter", "Event jest null");
        }

        return convertView;
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}


