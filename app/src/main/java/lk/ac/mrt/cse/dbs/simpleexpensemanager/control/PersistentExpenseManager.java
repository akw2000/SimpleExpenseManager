package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{
    private final Context context;
    public PersistentExpenseManager(Context context) {
        this.context = context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        // create persistent AccountDAO and TransactionDAO set the accounts DAO
        PersistentAccountDAO persistentAccountDAO = new PersistentAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);
        PersistentTransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);
    }
}
