package com.example.tapan.digitallibraryadmin.activity;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.model.Search;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookInfoActivity extends AppCompatActivity {

    private TextView bookNameTV, authorNameTV, publicationTV, issuesTV, availableBooksTV, avgRatingTV, priceTV, pagesTV;
    private ImageView bookIV;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookDatabaseReference;
    private String bookDbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        bookDbName = Search.getBookDbName();
        Log.d("bookDbName", "bookDbname "+bookDbName);

        bookNameTV = (TextView) findViewById(R.id.bookNameTextView);
        authorNameTV = (TextView) findViewById(R.id.authorTextView);
        publicationTV = (TextView) findViewById(R.id.publicationTextView);
        issuesTV = (TextView) findViewById(R.id.issueTextView);
        availableBooksTV = (TextView) findViewById(R.id.availableTextView);
        avgRatingTV = (TextView) findViewById(R.id.avgRatingTextView);
        priceTV = (TextView) findViewById(R.id.priceTextView);
        pagesTV = (TextView) findViewById(R.id.pagesTextView);

        bookIV = (ImageView) findViewById(R.id.bookPhotoImageView);
        Glide.with(this).load("http://eurodroid.com/pics/beginning_android_book.jpg").into(bookIV);

        firebaseDatabase = FirebaseDatabase.getInstance();
        bookDatabaseReference = firebaseDatabase.getReference().child("bookData").child(bookDbName);

        bookDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookNameTV.setText(dataSnapshot.child("bookName").getValue().toString());
                authorNameTV.setText(dataSnapshot.child("authorName").getValue().toString());
                publicationTV.setText(dataSnapshot.child("publication").getValue().toString());
                issuesTV.setText(dataSnapshot.child("issues").getValue().toString());
                availableBooksTV.setText(dataSnapshot.child("availableBooks").getValue().toString());
                if (dataSnapshot.child("avgRating").exists()){
                    avgRatingTV.setText(dataSnapshot.child("avgRating").getValue().toString());
                }
                priceTV.setText(dataSnapshot.child("price").getValue().toString());
                pagesTV.setText(dataSnapshot.child("pages").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
