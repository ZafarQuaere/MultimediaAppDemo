package zafar.multimediademo.acitivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import zafar.multimediademo.R;
import zafar.multimediademo.utility.Utils;


/**
 * Created by zafar.imam on 28-12-2016.
 */
public class ForgotPassword extends AppCompatActivity {

    private Button btnPassword;
    private EditText editEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmail = (EditText) findViewById(R.id.editEmail);
        btnPassword = (Button) findViewById(R.id.btnPassword);

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                if (email.equalsIgnoreCase("")) {
                    editEmail.setError(getString(R.string.enter_email));
                    editEmail.requestFocus();
                } else if (!Utils.isValidEmail(email)) {
                    editEmail.setError(getString(R.string.enter_valid_email));
                    editEmail.requestFocus();
                } else {
                    callApi(email);
                }
            }
        });
    }

    private void callApi(String email) {
    }
}
