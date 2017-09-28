package zafar.multimediademo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zafar.multimediademo.R;
import zafar.multimediademo.model.Detail;
import zafar.multimediademo.model.ProfileData;
import zafar.multimediademo.model.SearchDetail;
import zafar.multimediademo.parser.ParseManager;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.AppSharedPrefs;
import zafar.multimediademo.utility.Constants;
import zafar.multimediademo.utility.Utils;


/**
 * Created by Zafar on 10-01-2017.
 */
public class UserProfileFragment extends Fragment {

    private View view;
    private TextView txtFollow;
    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtPhoneNumber;
    private AppSharedPrefs sharedPrefs;
    private SearchDetail searchDetail;
    private AppDialogLoader loader;
    private String profileName, followEmail,profilePhone;
    private String userEmail ;
    String webUrl = "";
    private WebView webViewProifle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_profile_fragment,container,false);
        loader = AppDialogLoader.getLoader(getActivity());
        sharedPrefs = AppSharedPrefs.getInstance(getActivity());
        initUI();
        searchDetail = (SearchDetail) getArguments().get(getActivity().getString(R.string.key_serializable));
        followEmail = searchDetail.getEmail();
        Utils.DEBUG("Selected Email >>>>>> "+followEmail);
        callProfileApi(followEmail);
        userEmail = Utils.getLoginEmail(getActivity());
        loader.show();
        Utils.DEBUG("User Email :"+userEmail);
        webUrl = "http://activateyourproducts.com/profile.php?email="+userEmail;
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("DetailFragment", "onActivityCreated()");
        if (savedInstanceState != null) {
            webUrl = savedInstanceState.getString("currentURL", "");
        }
        if(!webUrl.trim().equalsIgnoreCase("")){
            webViewProifle = (WebView) getView().findViewById(R.id.webViewProifle);
            webViewProifle.getSettings().setJavaScriptEnabled(true);
            webViewProifle.setWebViewClient(new MyWebViewClient());
            webViewProifle.loadUrl(webUrl.trim());
        }
    }

    private void initUI() {
        loader.show();
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
        txtFollow = (TextView) view.findViewById(R.id.txtFollow);
        txtFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFollow.setText(txtFollow.getText().toString().equalsIgnoreCase("Follow")?"Following":"Follow");

            if (txtFollow.getText().toString().equalsIgnoreCase("Following")){
                callFollowApi(userEmail,followEmail);
            }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentURL", webUrl);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loader.dismiss();
        }
    }


    private void callFollowApi(final String fromEmail,final String followEmail) {
        String tag_json_obj = "json_obj_req";
        loader.show();
       // String url = "http://activateyourproducts.com/follow.php";
        Utils.DEBUG(Constants.FOLLOW_URL +" "+fromEmail +" "+followEmail);
        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.FOLLOW_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.DEBUG(" Follow response ------> " + response);
                loader.dismiss();
                if (response == null) {
                    return;
                }

                loader.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                loader.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("from_email", fromEmail);
                params.put("follow_email", followEmail);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }


    private void callProfileApi(final String email) {
        String tag_json_obj = "json_obj_req";
       // String url = "http://activateyourproducts.com/getProfile.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.PROFILE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.DEBUG(" Profile response ------> " + response.toString());
                //msgResponse.setText(response.toString());
                loader.dismiss();
                if (response == null) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(response);
                    ProfileData data = ParseManager.getInstance().fromJSON(object, ProfileData.class);

                    int success = object.optInt("success");
                    if (success == 1){
                        ArrayList<Detail> profDetail = data.getDetails();

                        for (int i = 0; i < profDetail.size(); i++) {
                            profileName = profDetail.get(0).getName();
                            Utils.DEBUG("Name :"+profileName);
                            followEmail = profDetail.get(0).getEmail();
                            profilePhone = profDetail.get(0).getPhone();

                        }
                        txtUsername.setText(profileName);
                        txtEmail.setText(followEmail);
                        txtPhoneNumber.setText(profilePhone);

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
                VolleyLog.d("Error: " + error.getMessage());
                loader.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }
}
