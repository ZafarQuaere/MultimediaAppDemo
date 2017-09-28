package zafar.multimediademo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import zafar.multimediademo.R;
import zafar.multimediademo.adapter.CustomListAdapter;
import zafar.multimediademo.model.SearchData;
import zafar.multimediademo.model.SearchDetail;
import zafar.multimediademo.parser.ParseManager;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.MyJsonObjectRequest;
import zafar.multimediademo.utility.Utils;


/**
 * Created by Zafar on 09-01-2017.
 */

public class SearchFragment extends Fragment {
    private View view;
    private ListView listView;
    private AppDialogLoader loader;
   // private SearchAdapter searchAdapter;
    private CustomListAdapter adapter;
    private ArrayList<SearchDetail> searchList;
    private EditText editSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        initUI(view);
        callSearchApi();
        return view;
    }



    private void initUI(View view) {
        loader = AppDialogLoader.getLoader(getActivity());

        editSearch = (EditText) view.findViewById(R.id.editSearch);


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listView = (ListView) view.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user = searchList.get(position).getName();
                Utils.moveToFragment(getActivity(),new UserProfileFragment(),searchList.get(position),0);
                Utils.showToast(getActivity(),"username : "+user);
            }
        });

    }

    private void callSearchApi() {
        String tag_json_obj = "json_obj_req";
        // String url = getString(R.string.URL_BASE) + getString(R.string.URL_METHOD_SELLER_LIST) + getString(R.string.URL_MANAGE_YOUR_PASS);

        String url = "http://activateyourproducts.com/search.php";

        loader.show();
        Utils.DEBUG("Callingggggg login Api...........");
        JSONObject requestObject = new JSONObject();

        MyJsonObjectRequest jsonObjReq = new MyJsonObjectRequest(
                false,
                getActivity(),
                Request.Method.POST,
                url,
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            return;
                        }
                        Utils.DEBUG("Search Response : " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            SearchData data = ParseManager.getInstance().fromJSON(jsonObject, SearchData.class);
                            int result = jsonObject.optInt("success");
                            if (result == 1){
                                 searchList = data.getDetails();
                               // searchAdapter = new SearchAdapter(getActivity(),R.layout.list_item,searchList);
                                adapter = new CustomListAdapter(getActivity(),searchList);
                                listView.setAdapter(adapter);
                                //listView.setAdapter(searchAdapter);
                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loader.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.ERROR("Error: " + error);
                Utils.showToast(getActivity(), "There is something wrong with API");
                loader.dismiss();
            }
        }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
}
