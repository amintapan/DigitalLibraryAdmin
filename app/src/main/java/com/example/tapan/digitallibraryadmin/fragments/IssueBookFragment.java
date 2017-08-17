package com.example.tapan.digitallibraryadmin.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

public class IssueBookFragment extends Fragment implements View.OnClickListener{

    private TextInputLayout layout_book_name, layout_user_id;
    private EditText edt_book_name, edt_user_id, edt_book_uid;
    private Button btn_issue, btn_book_qr, btn_user_qr;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookDatabaseReference, userDatabaseReference, libDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    int available_books = 0;
    int issues = 0;
    String bookId = null;
    String bookName = null;
    String userId=null, book_id=null, bookDbName=null, issuedTo=null;
    String bookUid=null, librarianName=null, librarianUid=null, librarianId=null;
    private boolean gotLibData = false, gotBookData = false, gotUserData = false;
    private BookName obj_bookName = new BookName();
    private static final int BOOK_RC_QRCODE_CAPTURE = 9001;
    private static final int USER_RC_QRCODE_CAPTURE = 9002;

    public IssueBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue_book, container, false);

        edt_book_name = (EditText) view.findViewById(R.id.edt_book_name);
        edt_user_id = (EditText) view.findViewById(R.id.edt_user_id);
        edt_book_uid = (EditText) view.findViewById(R.id.edt_book_uid);
        btn_issue = (Button) view.findViewById(R.id.btn_issue);
        btn_user_qr = (Button) view.findViewById(R.id.btn_user_qr);
        btn_book_qr = (Button) view.findViewById(R.id.btn_book_qr);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        librarianUid = firebaseUser.getUid();
        libDatabaseReference = firebaseDatabase.getReference().child("librarianData");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Issuing Book");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
//        bookDatabaseReference = firebaseDatabase.getReference();
//        bookDatabaseReference = firebaseDatabase.getReference().child("bookData").child("byou");
        btn_issue.setOnClickListener(this);
        btn_book_qr.setOnClickListener(this);
        btn_user_qr.setOnClickListener(this);

        return view;
    }

    private void checkAllThreeDone(boolean lib, boolean book, boolean user) {
        if(lib && book && user) {
            Log.d("three flag", "All three are done");
            new IssueBookFragment.IssueAsyncTask().execute();
        }
    }

    class IssueAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*
        *After getting the values of book and librarian, it should go below
        * it will set the values in firebase for book ISSUE
        */
        @Override
        protected Object doInBackground(Object[] params) {

            if(available_books>0) {
                available_books = obj_bookName.getAvailableBooks()-1;
            }
            issues = obj_bookName.getIssues()+1;
            //bookUid = obj_bookName1.getBookId();
            bookName = obj_bookName.getBookName();

            bookDatabaseReference.child("availableBooks").setValue(available_books);
            bookDatabaseReference.child("issues").setValue(issues);
            bookDatabaseReference.child("Time").setValue(ServerValue.TIMESTAMP);
            bookDatabaseReference.child("bookUid").child("issuedBooks").child(bookUid).child("issuedByName").setValue(librarianName);
            //Log.d("libname", librarianName);
            bookDatabaseReference.child("bookUid").child("issuedBooks").child(bookUid).child("issuedTo").setValue(userId);
            bookDatabaseReference.child("bookUid").child("issuedBooks").child(bookUid).child("issuedOn").setValue(ServerValue.TIMESTAMP);
            bookDatabaseReference.child("bookUid").child("issuedBooks").child(bookUid).child("issuedBy").setValue(librarianId);
            //bookDatabaseReference.child("bookUid").child(bookUid).child("returnDate").setValue(ServerValue.TIMESTAMP+"2 weeks");
            bookDatabaseReference.child("bookUid").child("availableBooks").child(bookUid).removeValue();

            userDatabaseReference.child("issuedBooks").child(bookUid).child("bookUid").setValue(bookUid);
            userDatabaseReference.child("issuedBooks").child(bookUid).child("bookName").setValue(bookName);
            //userDatabaseReference.child("issuedBooks").child(bookDbName).child("bookDbName").setValue(bookDbName);
            userDatabaseReference.child("issuedBooks").child(bookUid).child("issuedOn").setValue(ServerValue.TIMESTAMP);
            userDatabaseReference.child("issuedBooks").child(bookUid).child("issuedBy").setValue(librarianName);

            libDatabaseReference.child(librarianId).child("issuedBooks").child(bookUid).child("bookUid").setValue(bookUid);
            libDatabaseReference.child(librarianId).child("issuedBooks").child(bookUid).child("bookName").setValue(bookName);
            //libDatabaseReference.child(librarianId).child("issuedBooks").child(bookDbName).child("bookDbName").setValue(bookDbName);
            libDatabaseReference.child(librarianId).child("issuedBooks").child(bookUid).child("issuedOn").setValue(ServerValue.TIMESTAMP);
            libDatabaseReference.child(librarianId).child("issuedBooks").child(bookUid).child("issuedTo").setValue(userId);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            gotLibData=false;
            gotBookData=false;
            gotUserData=false;
            progressDialog.cancel();
            Toast.makeText(getContext(), "Book Issued", Toast.LENGTH_SHORT).show();
            edt_user_id.setText("");
            edt_book_name.setText("");
            edt_book_uid.setText("");

        }
    }

    private void btn_issue() {

        progressDialog.show();

        Log.d("temp", book_id);
        Log.d("temp", bookDbName);

        bookDatabaseReference = firebaseDatabase.getReference().child("bookData").child(bookDbName);
        userDatabaseReference = firebaseDatabase.getReference().child("userData").child(userId);

        getLibData();
        getBookData();
    }

    /*
    * To get the Librarian data such as name, id, uid
    * from libDatabaseReference it will ref. the librarianData in the database
    * librarian uid is already defined in onCreate frm that
    * it will get libId, from that it will get name*/
    private void getLibData(){
        libDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                librarianId = dataSnapshot.child("uidAndId").child(librarianUid).getValue().toString();
                librarianName = dataSnapshot.child(librarianId).child("Name").getValue().toString();
                Log.d("libID1", librarianId);
                obj_bookName.setLibrarianId(librarianId);
                Log.d("libID11", obj_bookName.getLibrarianId());
                gotLibData = true;
                Log.d("three flag", "lib done");
                checkAllThreeDone(gotLibData, gotBookData, gotUserData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*To get book Data such as availble_book, issues
    * bookDatabaseReference is ref. to bookData in DB
    * these values are used in the post delayed method
    * to set the data in firebase while issuing the book*/
    private void getBookData(){

        bookDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Log.d("getBookData", "Book DbName " + bookDbName + " exists in the database.");
                    available_books = Integer.parseInt(String.valueOf(dataSnapshot.child("bookUid").child("availableBooks").getChildrenCount()));

                    if (dataSnapshot.child("bookUid").child("availableBooks").child(bookUid).exists()) {
                        Log.d("validBook", bookUid);
                        obj_bookName.setValidBook(true);
                        issues = Integer.parseInt(dataSnapshot.child("issues").getValue().toString());
                        bookName = dataSnapshot.child("bookName").getValue().toString();

                        //bookId = dataSnapshot.child("bookId").getValue().toString();

                        obj_bookName.setQuantity(available_books);
                        obj_bookName.setIssues(issues);
                        //obj_bookName1.setBookId(bookId);
                        obj_bookName.setBookName(bookName);
                        Log.d("validBook", "YES");

                        gotBookData = true;
                        Log.d("three flag", "book done");
                        checkAllThreeDone(gotLibData, gotBookData, gotUserData);

                        getUserData();

                        return;
                        //Log.d("available", String.valueOf(bookName.getAvailableBooks()));

                        //databaseReference.child("availableBooks").setValue(quantity);
                    } else if (dataSnapshot.child("bookUid").child("issuedBooks").child(bookUid).exists()) {
                        Log.d("validBook", "issued To some other user");
                        //obj_bookName.setValidBook(false);
                        gotBookData = false;
                        issuedTo = dataSnapshot.child("bookUid").child("issuedBooks").child(bookUid).child("issuedTo").getValue().toString();
                        //Toast.makeText(IssueBookActivity.this, "This Book Is Already Issued To " + issuedTo, Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        showAlert();

                        return;
                    } else {
                        //obj_bookName.setValidBook(false);
                        gotBookData = false;
                        Toast.makeText(getContext(), "Book Not Valid", Toast.LENGTH_SHORT).show();
                        Log.d("validBook", "NO");
                        progressDialog.cancel();
                        return;
                    }
                } else{
                    edt_book_uid.setError("Verify Book ID");
                    edt_book_name.setError("Verify Book Name");
                    progressDialog.cancel();
                    Log.d("getBookData", "Book DbName " +bookDbName+ " does not exist in the database.");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getUserData(){
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data", userId);
                Log.d("data", String.valueOf(dataSnapshot.getChildrenCount()));
                Log.d("data", String.valueOf(dataSnapshot.child("issuedBooks")));
                if(dataSnapshot.exists()){
                    Log.d("getUserData", "User Id " +userId+ " exists in the database.");
                    if(dataSnapshot.child("issuedBooks").getChildrenCount()<=2 &&
                            dataSnapshot.child("issuedBooks").getChildrenCount()>=0){

                        gotUserData = true;
                        Log.d("three flag", "user done");
                        checkAllThreeDone(gotLibData, gotBookData, gotUserData);
                        return;

                    } else{
                        Log.d("data", String.valueOf(dataSnapshot.child(userId).child("issuedBooks").getChildrenCount()));
                        Toast.makeText(getContext(), "Already 2 Books Issued to " + userId, Toast.LENGTH_SHORT).show();
                        Log.d("validBook", "NO");
                        //obj_bookName.setValidBook(false);
                        gotUserData = false;
                        progressDialog.cancel();
                    }
                } else{
                    edt_user_id.setError("Invalid User ID");
                    progressDialog.cancel();
                    Log.d("getUserData", "User Id " +userId+ " does not exist in the database.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
                    Toast.makeText(getContext(), "No Data Captured", Toast.LENGTH_SHORT).show();
                    Log.d("Barcode", "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(getContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "No Data Captured", Toast.LENGTH_SHORT).show();
                    Log.d("Barcode", "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(getContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
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

    private void showAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        // Setting Dialog Title
        alertDialog.setTitle("Already Issued Book");

        // Setting Dialog Message
        alertDialog.setMessage("Want More Info on this Book?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                Toast.makeText(getContext(), "This Book Is Already Issued To " + issuedTo, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {

     /*Get the edittext values
     * name, bookuid and userid
     * bookuid will contain book id+(total number of quantity) --> substring is used for unique code of the book*/
        if(view == btn_issue){
            if(isEmpty()) {
                bookUid = edt_book_uid.getText().toString().trim();
                book_id = bookUid.substring(0, 3);
                bookDbName = edt_book_name.getText().toString().trim() + book_id;
                userId = edt_user_id.getText().toString().trim();
                btn_issue();
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
