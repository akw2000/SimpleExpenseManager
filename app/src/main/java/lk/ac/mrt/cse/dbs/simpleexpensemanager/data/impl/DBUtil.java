package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBFields.AccountTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBFields.TransactionTable;

public class DBUtil extends SQLiteOpenHelper {
    // database version to track changes to schema
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "200694G.db";
    // make it singleton
    private static DBUtil dbUtil = null;

    private DBUtil(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static DBUtil getInstance(@Nullable Context context){
        if(dbUtil == null){
            dbUtil = new DBUtil(context);
        }
        return dbUtil;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ENTRIES_ACCOUNT_TABLE =
                "CREATE TABLE " + AccountTable.TABLE_ACCOUNT
                        + "(" +
                        AccountTable.COLUMN_ACCOUNT_NO + " VARCHAR(50) PRIMARY KEY NOT NULL," +
                        AccountTable.COLUMN_BANK_NAME + " VARCHAR(255)," +
                        AccountTable.COLUMN_ACCOUNT_HOLDER_NAME + " VARCHAR(255)," +
                        AccountTable.COLUMN_BALANCE + " NUMERIC(10,2)" +
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_ACCOUNT_TABLE);
        final String SQL_CREATE_ENTRIES_TRANSACTION_TABLE =
                "CREATE TABLE " + TransactionTable.TABLE_TRANSACTION
                        + "(" +
                        TransactionTable.COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        TransactionTable.COLUMN_DATE + " DATE," +
                        TransactionTable.COLUMN_EXPENSE_TYPE + " VARCHAR(255)," +
                        TransactionTable.COLUMN_AMOUNT + " NUMERIC(10,2)," +
                        TransactionTable.COLUMN_ACCOUNT_NO + " VARCHAR(50)," +
                        "FOREIGN KEY(" + TransactionTable.COLUMN_ACCOUNT_NO + ") REFERENCES " +
                        AccountTable.TABLE_ACCOUNT + "(" + AccountTable.COLUMN_ACCOUNT_NO + ")" +
                        " ON DELETE CASCADE )";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_TRANSACTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        final String SQL_DELETE_ENTRIES_ACCOUNT_TABLE =
                "DROP TABLE IF EXISTS " + AccountTable.TABLE_ACCOUNT;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_ACCOUNT_TABLE);
        final String SQL_DELETE_ENTRIES_TRANSACTION_TABLE =
                "DROP TABLE IF EXISTS " + TransactionTable.TABLE_TRANSACTION;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_TRANSACTION_TABLE);

        onCreate(sqLiteDatabase);
    }
    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}
