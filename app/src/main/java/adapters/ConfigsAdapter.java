package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.luislopez.poprocess.R;

import java.util.ArrayList;

import preferences.CustomPreferences;

/**
 * Created by LuisLopez on 11/23/14.
 */
public class ConfigsAdapter extends BaseAdapter {

    private ArrayList<String> data;
    private LayoutInflater inflater;
    private Context context;


    public ConfigsAdapter(Context c, ArrayList<String> d){
        this.data = d;
        this.context = c;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListData(ArrayList<String> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null)convertView = inflater.inflate(R.layout.configs_adapter, null);

        String item = data.get(position);
        TextView name;
        Button delete;
        Button edit;
        if(item != null){
            final String nameString = data.get(position);
            name = (TextView) convertView.findViewById(R.id.config_text);
            delete = (Button) convertView.findViewById(R.id.erase_button);
            edit = (Button) convertView.findViewById(R.id.edit_button);
            name.setText(nameString);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> dataNames;
                    SharedPreferences preferences;
                    CustomPreferences customPreferences;
                    customPreferences = new CustomPreferences();
                    preferences = parent.getContext().getSharedPreferences("CUSTOM_PROCESS", Context.MODE_PRIVATE);
                    dataNames = customPreferences.loadAllNames(preferences);
                    customPreferences.deletePreferences(preferences, nameString);
                    dataNames.remove(nameString);
                    setListData(dataNames);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setConfig = new Intent(context,com.luislopez.poprocess.New.class);
                    setConfig.putExtra("NAME_EXTRA",nameString);
                    parent.getContext().startActivity(setConfig);
                }
            });

        }

        return convertView;
    }
}
