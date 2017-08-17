package com.example.tapan.digitallibraryadmin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.model.BookName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LibrarianInfoFragment extends Fragment implements View.OnClickListener{

    private EditText edt_lib_name, edt_lib_id;
    private Button btn_enter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference libDatabaseReference;
    private String librarianName, librarianId, librarianUid;
    private BookName obj_bookName = new BookName();
    Intent intent;

    public LibrarianInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_librarian_info, container, false);

        edt_lib_id = (EditText) view.findViewById(R.id.edt_lib_id);
        edt_lib_name = (EditText) view.findViewById(R.id.edt_lib_name);
        btn_enter = (Button) view.findViewById(R.id.btn_enter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        librarianUid = firebaseUser.getUid();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData");

        btn_enter.setOnClickListener(this);

        return view;
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
