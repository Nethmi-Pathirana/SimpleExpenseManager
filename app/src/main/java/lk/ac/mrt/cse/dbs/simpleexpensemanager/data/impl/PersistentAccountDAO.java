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
import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DB_Operations;

/**
 * Created by NETHMI-PC on 11/19/2016.
 */
public class PersistentAccountDAO implements AccountDAO {

    private Context context;

    public PersistentAccountDAO(Context context) {
        this.context = context;
    }
    @Override
    public List<String> getAccountNumbersList() {

        //Get the database connection
        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getReadableDatabase();

        //Select all account numbers from the accounts table
        String query = "SELECT "+ dbops.ACCOUNTS_ACCOUNT_NO+" FROM " + dbops.ACCOUNT_TABLE+" ORDER BY " + dbops.ACCOUNTS_ACCOUNT_NO ;

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<>();

        //Add account numbers to a list
        while (cursor.moveToNext())
        {
            result.add(cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_NO)));
        }

        cursor.close();

        //Return the list of account numbers
        return result;
    }

    @Override
    public List<Account> getAccountsList() {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getReadableDatabase();

        //Select all the details of the accounts in the account table
        String query = "SELECT * FROM " + dbops.ACCOUNT_TABLE+" ORDER BY "+dbops.ACCOUNTS_ACCOUNT_NO ;

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Account> result = new ArrayList<>();

        //Add account details to a list
        while (cursor.moveToNext())
        {
            Account account = new Account(
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_HOLDER)),
                    cursor.getDouble(cursor.getColumnIndex(dbops.ACCOUNTS_BALANCE)));

            result.add(account);
        }

        cursor.close();

        //Return list of account objects
        return result;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getReadableDatabase();

        //Details of the account specified by the accountNo
        String query = "SELECT * FROM " + dbops.ACCOUNT_TABLE + " WHERE " + dbops.ACCOUNTS_ACCOUNT_NO + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //Add details to the account object
        if (cursor.moveToFirst()) {
            account = new Account(
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_HOLDER)),
                    cursor.getDouble(cursor.getColumnIndex(dbops.ACCOUNTS_BALANCE)));
        }
        else {
            throw new InvalidAccountException("Invalid account number!");
        }

        cursor.close();

        //Return the account object
        return account;
    }

    @Override
    public void addAccount(Account account) {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getWritableDatabase();

        //Save details to the account table
        ContentValues values = new ContentValues();
        values.put(dbops.ACCOUNTS_ACCOUNT_NO, account.getAccountNo());
        values.put(dbops.ACCOUNTS_BANK_NAME, account.getBankName());
        values.put(dbops.ACCOUNTS_ACCOUNT_HOLDER, account.getAccountHolderName());
        values.put(dbops.ACCOUNTS_BALANCE, account.getBalance());

        db.insert(dbops.ACCOUNT_TABLE, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getWritableDatabase();

        //Delete the specified account from the accounts table
        String query = "SELECT * FROM " + dbops.ACCOUNT_TABLE + " WHERE " + dbops.ACCOUNTS_ACCOUNT_NO + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //Delete the account if found in the table
        if (cursor.moveToFirst()) {
            account = new Account(
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(dbops.ACCOUNTS_ACCOUNT_HOLDER)),
                    cursor.getFloat(cursor.getColumnIndex(dbops.ACCOUNTS_BALANCE)));

            db.delete(dbops.ACCOUNT_TABLE, dbops.ACCOUNTS_ACCOUNT_NO + " = ", new String[] { accountNo });
            cursor.close();

        }
        //If account is not found
        else {
            throw new InvalidAccountException("Account not found!");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getWritableDatabase();

        ContentValues values = new ContentValues();

        //Get the account details of the specified account
        Account account = getAccount(accountNo);

        //Update the balance if the account is found in the table
        if (account!=null) {

            double new_balance=0;

            //If it is an expense, deduct the amount from balance
            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_balance = account.getBalance() - amount;
            }
            //If it is an income, add the amount to balance
            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_balance = account.getBalance() + amount;
            }

            //Update the balance in the account table
            String query = "UPDATE "+dbops.ACCOUNT_TABLE+" SET "+dbops.ACCOUNTS_BALANCE+" = "+new_balance+" WHERE "+dbops.ACCOUNTS_ACCOUNT_NO+" = '"+ accountNo+"'";

            db.execSQL(query);
        }
        //If account is not found
        else {
            throw new InvalidAccountException("Account not found!");
        }
    }
}
