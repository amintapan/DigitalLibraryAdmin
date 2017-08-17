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
import android.widget.Toast;

import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.activity.BookInfoActivity;
import com.example.tapan.digitallibraryadmin.activity.UserInfoActivity;
import com.example.tapan.digitallibraryadmin.model.Search;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookHistoryFragment extends Fragment implements View.OnClickListener{
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookDatabaseReference, userDatabaseReference;
    private EditText bookName, bookUid, enrollNumber;
    private Button btnBookName, btnBookUid, btnEnrollNumber;
    private String bookDbName;
    Intent intent;
    private Search search = new Search();

    public BookHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_history, container, false);

        firebaseDatabase = firebaseDatabase.getInstance();
        bookDatabaseReference = firebaseDatabase.getReference().child("bookData");
        userDatabaseReference = firebaseDatabase.getReference().child("userData");

        bookName = (EditText) view.findViewById(R.id.bookName);
        bookUid = (EditText) view.findViewById(R.id.bookUid);
        enrollNumber = (EditText) view.findViewById(R.id.enrollNumber);

        btnBookName = (Button) view.findViewById(R.id.btn_book_name);
        btnBookUid = (Button) view.findViewById(R.id.btn_book_Uid);
        btnEnrollNumber = (Button) view.findViewById(R.id.btn_enroll_number);

        btnBookName.setOnClickListener(this);
        btnBookUid.setOnClickListener(this);
        btnEnrollNumber.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnBookName){
            if(bookName.getText().toString().isEmpty()){
                bookName.setError("Enter Book DB Name");
            } else{
                Log.d("bookName", "book DB name is" +bookName.getText().toString());
                btnBookName();
            }
        }
        if(view == btnBookUid){
            if(bookUid.getText().toString().isEmpty()) {
                bookUid.setError("Enter Book UID");
            } else{
                btnBookUid();
            }
        }
        if(view == btnEnrollNumber){
            if(enrollNumber.getText().toString().isEmpty()) {
                enrollNumber.setError("Enter Enrollment Number");
            } else{
                btnEnrollNumber();
            }
        }
    }

    private void btnBookName() {
        //here bookName is bookDbName
        Query query = bookDatabaseReference.orderByChild("bookDbName").equalTo(bookName.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("bookName", String.valueOf(dataSnapshot.getValue()));
//                        bookDbName = dataSnapshot.child("CPUipl").child("bookName").getValue().toString().trim()+dataSnapshot.child("CPUipl").child("bookId").getValue().toString().trim();
//                        Log.d("bookDbName", bookDbName);
                        if(dataSnapshot.child(bookName.getText().toString()).exists()){
                            Search.setBookName(dataSnapshot.child(bookName.getText().toString()).child("bookName").getValue().toString().trim());
                            Search.setBookDbName(bookName.getText().toString());

                            intent = new Intent(getContext(), BookInfoActivity.class);
                            startActivity(intent);
                            //Log.d("bookDbName", bookDbName);
                        } else{
                            Toast.makeText(getContext(), "No Such Book Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        });
    }

    private void btnBookUid() {
        Query query = bookDatabaseReference.child("bookUid").orderByChild("availableBooks").equalTo(bookUid.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("bookName", String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void btnEnrollNumber() {
        Query query = userDatabaseReference.orderByChild("userId").equalTo(enrollNumber.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(enrollNumber.getText().toString()).exists()){
                    Search.setEnrollNumber(enrollNumber.getText().toString().trim());
                    intent = new Intent(getContext(), UserInfoActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getContext(), "No Such User Found", Toast.LENGTH_SHORT).show();
                }
                Log.d("bookName", String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
