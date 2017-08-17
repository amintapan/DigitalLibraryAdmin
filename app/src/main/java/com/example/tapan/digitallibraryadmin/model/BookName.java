package com.example.tapan.digitallibraryadmin.model;

/**
 * Created by Tapan on 3/4/2017.
 */

public class BookName {

    int available_books;
    int issues;
    String bookId;
    String bookName;
    String librarianName;
    String librarianId;

    public static String getAbc() {
        return abc;
    }

    public static void setAbc(String abc) {
        BookName.abc = abc;
    }

    static String abc ;

    public static String getIssuedBy() {
        return issuedBy;
    }

    public static void setIssuedBy(String issuedBy) {
        BookName.issuedBy = issuedBy;
    }

    static String issuedBy;

    public String getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(String issuedOn) {
        this.issuedOn = issuedOn;
    }

    String issuedOn;

    Boolean validBook=true;

    public String getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(String librarianId) {
        this.librarianId = librarianId;
    }



    public Boolean getValidBook() {
        return validBook;
    }

    public void setValidBook(Boolean validBook) {
        this.validBook = validBook;
    }



    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId(){
        return bookId;
    }
    public void setBookId(String bookId){
        this.bookId = bookId;
    }

    public int getAvailableBooks() {
        return available_books;
    }
    public void setQuantity(int available_books) {
        this.available_books = available_books;
    }

    public int getIssues(){
        return issues;
    }
    public void setIssues(int issues){
        this.issues = issues;
    }

}
