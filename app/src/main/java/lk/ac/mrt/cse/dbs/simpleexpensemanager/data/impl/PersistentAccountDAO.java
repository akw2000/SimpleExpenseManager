/**
 * It implements the AccountDAO interface and uses the DBUtil class to access the database
 */
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

/**
 * It returns a list of account numbers from the database
 * 
 * @return A list of account numbers.
 */
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

/**
 * It returns a list of all the accounts in the database
 * 
 * @return A list of Account objects.
 */
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

/**
 * It returns an account object if the account number is valid, otherwise it throws an exception
 * 
 * @param accountNo The account number of the account to be retrieved.
 * @return The account object is being returned.
 */
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

/**
 * It takes an Account object as a parameter, and inserts it into the database
 * 
 * @param account The account object to be inserted into the database.
 */
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

/**
 * It deletes the account from the database if it exists, otherwise it throws an exception
 * 
 * @param accountNo The account number of the account to be removed.
 */
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbUtil.getReadableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT + " WHERE " +
                AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if (cursor.moveToFirst()) {
            // delete the account
            db.delete(AccountTable.TABLE_ACCOUNT, AccountTable.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo} );
        } else {
            // empty query returned
            String msg = "The given account " + accountNo + " is invalid.";
            cursor.close();
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

/**
 * It updates the balance of an account by subtracting the amount if the expense type is EXPENSE and
 * adding the amount if the expense type is INCOME
 * 
 * @param accountNo the account number
 * @param expenseType enum type
 * @param amount the amount of money to be added or subtracted from the account
 */
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbUtil.getWritableDatabase();
        final String SQL_SELECT_ACCOUNT_INFO = "SELECT  * FROM " +
                AccountTable.TABLE_ACCOUNT + " WHERE " +
                AccountTable.COLUMN_ACCOUNT_NO + "= ?";
        Cursor cursor = db.rawQuery(SQL_SELECT_ACCOUNT_INFO, new String[]{accountNo});
        if(cursor.moveToFirst()){
            // first update the balance
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));
            // based on the expense type, add or subtract the amount
            switch (expenseType) {
                case EXPENSE:
                    balance -=amount;
                    break;
                case INCOME:
                    balance +=amount;
                    break;
            }
            ContentValues values = new ContentValues();
            values.put(AccountTable.COLUMN_BALANCE, balance);
            // update the balance
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