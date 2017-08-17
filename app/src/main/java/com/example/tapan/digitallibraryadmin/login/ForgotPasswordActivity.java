package com.example.tapan.digitallibraryadmin.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapan.digitallibraryadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmailForgot;
    private String emailForgot;
    private Button btnReset;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initView();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailForgot = edtEmailForgot.getText().toString().trim();

                if (TextUtils.isEmpty(emailForgot)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.setMessage("Sending Email");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

                auth.sendPasswordResetEmail(emailForgot)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password on your email!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.cancel();
                            }
                        });
            }
        });
    }

    private void initView(){
        edtEmailForgot = (EditText) findViewById(R.id.edt_email_forgot);
        btnReset = (Button) findViewById(R.id.btn_reset);
        auth = FirebaseAuth.getInstance();
    }
}
