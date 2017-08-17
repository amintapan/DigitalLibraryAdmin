package com.example.tapan.digitallibraryadmin.model;

/**
 * Created by Tapan on 5/13/2017.
 */

public class Search {

    public static String getBookName() {
        return bookName;
    }

    public static void setBookName(String bookName) {
        Search.bookName = bookName;
    }

    public static String bookName;

    public static String getBookDbName() {
        return bookDbName;
    }

    public static void setBookDbName(String bookDbName) {
        Search.bookDbName = bookDbName;
    }

    public static String bookDbName;

    public static String getEnrollNumber() {
        return enrollNumber;
    }

    public static void setEnrollNumber(String enrollNumber) {
        Search.enrollNumber = enrollNumber;
    }

    public static String enrollNumber;
}
