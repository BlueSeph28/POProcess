package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luislopez.poprocess.R;

import java.util.ArrayList;

import beans.PositionBean;

/**
 * Created by LuisLopez on 11/19/14.
 */
public class PositionAdapter extends BaseAdapter {

    private ArrayList<PositionBean> data;
    private LayoutInflater inflater;

    public PositionAdapter(Context context, ArrayList<PositionBean> position){
        this.inflater = LayoutInflater.from(context);
        this.data = position;
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

    public void setListData(ArrayList<PositionBean> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG",data.toString());
        if(convertView == null){
            convertView = inflater.inflate(R.layout.info_adapter,null);
        }

        PositionBean item = data.get(position);

        TextView name;
        TextView macAddress;

        if(item != null){

        name = (TextView) convertView.findViewById(R.id.name_address);
        macAddress = (TextView) convertView.findViewById(R.id.mac_address);
        name.setText(data.get(position).getName());
        macAddress.setText(data.get(position).getMac());
        }

        return convertView;
    }




}
