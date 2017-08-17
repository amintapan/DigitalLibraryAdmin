package com.example.tapan.digitallibraryadmin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapan.digitallibraryadmin.Barcode.BarcodeCaptureActivity;
import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.model.BookName;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ReturnBookFragment extends Fragment implements View.OnClickListener{

    private EditText edt_book_name, edt_book_uid, edt_user_id;
    private Button btn_return, btn_book_qr, btn_user_qr;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookDatabaseReference, userDatabaseReference, libDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    int available_books = 0;
    private String userId=null, book_id=null, bookDbName=null, bookName=null, issuedTo=null, issuedBy=null, issuedOn=null;
    private String bookUid=null, librarianName=null, librarianUid=null, librarianId=null;
    private Boolean validBook;
    private BookName obj_bookName = new BookName();
    private boolean gotLibData = false, gotBookData = false, gotUserData = false;
    private static final int BOOK_RC_QRCODE_CAPTURE = 9001;
    private static final int USER_RC_QRCODE_CAPTURE = 9002;

    public ReturnBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_return_book, container, false);
        edt_book_name = (EditText) view.findViewById(R.id.edt_book_name);
        edt_user_id = (EditText) view.findViewById(R.id.edt_user_id);
        edt_book_uid = (EditText) view.findViewById(R.id.edt_book_uid);
        btn_return = (Button) view.findViewById(R.id.btn_return);
        btn_book_qr = (Button) view.findViewById(R.id.btn_book_qr);
        btn_user_qr = (Button) view.findViewById(R.id.btn_user_qr);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        librarianUid = firebaseUser.getUid();

//        bookDatabaseReference = firebaseDatabase.getReference().child("bookData");
//        userDatabaseReference = firebaseDatabase.getReference().child("userData");
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Returning Book");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        btn_return.setOnClickListener(this);
        btn_book_qr.setOnClickListener(this);
        btn_user_qr.setOnClickListener(this);
        return view;
    }

    private void checkAllThreeDone(boolean lib, boolean book, boolean user) {
        if(lib && book && user) {
            Log.d("three flag", "All three are done");
            new ReturnBookFragment.ReturnAsyncTask().execute();
        }
    }


    private void btn_return(){


        Log.d("temp", book_id);
        Log.d("temp", bookDbName);
        bookDatabaseReference = firebaseDatabase.getReference().child("bookData").child(bookDbName);
        userDatabaseReference = firebaseDatabase.getReference().child("userData").child(userId);

        progressDialog.show();
        getLibData();
        getBookData();
        Log.d("not", "after getBookData");

//        new ReturnBookActivity.ReturnAsyncTask().execute();
    }

    class ReturnAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {

            Log.d("time111", "finished preexecute");
            issuedBy = BookName.getIssuedBy();
            Log.d("time111", "issued By" +issuedBy);
            issuedOn = obj_bookName.getIssuedOn();
            bookName = obj_bookName.getBookName();
            available_books = obj_bookName.getAvailableBooks();
            validBook = obj_bookName.getValidBook();
            //librarianId = obj_bookName.getLibrarianId();
            Log.d("time123", librarianId);  //GETS NULL VALUE...........................................
        }

        @Override
        protected Object doInBackground(Object[] params) {
            //String bookUid, bookName;
            Log.d("time111", "in background");
            if(validBook) {

                available_books = available_books + 1;
                /*if (available_books > 0) {
                    available_books = obj_bookName.getAvailableBooks() + 1;
                }*/
                //bookUid = obj_bookName1.getBookId();

                bookDatabaseReference.child("availableBooks").setValue(available_books);
                //bookDatabaseReference.child("Time").setValue(ServerValue.TIMESTAMP);
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("issuedByName").setValue("");
                //Log.d("libname", librarianName);
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("issuedTo").setValue("");
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("issuedOn").setValue("");
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("issuedBy").setValue("");
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("returnedTo").setValue(librarianId);
                bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).child("returnedOn").setValue(ServerValue.TIMESTAMP);
                bookDatabaseReference.child("bookUid").child("issuedBooks").child(bookUid).removeValue();
                Log.d("not", "book removed from issued");

                userDatabaseReference.child("issuedBooks").child(bookUid).removeValue();
                userDatabaseReference.child("returnedBooks").child(bookUid).child("bookUid").setValue(bookUid); ///ave che
                userDatabaseReference.child("returnedBooks").child(bookUid).child("bookName").setValue(bookName);
                userDatabaseReference.child("returnedBooks").child(bookUid).child("issuedOn").setValue(issuedOn);
                userDatabaseReference.child("returnedBooks").child(bookUid).child("issuedBy").setValue(issuedBy);
                userDatabaseReference.child("returnedBooks").child(bookUid).child("returnedOn").setValue(ServerValue.TIMESTAMP);    //ave che
                userDatabaseReference.child("returnedBooks").child(bookUid).child("returnedBy").setValue(librarianName);

                Log.d("time111", librarianId + bookUid);
                libDatabaseReference.child(librarianId).child("returnedBooks").child(bookUid).child("bookUid").setValue(bookUid);
                libDatabaseReference.child(librarianId).child("returnedBooks").child(bookUid).child("bookName").setValue(bookName); //kwngkjsnf
                //libDatabaseReference.child(librarianId).child("returnedBooks").child(bookDbName).child("bookDbName").setValue(bookDbName);
                libDatabaseReference.child(librarianId).child("returnedBooks").child(bookUid).child("returnedOn").setValue(ServerValue.TIMESTAMP);
                libDatabaseReference.child(librarianId).child("returnedBooks").child(bookUid).child("returnedBy").setValue(userId);

                Log.d("book", "Return Done");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(getContext(), "Book Returned", Toast.LENGTH_SHORT).show();
            gotLibData=false;
            gotBookData=false;
            gotUserData=false;
            edt_user_id.setText("");
            edt_book_name.setText("");
            edt_book_uid.setText("");
            progressDialog.cancel();
        }
    }


    /*
* To get the Librarian data such as name, id, uid
* from libDatabaseReference it will ref. the librarianData in the database
* librarian uid is already defined in onCreate frm that
* it will get libId, from that it will get name*/
    private void getLibData(){
        Log.d("time111", "In getLibData");

        libDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnapshot", dataSnapshot.getValue().toString());
                librarianId = dataSnapshot.child("uidAndId").child(librarianUid).getValue().toString();
                librarianName = dataSnapshot.child(librarianId).child("Name").getValue().toString();
                Log.d("libID1","LibID1" +librarianId);
                obj_bookName.setLibrarianId(librarianId);
                Log.d("libID11", "librarianID " +obj_bookName.getLibrarianId());
                gotLibData = true;
                Log.d("three flag", "lib done");
                checkAllThreeDone(gotLibData, gotBookData, gotUserData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("datasnapshot", databaseError.getMessage());
            }
        });
    }

    /*To get book Data such as availble_book, issues
* bookDatabaseReference is ref. to bookData in DB
* these values are used in the post delayed method
* to set the data in firebase while issuing the book*/
    private void getBookData(){

        Log.d("time1112", "In getBookData");
        bookDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("datasnapshot1", dataSnapshot.getValue().toString());
                Log.d("validBook", "Data Change");
                //Log.d("Error",dataSnapshot.getValue().toString());
                available_books = Integer.parseInt(String.valueOf(dataSnapshot.child("bookUid").child("availableBooks").getChildrenCount()));

                if(dataSnapshot.child("bookUid").child("issuedBooks").child(bookUid).exists()){
                    Log.d("validBook", bookUid);
                    obj_bookName.setValidBook(true);
                    bookName = dataSnapshot.child("bookName").getValue().toString();
                    //bookId = dataSnapshot.child("bookId").getValue().toString();
                    obj_bookName.setQuantity(available_books);
                    //obj_bookName.setIssues(issues);
                    //obj_bookName1.setBookId(bookId);
                    obj_bookName.setBookName(bookName);
                    Log.d("validBook", "YES");

                    issuedTo = dataSnapshot.child("bookUid").child("issuedBooks").child(bookUid).child("issuedTo").getValue().toString();
                    gotBookData = true;
                    Log.d("three flag", "book done");
                    checkAllThreeDone(gotLibData, gotBookData, gotUserData);
                    if(issuedTo.equals(userId)){
                        //showAlert();
                        getUserData();
                    } else {
                        Toast.makeText(getContext(), "This Book is issued to " +issuedTo, Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                    return;
                    //Log.d("available", String.valueOf(bookName.getAvailableBooks()));

                    //databaseReference.child("availableBooks").setValue(quantity);
                } else if(dataSnapshot.child("bookUid").child("availableBooks").child(bookUid).exists()){
                    obj_bookName.setValidBook(false);
                    Log.d("time1112", "this boook was not issued");
                    Toast.makeText(getContext(), "This Book Was Not Issued", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    return;
                }
                else {
                    obj_bookName.setValidBook(false);
                    Toast.makeText(getContext(), "Book Not Valid", Toast.LENGTH_SHORT).show();
                    Log.d("validBook", "NO");
                    progressDialog.cancel();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("datasnapshot1", databaseError.getMessage());
            }
        });

        Log.d("time1112", "Out of getBookData");
    }

    private void getUserData(){
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnapshot3", "ds3" +dataSnapshot.getValue());
                Log.d("time111", "bookUID" +bookUid);
                Log.d("time111", "issued by" +dataSnapshot.child("issuedBooks").child(bookUid).child("issuedBy").getValue().toString());
                obj_bookName.setIssuedBy(dataSnapshot.child("issuedBooks").child(bookUid).child("issuedBy").getValue().toString());
                obj_bookName.setIssuedOn(dataSnapshot.child("issuedBooks").child(bookUid).child("issuedOn").getValue().toString());
                gotUserData = true;
                Log.d("three flag", "user done");
                checkAllThreeDone(gotLibData, gotBookData, gotUserData);
/*
                Log.d("data", userId);
                Log.d("data", String.valueOf(dataSnapshot.getChildrenCount()));
                Log.d("data", String.valueOf(dataSnapshot.child("issuedBooks")));
                if(!dataSnapshot.child("issuedBooks").child(bookUid).exists()){
                    Log.d("getUserData", String.valueOf(dataSnapshot.child(userId).child("issuedBooks").getValue()));
                    Toast.makeText(ReturnBookActivity.this, "This book was not issued to " + userId, Toast.LENGTH_SHORT).show();
                    Log.d("validBook", "NO");
                    obj_bookName.setValidBook(false);
                    progressDialog.cancel();
                    return;
                }
*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getuserData", databaseError.getDetails());
                Toast.makeText(getContext(), "Unable to Fetch data at this time!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
/*
    private void showAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReturnBookActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Issued to some other ID");

        // Setting Dialog Message
        alertDialog.setMessage("Want More Info on this Book?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                Toast.makeText(ReturnBookActivity.this, "This Book Is Already Issued To " + issuedTo, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
*/

    private void btn_book_qr(){
        Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        Log.d("camera","capture activity");
        startActivityForResult(intent, BOOK_RC_QRCODE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int bookDbNameLength;
        if (requestCode == BOOK_RC_QRCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d("Barcode", "Barcode read: " + barcode.displayValue);
                    bookDbName = barcode.displayValue;
                    bookDbNameLength = bookDbName.length();
                    String tmp[] = bookDbName.split(":");
                    bookName = bookDbName.substring(0, bookDbNameLength-tmp[tmp.length-1].length()-1);
                    bookUid = tmp[tmp.length-1];
                    edt_book_name.setText(bookName);
                    edt_book_uid.setText(bookUid);
                    Log.d("Barcode", bookName + " id : "+bookUid);
                } else {
                    Log.d("Barcode", "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("Barcode","Error : "+CommonStatusCodes.getStatusCodeString(resultCode));
            }
        } else if(requestCode == USER_RC_QRCODE_CAPTURE){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d("Barcode", "Barcode read: " + barcode.displayValue);
                    userId = barcode.displayValue;
                    edt_user_id.setText(userId);
                    Log.d("Barcode", "UserId" + userId);
                } else {
                    Log.d("Barcode", "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("Barcode","Error : "+CommonStatusCodes.getStatusCodeString(resultCode));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void btn_user_qr(){
        Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        Log.d("camera","capture activity");
        startActivityForResult(intent, USER_RC_QRCODE_CAPTURE);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_return){
            if(isEmpty()) {
                bookUid = edt_book_uid.getText().toString().trim();
                book_id = bookUid.substring(0, 3);
                bookDbName = edt_book_name.getText().toString().trim() + book_id;
                userId = edt_user_id.getText().toString().trim();
                btn_return();
            }

        }
        if(view == btn_book_qr){
            btn_book_qr();
        }
        if(view == btn_user_qr){
            btn_user_qr();
        }
    }

    private boolean isEmpty(){
        if(TextUtils.isEmpty(edt_book_name.getText().toString())){
            edt_book_name.setError("Enter Book Name");
            return false;
        }
        if(TextUtils.isEmpty(edt_book_uid.getText().toString())){
            edt_book_name.setError("Enter Book UID");
            return false;
        }
        if(TextUtils.isEmpty(edt_user_id.getText().toString())){
            edt_book_name.setError("Enter User ID");
            return false;
        }
        return true;
    }

}