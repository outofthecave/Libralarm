package com.outofthecave.libralarm.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.outofthecave.libralarm.AlarmListActivity;
import com.outofthecave.libralarm.R;
import com.outofthecave.libralarm.model.Alarm;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AlarmListRecyclerViewAdapter extends RecyclerView.Adapter<AlarmListRecyclerViewAdapter.ViewHolder> {
    private final AlarmListActivity activity;
    private List<Alarm> alarms;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ConstraintLayout layout;

        ViewHolder(ConstraintLayout layout) {
            super(layout);
            this.layout = layout;
        }
    }

    public AlarmListRecyclerViewAdapter(AlarmListActivity activity) {
        this.activity = activity;
        this.alarms = Collections.emptyList();
    }

    public AlarmListRecyclerViewAdapter setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        // TODO Update only the items that actually changed, see https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
        notifyDataSetChanged();
        return this;
    }

    @NonNull
    @Override
    public AlarmListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Alarm alarm = alarms.get(position);

        String text = String.format(Locale.ROOT, "%d:%d %s", alarm.dateTime.hour, alarm.dateTime.minute, alarm.name);

        TextView textView = holder.layout.findViewById(R.id.alarmListItemTextView);
        textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }
}
