package zafar.multimediademo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import zafar.multimediademo.R;
import zafar.multimediademo.model.SearchDetail;


/**
 * Created by Zafar on 10-01-2017.
 */
public class CustomListAdapter extends BaseAdapter{

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<SearchDetail> worldpopulationlist = null;
    private ArrayList<SearchDetail> arraylist;

    public CustomListAdapter(Context activity, ArrayList<SearchDetail> searchList) {
        mContext = activity;
        this.worldpopulationlist = searchList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<SearchDetail>();
        this.arraylist.addAll(worldpopulationlist);

    }
    public class ViewHolder {
        TextView name;
        TextView email;
        TextView gender;

    }
    @Override
    public int getCount() {
        return worldpopulationlist.size();
    }

    @Override
    public Object getItem(int position) {
        return worldpopulationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.txtUsername);
            holder.email = (TextView) view.findViewById(R.id.txtEmail);
           // holder.population = (TextView) view.findViewById(R.id.population);
            // Locate the ImageView in listview_item.xml

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(worldpopulationlist.get(position).getName());
        holder.email.setText(worldpopulationlist.get(position).getEmail());

       /* view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = worldpopulationlist.get(position).getName();
                Utils.moveToFragment(mContext,new UserProfileFragment(),worldpopulationlist,0);
                Utils.showToast(mContext,"username : "+user);
            }
        });*/
        return view;
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        worldpopulationlist.clear();
        if (charText.length() == 0) {
            worldpopulationlist.addAll(arraylist);
        } else {
            for (SearchDetail wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    worldpopulationlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
