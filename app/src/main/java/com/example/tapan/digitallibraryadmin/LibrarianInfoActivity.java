package com.example.tapan.digitallibraryadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tapan.digitallibraryadmin.model.BookName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LibrarianInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edt_lib_name, edt_lib_id;
    private Button btn_enter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference libDatabaseReference;
    private String librarianName, librarianId, librarianUid;
    private BookName obj_bookName = new BookName();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_info);

        edt_lib_id = (EditText) findViewById(R.id.edt_lib_id);
        edt_lib_name = (EditText) findViewById(R.id.edt_lib_name);
        btn_enter = (Button) findViewById(R.id.btn_enter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        librarianUid = firebaseUser.getUid();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData");

        btn_enter.setOnClickListener(this);

    }

    private void btn_enter(){
        libDatabaseReference.child("uidAndId").child(librarianUid).setValue(librarianId);
        libDatabaseReference.child(librarianId);
        libDatabaseReference.child(librarianId).child("Name").setValue(librarianName);
        libDatabaseReference.child(librarianId).child("Unique ID").setValue(librarianUid);
        obj_bookName.setLibrarianId(librarianId);
        Log.d("libID", obj_bookName.getLibrarianId());
        //obj_bookName1.setLibrarianName(librarianName);

    }

    @Override
    public void onClick(View view) {
        if(view == btn_enter){
            librarianName = edt_lib_name.getText().toString();
            librarianId = edt_lib_id.getText().toString();
            btn_enter();
        }
    }
}
