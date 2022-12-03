package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBFields.AccountTable;

public  class PersistentAccountDAO implements AccountDAO {
    private final DBUtil dbUtil;

    public PersistentAccountDAO(Context context) {
        this.dbUtil = DBUtil.getInstance(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumberList = new ArrayList<>();
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNT_NUMBERS = "SELECT " +
                AccountTable.COLUMN_ACCOUNT_NO + " FROM " +
                AccountTable.TABLE_ACCOUNT ;
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_NUMBERS, null);
        while(cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_NO));
            accountNumberList.add(accountNo);
        }
        cursor.close();
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountInfoList = new ArrayList<>();
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNTS_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT ;
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNTS_INFO, null);
        while(cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));

            accountInfoList.add(new Account(accountNo, bankName, accountHolderName, balance));
        }
        cursor.close();
        return accountInfoList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account;
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT + " WHERE " +
                AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if(cursor.moveToFirst()) {
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));

            account = new  Account(accountNo, bankName, accountHolderName, balance);
        } else {
            // empty query returned
            String msg = "The given account " + accountNo + " is invalid.";
            cursor.close();
            throw new InvalidAccountException(msg);
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbUtil.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(AccountTable.COLUMN_ACCOUNT_NO, account.getAccountNo());
        values.put(AccountTable.COLUMN_BANK_NAME, account.getBankName());
        values.put(AccountTable.COLUMN_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(AccountTable.COLUMN_BALANCE, account.getBalance());

        // Insert the new row
        db.insert(AccountTable.TABLE_ACCOUNT, null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT + " WHERE " +
                AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if(cursor.moveToFirst()) {
            db.delete(AccountTable.TABLE_ACCOUNT, AccountTable.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo} );
        } else {
            // empty query returned
            String msg = "The given account " + accountNo + " is invalid.";
            cursor.close();
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT + " WHERE " +
                AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if(cursor.moveToFirst()) {
            // first update the balance
            double newBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));
            switch (expenseType){
                case EXPENSE:
                    newBalance -= amount;
                case INCOME:
                    newBalance += amount;
            }
            ContentValues values = new ContentValues();
            values.put(AccountTable.COLUMN_BALANCE, newBalance);
            db.update(AccountTable.TABLE_ACCOUNT, values, AccountTable.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});
        } else {
            // empty query returned
            String msg = "The given account " + accountNo + " is invalid.";
            cursor.close();
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }
}