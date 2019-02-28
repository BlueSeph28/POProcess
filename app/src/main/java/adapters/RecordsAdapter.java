package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luislopez.poprocess.R;

import java.util.ArrayList;

/**
 * Created by LuisLopez on 1/8/15.
 */
public class RecordsAdapter extends BaseAdapter {

    ArrayList<String> names, times, dates;
    Context context;
    private LayoutInflater inflater;

    public RecordsAdapter (Context c, ArrayList<String> names, ArrayList<String> times, ArrayList<String> dates){
        this.context = c;
        this.inflater = LayoutInflater.from(c);
        this.names = names;
        this.times = times;
        this.dates = dates;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.record_adapter,null);
        }

        String name = names.get(position);
        String time = times.get(position);
        String date = dates.get(position);

        TextView textName;
        TextView textTime;
        TextView textDate;

        if(name != null){

            textName = (TextView) convertView.findViewById(R.id.name_text);
            textTime = (TextView) convertView.findViewById(R.id.time_text);
            textDate = (TextView) convertView.findViewById(R.id.date_text);
            textName.setText(name);
            textTime.setText(time);
            textDate.setText(date);

        }

        return convertView;
    }
}
