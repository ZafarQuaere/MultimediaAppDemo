package zafar.multimediademo.acitivities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import zafar.multimediademo.R;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.Constants;
import zafar.multimediademo.utility.MyJsonObjectRequest;
import zafar.multimediademo.utility.Utils;


/**
 * Created by zafar.imam on 28-12-2016.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
       {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private Button btnLogin;
    private TextView txtForgotPassword;
    private TextView txtRegister;
    private EditText editEmail;
    private EditText editPassword;
    private String email ;
    private String password;
    private AppDialogLoader loader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
       /* if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }*/
        setContentView(R.layout.activity_login);
        initUI();
        loader = AppDialogLoader.getLoader(this);

    }

    private void initUI() {


        btnLogin = (Button) findViewById(R.id.btnLogin);


        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        txtRegister.setPaintFlags(txtRegister.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);


        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
    }


    private void signOut() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {


            case R.id.btnLogin:
               startActivity(new Intent(LoginActivity.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //login();
                break;

            case R.id.txtForgotPassword:
                startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;

            case R.id.txtRegister:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;


        }
    }

    private void login() {

         email = editEmail.getText().toString();
         password = editPassword.getText().toString();
        if (email.equalsIgnoreCase("")){
            editEmail.setError(getString(R.string.enter_email));
            editEmail.requestFocus();
        }
        else if (password.equalsIgnoreCase("")){
            editPassword.setError(getString(R.string.enter_password));
            editPassword.requestFocus();
        }
        else if (!Utils.isValidEmail(email)){
            editEmail.setError(getString(R.string.enter_valid_email));
            editEmail.requestFocus();
        }
        else if (password.length()<6){
            editPassword.setError(getString(R.string.password_length));
            editPassword.requestFocus();
        }
        else if (!Utils.isInternetConnected(this)){
            Utils.showPopUp(this, getString(R.string.internet_alert), null);
        }
        else {

            //Call Api from here ..
            callLoginApi();
        }
    }


    private void callLoginApi() {

        String tag_json_obj = "json_obj_req";
        // String url = getString(R.string.URL_BASE) + getString(R.string.URL_METHOD_SELLER_LIST) + getString(R.string.URL_MANAGE_YOUR_PASS);

       // String url = "http://activateyourproducts.com/checkLogin.php";

        loader.show();
        Utils.DEBUG("Callingggggg login Api...........");
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("email",email );
            requestObject.put("password", password);
        } catch (Exception e) {
            Utils.ERROR("Error while creating json request : " + e.toString());
        }
        MyJsonObjectRequest jsonObjReq = new MyJsonObjectRequest(
                false,
                this,
                Request.Method.POST,
                Constants.LOGIN_URL,
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            return;
                        }
                        Utils.DEBUG("Login Response : " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int result = jsonObject.optInt("success");
                            if (result == 1){
                                Utils.showToast(LoginActivity.this,jsonObject.optString("message"));
                                Utils.setLoginEmail(LoginActivity.this,email);
                                Utils.setLoginPassword(LoginActivity.this,password);
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                            else {
                                Utils.showToast(LoginActivity.this,jsonObject.optString("message"));
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
                Utils.showToast(LoginActivity.this, "There is something wrong with API");
                loader.dismiss();
            }
        }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


}
