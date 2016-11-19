package lk.ac.mrt.cse.dbs.simpleexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NETHMI-PC on 11/19/2016.
 */
public class DB_Operations extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "MyDatabase.db";
    private static DB_Operations DB_OP = null;
    private static final int DATABASE_VERSION = 1;

    public static final String ACCOUNT_TABLE = "accounts";
    public static final String ACCOUNTS_ACCOUNT_NO = "accountNo";
    public static final String ACCOUNTS_BANK_NAME = "bankName";
    public static final String ACCOUNTS_ACCOUNT_HOLDER = "accountHolderName";
    public static final String ACCOUNTS_BALANCE = "balance";

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String TRANSACTIONS_ID = "transactionID";
    public static final String TRANSACTIONS_DATE = "date";
    public static final String TRANSACTIONS_ACCOUNT_NO = "accountNo";
    public static final String TRANSACTIONS_EXPENSE_TYPE = "expenseType";
    public static final String TRANSACTIONS_AMOUNT = "amount";

    public DB_Operations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DB_Operations getInstance(Context context) {
        if (DB_OP == null)
            DB_OP = new DB_Operations(context);
        return DB_OP;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String account_table = "CREATE TABLE " + ACCOUNT_TABLE + "(" + ACCOUNTS_ACCOUNT_NO + " VARCHAR(20) NOT NULL PRIMARY KEY," + ACCOUNTS_BANK_NAME + " VARCHAR(100) NULL," + ACCOUNTS_ACCOUNT_HOLDER + " VARCHAR(100) NULL," + ACCOUNTS_BALANCE + " DECIMAL(10,2) NULL )";

        String transaction_table = "CREATE TABLE " + TRANSACTION_TABLE + "(" + TRANSACTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + TRANSACTIONS_ACCOUNT_NO + " VARCHAR(20) NOT NULL," + TRANSACTIONS_DATE + " DATE NULL," + TRANSACTIONS_AMOUNT + " DECIMAL(10,2) NULL," + TRANSACTIONS_EXPENSE_TYPE + " VARCHAR(100) NULL, FOREIGN KEY(" + TRANSACTIONS_ACCOUNT_NO + ") REFERENCES " + ACCOUNT_TABLE + "(" + ACCOUNTS_ACCOUNT_NO + "))";

        db.execSQL(account_table);
        db.execSQL(transaction_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE);
        onCreate(db);

    }
}

