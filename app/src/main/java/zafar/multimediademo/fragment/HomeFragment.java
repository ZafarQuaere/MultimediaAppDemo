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

import zafar.multimediademo.R;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.Utils;


/**
 * Created by Zafar on 07-01-2017.
 */
public class HomeFragment extends Fragment {

    private View view;
    String mURL = "";
    private String userEmail;
    private AppDialogLoader loader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        Log.v("DetailFragment", "onCreateView()");
        userEmail = Utils.getLoginEmail(getActivity());
        Utils.DEBUG("User Email :"+userEmail);
        loader.show();
        mURL = "http://activateyourproducts.com/home.php?email="+userEmail;
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("DetailFragment", "onCreate()");
        loader = AppDialogLoader.getLoader(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("DetailFragment", "onActivityCreated()");
        if (savedInstanceState != null) {
            mURL = savedInstanceState.getString("currentURL", "");
        }
        if(!mURL.trim().equalsIgnoreCase("")){
            WebView myWebView = (WebView) getView().findViewById(R.id.webViewHome);
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.setWebViewClient(new MyWebViewClient());
            myWebView.loadUrl(mURL.trim());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentURL", mURL);
    }


    public void setURLContent(String URL) {
        mURL = URL;
    }

    public void updateURLContent(String URL) {
        mURL = URL;
        WebView myWebView = (WebView) getView().findViewById(R.id.webViewHome);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(mURL.trim());
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
}
