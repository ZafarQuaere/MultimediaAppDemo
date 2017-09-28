package zafar.multimediademo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import zafar.multimediademo.R;
import zafar.multimediademo.model.SearchDetail;


/**
 * Created by Zafar on 10-01-2017.
 */

public class SearchAdapter extends ArrayAdapter<SearchDetail>{

    ArrayList<SearchDetail> originalList;
    ArrayList<SearchDetail> searchList;
    private SearchFilter filter;
    private Context context;

    public SearchAdapter(Context context, int list_item, ArrayList<SearchDetail> searchList) {
        super(context,list_item,searchList);

        this.originalList = searchList;
        originalList.addAll(searchList);
        this.searchList = searchList;
        searchList.addAll(searchList);
        this.context = context;

    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new SearchFilter();
        }
        return filter;
    }


    private class ViewHolder {
        TextView name;
        TextView email;
        TextView gender;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item, null);

            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.txtUsername);
            holder.email = (TextView) convertView.findViewById(R.id.txtEmail);
           // holder.gender = (TextView) convertView.findViewById(R.id.gender);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchDetail country = searchList.get(position);
        holder.name.setText(country.getName());
        holder.email.setText(country.getEmail());
       // holder.region.setText(country.getRegion());

        return convertView;

    }



    private class SearchFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<SearchDetail> filteredItems = new ArrayList<SearchDetail>();

                for(int i = 0, l = originalList.size(); i < l; i++)
                {
                    SearchDetail searchDetail = originalList.get(i);
                    if(searchDetail.toString().toLowerCase().contains(constraint))
                        filteredItems.add(searchDetail);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            searchList = (ArrayList<SearchDetail>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = searchList.size(); i < l; i++)
                add(searchList.get(i));
            notifyDataSetInvalidated();
        }
    }


}

