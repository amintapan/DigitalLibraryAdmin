package com.example.tapan.digitallibraryadmin.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapan.digitallibraryadmin.activity.Main2Activity;
import com.example.tapan.digitallibraryadmin.model.BookName;
import com.example.tapan.digitallibraryadmin.activity.MainActivity;
import com.example.tapan.digitallibraryadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPassword;
    private TextInputLayout layoutEmail, layoutPassword;
    private Button btnSignIn;
    private TextView txtForgotPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference libDatabaseReference;
    private BookName book = new BookName();
    String librarianUid, librarianId, userUid=null;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initView();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData").child("uidAndId");
        //librarianUid = firebaseUser.getUid();
        /*firebaseDatabase = FirebaseDatabase.getInstance();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData").child("uidAndId");



        libDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                librarianId = dataSnapshot.child(librarianUid).getValue().toString();
                Log.d("librarianId", librarianId);
                book.setLibrarianId(librarianId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //String librarianId = firebaseUser.

        if(firebaseUser !=null ){
            //Go to Main Activity(Already logged in)
            startActivity(new Intent(LoginActivity.this, Main2Activity.class));
            //intent.putExtra("librarianId", librarianId);
            finish();
        }

        btnSignIn.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
    }

    //For signing in with email and password
    private void signIn() {
        String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();
        Log.d("toString", "email and password converted to string");
        if (email.isEmpty()) {
            edtEmail.setError("Enter Email");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Enter Password");
            return;
        }
        if (password.length()<6){
            edtPassword.setError("Minimum 6 characters required");
        }

        progressDialog.setMessage("Signing In");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.cancel();
                if (!task.isSuccessful()) {
                    //ERROR
                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("signIn", "Sign In credentials are correct but check if it is a librarian or a user");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    userUid = firebaseUser.getUid();
                    Log.d("uid", userUid);
                    isUser();
                }
            }
        });

    }

    private void isUser(){
        libDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userUid).exists()){
                    Log.d("isUser", "User is a librarian");
                    Toast.makeText(LoginActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                    intent = new Intent();
                    intent.setClass(LoginActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                } else{
                    Log.d("isUser", "User is not a librarian");
                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Unable to connect at this time", Toast.LENGTH_LONG).show();
                Log.d("isUser", databaseError.getDetails());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignIn) {
            signIn();
        }

        if (view == txtForgotPassword){
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        }
    }

    private void initView(){
        layoutEmail = (TextInputLayout) findViewById(R.id.layout_email);
        layoutPassword = (TextInputLayout) findViewById(R.id.layout_password);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        txtForgotPassword = (TextView) findViewById(R.id.txt_forgot_password);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}
