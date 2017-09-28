package zafar.multimediademo.acitivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import zafar.multimediademo.R;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.Constants;
import zafar.multimediademo.utility.Utils;


/**
 * Created by zafar.imam on 28-12-2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtLogin;
    private EditText editName;
    private EditText editMobileNumber;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button btnRegister;
    private AppDialogLoader loader;
    private String name;
    private String phoneNO;
    private String email;
    private String password;
    private String gender;
    private RadioButton radioMale,radioFemale;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loader = AppDialogLoader.getLoader(this);
        initUI();
    }

    private void initUI() {
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        editName = (EditText) findViewById(R.id.editName);
        editMobileNumber = (EditText) findViewById(R.id.editMobileNumber);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);

        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFeMale);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.radioMale:
                        String male = radioMale.getText().toString();
                        //Utils.showToast(getApplicationContext(),male);
                        gender = 1+"";
                        break;

                    case R.id.radioFeMale:
                        String female = radioFemale.getText().toString();
                       // Utils.showToast(getApplicationContext(),female);
                        gender = 0+"";
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.txtLogin:
                onBackPressed();
                break;

            case R.id.btnRegister:
                //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //loader.show();
                if (getResources().getBoolean(R.bool.validate)){
                    register();
                }
                else {
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

                break;
        }

    }

    private void register() {
         name = editName.getText().toString();
         phoneNO = editMobileNumber.getText().toString();
         email = editEmail.getText().toString();
         password = editPassword.getText().toString();

        String confPassword = editConfirmPassword.getText().toString();


        if (name.equalsIgnoreCase("")){
            editName.setError(getString(R.string.enter_name));
            editName.requestFocus();
        }
        else if (phoneNO.equalsIgnoreCase("")){
            editMobileNumber.setError(getString(R.string.enter_phone_no));
            editMobileNumber.requestFocus();

        }
        else if (phoneNO.length()<10){
            editMobileNumber.setError(getString(R.string.enter_valid_phone));
            editMobileNumber.requestFocus();
        }
        else if (email.equalsIgnoreCase("")){
            editEmail.setError(getString(R.string.enter_email));
            editEmail.requestFocus();
        }
        else if (!Utils.isValidEmail(email)){
            editEmail.setError(getString(R.string.enter_valid_email));
            editEmail.requestFocus();
        }
        else if (password.equalsIgnoreCase("")){
            editPassword.setError(getString(R.string.enter_password));
            editPassword.requestFocus();
        }
        else if (password.length()<6){
            editPassword.setError(getString(R.string.password_length));
            editPassword.requestFocus();
        }
        else if (confPassword.equalsIgnoreCase("")){
                editConfirmPassword.setError(getString(R.string.enter_confirm_pass));
                editConfirmPassword.requestFocus();
        }
        else if (!confPassword.matches(password)){
            editConfirmPassword.setError(getString(R.string.password_doest_matching));
            editConfirmPassword.requestFocus();
        }
        else if (!Utils.isInternetConnected(this)){
            Utils.showPopUp(this, getString(R.string.internet_alert), null);
        }

        else {
            /*startActivity(new Intent(RegisterActivity.this,ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);*/
            //Call Register Api Here..
           // registerApiCall();
            registerStringApi();

        }
    }


    private void registerStringApi() {

        String tag_json_obj = "json_obj_req";
        loader.show();
       // String url = "http://activateyourproducts.com/registration.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.DEBUG(" Register response ------> " + response.toString());
                //msgResponse.setText(response.toString());
                loader.dismiss();
                if (response == null) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    int result = jsonObject.optInt("success");
                    if (result == 1){
                        Utils.showToast(RegisterActivity.this,jsonObject.optString("message"));
                        Utils.setLoginEmail(RegisterActivity.this,email);
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                    else {
                        Utils.showToast(RegisterActivity.this,jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
                params.put("password", password);
                params.put("name", name);
                params.put("gender", gender);
                params.put("phone", phoneNO);


                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }


    /*private void registerApiCall() {
        String tag_json_obj = "json_obj_req";
        // String url = getString(R.string.URL_BASE) + getString(R.string.URL_METHOD_SELLER_LIST) + getString(R.string.URL_MANAGE_YOUR_PASS);
        String url = "http://activateyourproducts.com/registration.php";

        loader.show();
        Utils.DEBUG("Callingggggg register Api...........");
        JSONObject requestObject = new JSONObject();
        try {

            requestObject.put("email", email);
            requestObject.put("password", password);
            requestObject.put("username", name);
            requestObject.put("gender", gender);
            requestObject.put("phone", phoneNO);
        } catch (Exception e) {
            Utils.ERROR("Error while creating json request : " + e.toString());
        }
        MyJsonObjectRequest jsonObjReq = new MyJsonObjectRequest(
                false,
                this,
                Request.Method.POST,
                url,
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            return;
                        }
                        Utils.DEBUG("Register Response: " + response.toString());

                        if (response == null) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int result = jsonObject.optInt("success");
                            if (result == 1){
                                Utils.showToast(RegisterActivity.this,jsonObject.optString("message"));
                                Utils.setLoginEmail(RegisterActivity.this,email);
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                            else {
                                Utils.showToast(RegisterActivity.this,jsonObject.optString("message"));
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
                Utils.showToast(RegisterActivity.this, "There is something wrong with API");
                loader.dismiss();
            }
        }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }*/
}
