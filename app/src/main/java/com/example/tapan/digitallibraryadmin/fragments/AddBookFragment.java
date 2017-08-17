package com.example.tapan.digitallibraryadmin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapan.digitallibraryadmin.model.BookName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.example.tapan.digitallibraryadmin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookFragment extends Fragment implements View.OnClickListener{

    private EditText edt_book_name, edt_author_name, edt_publication, edt_price, edt_book_id, edt_pages, edt_quantity;
    private Button btn_submit;
    private String bookName, bookId;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, bookUidDatabaseReference, libDatabaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String librarianUid=null, librarianId;
    private Intent intent;
    private BookName obj_bookName1 = new BookName();

    public AddBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference().child("bookData");
        librarianUid = firebaseUser.getUid();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData");
        //librarianId = intent.getStringExtra("librarianId");
        //Log.d("librarianID", librarianId);

        initViews(view);

        btn_submit.setOnClickListener(this);
        return view;
    }

    private void btn_submit(){
        if(isEmpty()){
            //checks if the fields are empty or not

            bookName = edt_book_name.getText().toString();
            bookId = edt_book_id.getText().toString();
            //librarianId = obj_bookName1.getLibrarianId();                //NOT WORKING...... IDK WHY >_<
            //librarianId = firebaseUser.getUid();
            //Log.d("libID0", librarianId);

            databaseReference = firebaseDatabase.getReference().child("bookData").child(bookName+bookId);
            bookUidDatabaseReference = databaseReference.child("bookUid");

            Log.d("bookName", edt_book_name.getText().toString());
            databaseReference.child("bookDbName").setValue(bookName+bookId);
            databaseReference.child("bookName").setValue(edt_book_name.getText().toString());
            databaseReference.child("authorName").setValue(edt_author_name.getText().toString());
            databaseReference.child("publication").setValue(edt_publication.getText().toString());
            databaseReference.child("price").setValue(edt_price.getText().toString());
            databaseReference.child("bookId").setValue(edt_book_id.getText().toString());
            databaseReference.child("pages").setValue(edt_pages.getText().toString());
            databaseReference.child("quantity").setValue(edt_quantity.getText().toString());
            databaseReference.child("availableBooks").setValue(edt_quantity.getText().toString());
            databaseReference.child("Time").setValue(ServerValue.TIMESTAMP);
            databaseReference.child("issues").setValue(0);
            //databaseReference.child("bookURL").setValue("https://firebasestorage.googleapis.com/v0/b/digitallibraryadmin.appspot.com/o/AIPol.jpg?alt=media&token=3ffd36c4-882b-43e9-9201-cbfefe3a24cf"bookName+bookId+".jpg")

            int quantity = Integer.parseInt(edt_quantity.getText().toString());
            Log.d("quantity", String.valueOf(quantity));
            for(int i = 0; i< quantity; i++){
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).setValue(0);
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).child("issuedBy").setValue("");
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).child("issuedTo").setValue("");
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).child("issuedOn").setValue("");
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).child("returnedTo").setValue("");
                bookUidDatabaseReference.child("availableBooks").child(edt_book_id.getText().toString()+i).child("returnedOn").setValue("");

                Log.d("quantity", String.valueOf(quantity)+i);
            }

            //Log.d("libID", librarianId + bookId );
            libDatabaseReference.child("786786").child("addedBooks").child(bookId).child("bookId").setValue(bookId);
            libDatabaseReference.child("786786").child("addedBooks").child(bookId).child("bookName").setValue(bookName);
            //libDatabaseReference.child("786786").child("addedBooks").child(bookDbName).child("bookDbName").setValue(bookDbName);
            libDatabaseReference.child("786786").child("addedBooks").child(bookId).child("issuedOn").setValue(ServerValue.TIMESTAMP);
            libDatabaseReference.child("786786").child("addedBooks").child(bookId).child("numberOfBooks").setValue(quantity);

            Toast.makeText(getContext(), "Book Added", Toast.LENGTH_LONG).show();
            edt_book_name.setText("");
            edt_author_name.setText("");
            edt_publication.setText("");
            edt_price.setText("");
            edt_book_id.setText("");
            edt_pages.setText("");
            edt_quantity.setText("");
        }
    }

    private void initViews(View view){
        edt_book_name = (EditText) view.findViewById(R.id.edt_book_name);
        edt_author_name = (EditText) view.findViewById(R.id.edt_author_name);
        edt_publication = (EditText) view.findViewById(R.id.edt_publication);
        edt_price= (EditText) view.findViewById(R.id.edt_price);
        edt_book_id = (EditText) view.findViewById(R.id.edt_book_id);
        edt_pages = (EditText) view.findViewById(R.id.edt_pages);
        edt_quantity = (EditText) view.findViewById(R.id.edt_quantity);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
    }

    private boolean isEmpty(){
        if(TextUtils.isEmpty(edt_book_name.getText().toString())) {
            edt_book_name.setError("Enter Book Name");
            return false;
        }
        if(TextUtils.isEmpty(edt_author_name.getText().toString())) {
            edt_author_name.setError("Enter Author Name");
            return false;
        }
        if(TextUtils.isEmpty(edt_publication.getText().toString())) {
            edt_publication.setError("Enter Publication");
            return false;
        }
        if(TextUtils.isEmpty(edt_price.getText().toString())) {
            edt_price.setError("Enter Price");
            return false;
        }
        if(TextUtils.isEmpty(edt_book_id.getText().toString())) {
            edt_book_id.setError("Enter Book ID");
            return false;
        }
        if(TextUtils.isEmpty(edt_pages.getText().toString())) {
            edt_pages.setError("Enter Pages");
            return false;
        }
        if(TextUtils.isEmpty(edt_quantity.getText().toString())) {
            edt_quantity.setError("Enter Quantity");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == btn_submit){
            btn_submit();
        }
    }
}
