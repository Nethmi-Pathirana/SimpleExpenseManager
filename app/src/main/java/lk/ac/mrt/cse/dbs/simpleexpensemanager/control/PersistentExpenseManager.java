package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

/**
 * Created by NETHMI-PC on 11/19/2016.
 */
public class PersistentExpenseManager extends ExpenseManager {
    private Context context = null;

    public PersistentExpenseManager( Context context) throws ExpenseManagerException {
        this.context = context;
        setup();
    }

    @Override
    public void setup() throws ExpenseManagerException {
        //Setup AccountDAO
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);

        //Setup TransactionDAO
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);
    }
}
