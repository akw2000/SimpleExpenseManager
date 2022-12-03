package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.provider.BaseColumns;

public final class DBFields implements BaseColumns {
    // make constructor private
    private DBFields(){}
    // Inner class that defines the table contents
    // Account table
    public static class AccountTable implements BaseColumns {
        public static final String TABLE_ACCOUNT = "account";
        public static final String COLUMN_ACCOUNT_NO = "accountNo";
        public static final String COLUMN_BANK_NAME = "bankName";
        public static final String COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
        public static final String COLUMN_BALANCE = "balance";
    }
    //Transaction table
    public static class TransactionTable implements BaseColumns {

        public static final String TABLE_TRANSACTION = "transactionLog";
        public static final String COLUMN_TRANSACTION_ID = "transactionId";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_EXPENSE_TYPE = "expenseType";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_ACCOUNT_NO = "accountNo";

    }
}
