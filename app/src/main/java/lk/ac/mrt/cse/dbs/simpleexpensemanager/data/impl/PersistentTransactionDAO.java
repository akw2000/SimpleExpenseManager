package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBFields.TransactionTable;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DBUtil dbUtil;

    public PersistentTransactionDAO(Context context) {
        this.dbUtil = DBUtil.getInstance(context);
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbUtil.getWritableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                DBFields.AccountTable.TABLE_ACCOUNT + " WHERE " +
                DBFields.AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if(cursor.moveToFirst()) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            // format date
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
            values.put(TransactionTable.COLUMN_DATE, formattedDate);
            values.put(TransactionTable.COLUMN_EXPENSE_TYPE, expenseType.toString());
            values.put(TransactionTable.COLUMN_AMOUNT, amount);
            values.put(TransactionTable.COLUMN_ACCOUNT_NO, accountNo);

            // Insert the new row
            db.insert(TransactionTable.TABLE_TRANSACTION, null, values);
        }
        cursor.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_TRANSACTIONS_INFO = "SELECT  * FROM " +
                TransactionTable.TABLE_TRANSACTION ;
        Cursor cursor = db.rawQuery(SQL_SELECT_TRANSACTIONS_INFO, null);
        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE));
            Date formattedDate = null;
            try {
                formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_EXPENSE_TYPE));
            ExpenseType expenseTypeObj;
            if (expenseType.equals(ExpenseType.EXPENSE.toString())) {
                expenseTypeObj = ExpenseType.EXPENSE;
            } else {
                expenseTypeObj = ExpenseType.INCOME;
            }
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT));

            transactionList.add(new Transaction(formattedDate, accountNo, expenseTypeObj, amount));
        }
        cursor.close();
        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> paginatedTransactionList = new ArrayList<>();
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_TRANSACTIONS_INFO = "SELECT  * FROM " +
                TransactionTable.TABLE_TRANSACTION + " ORDER BY " +
                TransactionTable.COLUMN_TRANSACTION_ID + " DESC LIMIT ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_TRANSACTIONS_INFO, new String[]{String.valueOf(limit)});
        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE));
            Date formattedDate = null;
            try {
                formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_EXPENSE_TYPE));
            ExpenseType expenseTypeObj;
            if (expenseType.equals(ExpenseType.EXPENSE.toString())) {
                expenseTypeObj = ExpenseType.EXPENSE;
            } else {
                expenseTypeObj = ExpenseType.INCOME;
            }
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT));

            paginatedTransactionList.add(new Transaction(formattedDate, accountNo, expenseTypeObj, amount));
        }
        cursor.close();
        // reverse order received from query, we want the order based on the time of insertion
        Collections.reverse(paginatedTransactionList);
        return paginatedTransactionList;
    }
}
