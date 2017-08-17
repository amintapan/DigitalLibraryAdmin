package com.example.tapan.digitallibraryadmin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tapan.digitallibraryadmin.AddBookActivity;
import com.example.tapan.digitallibraryadmin.IssueBookActivity;
import com.example.tapan.digitallibraryadmin.LibrarianInfoActivity;
import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.ReturnBookActivity;
import com.example.tapan.digitallibraryadmin.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    Button btn_add_book, btn_issue_book, btn_return_book, btn_librarian_info, btn_log_out, btn_drawer;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add_book = (Button) findViewById(R.id.btn_add_book);
        btn_issue_book = (Button) findViewById(R.id.btn_issue_book);
        btn_return_book = (Button) findViewById(R.id.btn_return_book);
        btn_librarian_info = (Button) findViewById(R.id.btn_librarian_info);
        btn_log_out = (Button) findViewById(R.id.btn_log_out);
        btn_drawer = (Button) findViewById(R.id.btn_drawer);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_add_book.setOnClickListener(this);
        btn_issue_book.setOnClickListener(this);
        btn_return_book.setOnClickListener(this);
        btn_librarian_info.setOnClickListener(this);
        btn_log_out.setOnClickListener(this);
        btn_drawer.setOnClickListener(this);
    }

    private void btn_issue_book(){
        intent = new Intent();
        intent.setClass(MainActivity.this, IssueBookActivity.class);
        startActivity(intent);
    }

    private void btn_add_book(){
        intent = new Intent();
        intent.setClass(MainActivity.this, AddBookActivity.class);
        startActivity(intent);
    }

    private void btn_return_book(){
        intent = new Intent();
        intent.setClass(MainActivity.this, ReturnBookActivity.class);
        startActivity(intent);
    }

    private void btn_librarian_info(){
        intent = new Intent();
        intent.setClass(MainActivity.this, LibrarianInfoActivity.class);
        startActivity(intent);
    }

    private void btn_log_out(){
        intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        firebaseAuth.signOut();
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == btn_issue_book) {
            btn_issue_book();
        }
        if(view == btn_add_book){
            btn_add_book();
        }
        if(view == btn_return_book){
            btn_return_book();
        }
        if(view == btn_librarian_info){
            btn_librarian_info();
        }
        if(view == btn_log_out){
            btn_log_out();
        }
        if(view == btn_drawer) {
            Intent i = new Intent(this, Main2Activity.class);
            startActivity(i);
        }
    }
}