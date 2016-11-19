package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DB_Operations;

/**
 * Created by NETHMI-PC on 11/19/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO {

    private Context context;


    public PersistentTransactionDAO(Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        //Get the connection
        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getWritableDatabase();

        //Save transaction details to the transaction_log table
        ContentValues values = new ContentValues();
        values.put(dbops.TRANSACTIONS_ACCOUNT_NO, accountNo);
        values.put(dbops.TRANSACTIONS_DATE, convertDateToString(date));
        values.put(dbops.TRANSACTIONS_AMOUNT, amount);
        values.put(dbops.TRANSACTIONS_EXPENSE_TYPE, expenseType.toString());

        db.insert(dbops.TRANSACTION_TABLE, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        DB_Operations dbops = DB_Operations.getInstance(context);
        SQLiteDatabase db = dbops.getWritableDatabase();

        //Get details of all the transactions
        String query = "SELECT " + dbops.TRANSACTIONS_ACCOUNT_NO + ", " + dbops.TRANSACTIONS_DATE + ", " + dbops.TRANSACTIONS_EXPENSE_TYPE + ", " + dbops.TRANSACTIONS_AMOUNT + " FROM " + dbops.TRANSACTION_TABLE + " ORDER BY " + dbops.TRANSACTIONS_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> logs = new ArrayList<>();

        //Add transaction details to a list
        while (cursor.moveToNext()) {
            try {
                ExpenseType expenseType = null;
                String dateS = cursor.getString(cursor.getColumnIndex(dbops.TRANSACTIONS_DATE));
                Date date = convertStringToDate(dateS);

                if (cursor.getString(cursor.getColumnIndex(dbops.TRANSACTIONS_EXPENSE_TYPE)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                } else {
                    expenseType = ExpenseType.EXPENSE;
                }

                Transaction new_trans = new Transaction(date, cursor.getString(cursor.getColumnIndex(dbops.TRANSACTIONS_ACCOUNT_NO)), expenseType, cursor.getDouble(cursor.getColumnIndex(dbops.TRANSACTIONS_AMOUNT)));

                //Add transaction to logs list
                logs.add(new_trans);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //If limit is zero return all the transaction logs
        if (limit == 0) {
            return logs;
        } else {
            //If specified limit is less than the size of the logs, return all the logs
            if (logs.size() <= limit) {
                return logs;
            //If specified limit is greater than the size of the logs, return only the first 'limit' logs
            } else {
                return logs.subList(0, limit); //logs are in the descending order
            }
        }
    }


    //Method to convert a date object to a string
    private static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;

    }

    //Method to convert a string to a date object
    private static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date Date_date = dateFormat.parse(date);
        return Date_date;
    }
}
