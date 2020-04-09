package com.example.instantinvites;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventsListAdapter extends ArrayAdapter<newEvent> {
    private Activity context;
    private List<newEvent> eventsList;
    newEvent event;

    public EventsListAdapter(Activity context, List<newEvent> eventsList){
        super(context, R.layout.events_list_layout, eventsList);
        this.context = context;
        this.eventsList = eventsList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.events_list_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.eventsListLayout);

        event = eventsList.get(position);
        textViewName.setText(event.getName());

        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = eventsList.get(position);
                Intent intent = new Intent(getContext(), EventDetails.class);
                intent.putExtra("event id", event.getEventId());
                intent.putExtra("event name", event.getName());
                intent.putExtra("event description", event.getDescription());
                intent.putExtra("event venue", event.getVenue());
                intent.putExtra("event startTime", event.getStartTime());
                intent.putExtra("event endTime", event.getEndTime());
                context.startActivity(intent);
            }
        });

        return listViewItem;
    }
}
