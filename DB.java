package ru.seminma.rfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {
    private static final String LOG_TAG = "LOG SQLite";

    private static final String DB_NAME = "rFoodDB";
    private static final int DB_VERSION = 16;
    private static final String DB_TABLE = "mytab";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMG = "img";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TXT = "txt";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IMG + " integer, " +
                    COLUMN_TIME + " text, " +
                    COLUMN_TXT + " text" +
                    ");";

    private static final String DB_DELETE = "DROP TABLE IF EXISTS " + DB_TABLE + ";";

    // имя таблицы категории еды, поля и запрос создания
    public static final String FOOD_TYPE_TABLE = "foodType";
    public static final String FOOD_TYPE_COLUMN_ID = "_id";
    public static final String FOOD_TYPE_COLUMN_NAME = "name";
    private static final String FOOD_TYPE_TABLE_CREATE = "create table " + FOOD_TYPE_TABLE
            + "(" + FOOD_TYPE_COLUMN_ID + " integer primary key, "
            + FOOD_TYPE_COLUMN_NAME + " text" + ");";

    // запрос удаления таблицы еды
    private static final String FOOD_TYPE_TABLE_DELETE = "DROP TABLE IF EXISTS " + FOOD_TYPE_TABLE + ";";

    // имя таблицы еды, поля и запрос создания
    public static final String FOOD_TABLE = "food";
    public static final String FOOD_COLUMN_ID = "_id";
    public static final String FOOD_COLUMN_NAME = "name";
    public static final String FOOD_COLUMN_TYPE = "type";
    private static final String FOOD_TABLE_CREATE = "create table " + FOOD_TABLE
            + "(" + FOOD_COLUMN_ID + " integer primary key autoincrement, "
            + FOOD_COLUMN_NAME + " text, "
            + FOOD_COLUMN_TYPE + " integer" + ");";

    // запрос удаления таблицы типа еды
    private static final String FOOD_TABLE_DELETE = "DROP TABLE IF EXISTS " + FOOD_TABLE + ";";


    // таблица приёма пищи
    public static final String MEALTIME_TABLE = "mealtime";
    public static final String MEALTIME_COLUMN_ID = "_id";
    public static final String MEALTIME_COLUMN_NAME = "name";
    public static final String MEALTIME_COLUMN_DATE_TIME = "dateTime";

    private static final String MEALTIME_TABLE_CREATE = "create table " + MEALTIME_TABLE + "("
            + MEALTIME_COLUMN_ID + " integer primary key autoincrement, "
            + MEALTIME_COLUMN_NAME + " text, "
            + MEALTIME_COLUMN_DATE_TIME + " integer " + ");" ;

    // запрос удаления таблицы приёма пищи
    private static final String MEALTIME_TABLE_DELETE = "DROP TABLE IF EXISTS " + MEALTIME_TABLE + ";";


    // Продукты съеденные во время приёма пищи
    public static final String MEALTIME_FOOD_TABLE = "mealtime_food";
    public static final String MEALTIME_FOOD_COLUMN_ID = "_id";
    public static final String MEALTIME_FOOD_COLUMN_MEALTIME_ID = "mealtime_id";
    public static final String MEALTIME_FOOD_COLUMN_FOOD_ID = "food_id";

    private static final String MEALTIME_FOOD_TABLE_CREATE = "create table " + MEALTIME_FOOD_TABLE + "("
            + MEALTIME_FOOD_COLUMN_ID + " integer primary key autoincrement, "
            + MEALTIME_FOOD_COLUMN_MEALTIME_ID + " integer, "
            + MEALTIME_FOOD_COLUMN_FOOD_ID + " integer " + ");" ;

    // запрос удаления таблицы приёма пищи
    private static final String MEALTIME_FOOD_TABLE_DELETE = "DROP TABLE IF EXISTS " + MEALTIME_FOOD_TABLE + ";";


    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        Log.d(LOG_TAG,"mCtx = " + mCtx);
        if (mCtx == null) {
            Log.d(LOG_TAG,"mCtx is NULL !!! ");
        }
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // получить все данные из таблицы MEALTIME_TABLE
    public Cursor getAllMealtimeData(long startDate, long stopDate) {
        String where = MEALTIME_COLUMN_DATE_TIME + " >= " + startDate + " and " + MEALTIME_COLUMN_DATE_TIME + " <= " + stopDate;
        return mDB.query(MEALTIME_TABLE, null, where, null, null, null, null);
    }

    // получить все данные из таблицы MEALTIME_TABLE с коментариями
    public Cursor getAllMealtimeDataComments(long startDate, long stopDate) {

        String table = MEALTIME_TABLE + " inner join " + MEALTIME_FOOD_TABLE +
                " on " + MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID +
                " inner join " + FOOD_TABLE + " on " + FOOD_TABLE + "." + FOOD_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID;

        String columns[] = { MEALTIME_TABLE + "." + MEALTIME_COLUMN_NAME + " as name",
                            MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID  + " as _id" ,
                            "group_concat(" + FOOD_TABLE + "." + FOOD_COLUMN_NAME  + ") as food "  };

        String where = MEALTIME_COLUMN_DATE_TIME + " >= " + startDate + " and " + MEALTIME_COLUMN_DATE_TIME + " <= " + stopDate;
        String groupBy = MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID;
        Cursor cursor = mDB.query(table, columns, where, null, groupBy, null, null);

        debugCursor(cursor);

        return  cursor;
    }


    public String getMealtime (long id) {
        String columns[] = { MEALTIME_TABLE + "." + MEALTIME_COLUMN_NAME + " as name", MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID  + " as id" };
       // String args[] = { id + " "};
        Cursor cursor = mDB.query(MEALTIME_TABLE, columns,  MEALTIME_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Log.d(LOG_TAG," getMealtime = " + cursor.getString(0));
        return cursor.getString(0);
    }

    public String getMealtimeDay (long id) {
        String columns[] = { MEALTIME_TABLE + "." + MEALTIME_COLUMN_DATE_TIME + " as name", MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID  + " as id" };
        // String args[] = { id + " "};
        Cursor cursor = mDB.query(MEALTIME_TABLE, columns,  MEALTIME_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Log.d(LOG_TAG," getMealtimeDay = " + cursor.getString(0));
        return cursor.getString(0);
    }

    // получить все данные из таблицы MEALTIME_FOOD_TABLE
    public Cursor getAllMealtimeFoodData() {
        //return mDB.query(MEALTIME_FOOD_TABLE, null, null, null, null, null, null);
        //select Mealtime.name, Food.name from Mealtime, Food, MealtimeFood where Mealtime._id = MealtimeFood.Mealtime_id and Food._id = MealtimeFood.Food_id;
        String table = MEALTIME_TABLE + " inner join " + MEALTIME_FOOD_TABLE + " on " + MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID;
        table += " inner join " + FOOD_TABLE + " on " + FOOD_TABLE + "." + FOOD_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID;

        String columns[] = { MEALTIME_TABLE + "." + MEALTIME_COLUMN_NAME + " as name", MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID  + " as _id" , FOOD_TABLE + "." + FOOD_COLUMN_NAME  + " as food" };
        Log.d(LOG_TAG," get ALL MealTime Data TABLE = " + table);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 1 = " + columns[0]);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 2 = " + columns[1]);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 3 = " + columns[2]);
        return mDB.query(table, columns, null, null, null, null, null);
    }

    // получить все данные из таблицы MEALTIME_FOOD_TABLE
    public Cursor getMealtimeFoodData(long id) {
        //return mDB.query(MEALTIME_FOOD_TABLE, null, null, null, null, null, null);
        //select Mealtime.name, Food.name from Mealtime, Food, MealtimeFood where Mealtime._id = MealtimeFood.Mealtime_id and Food._id = MealtimeFood.Food_id;
        String table = MEALTIME_TABLE + " inner join " + MEALTIME_FOOD_TABLE + " on " + MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID;
        table += " inner join " + FOOD_TABLE + " on " + FOOD_TABLE + "." + FOOD_COLUMN_ID+ " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID;

        String columns[] = { MEALTIME_TABLE + "." + MEALTIME_COLUMN_NAME + " as name", MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID  + " as _id" , FOOD_TABLE + "." + FOOD_COLUMN_NAME  + " as food" };
        Log.d(LOG_TAG," get ALL MealTime Data TABLE = " + table);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 1 = " + columns[0]);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 2 = " + columns[1]);
        Log.d(LOG_TAG," get ALL MealTime Data COLLUMN 3 = " + columns[2]);
        return mDB.query(table, columns, MEALTIME_TABLE + "." + MEALTIME_COLUMN_ID+ " = " + id, null, null, null, null);
    }

    public long newMealtime (long timeStamp){
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(MEALTIME_COLUMN_DATE_TIME,timeStamp);
        cv.put(MEALTIME_COLUMN_NAME, DateTimeConverter.UTC2String(timeStamp));
        return mDB.insert(MEALTIME_TABLE, null, cv);
    }

    public int deleteMealtime (long id){
        int count = mDB.delete(MEALTIME_TABLE, MEALTIME_COLUMN_ID + " = "+ id, null);
        Log.d(LOG_TAG," delete from " + MEALTIME_TABLE + " deleted = "+ count);
        deleteMealtimeAllFood(id);
        return count;
    }

    public int deleteMealtimeAllFood (long id){
        int count = mDB.delete(MEALTIME_FOOD_TABLE, MEALTIME_FOOD_COLUMN_MEALTIME_ID + " = "+ id, null);
        Log.d(LOG_TAG," delete from " + MEALTIME_FOOD_TABLE + " deleted = "+ count);
        return count;
    }

    public int deleteMealtimeFood (long mealtimeID, long foodID){
        String where = MEALTIME_FOOD_COLUMN_MEALTIME_ID + " = "+ mealtimeID + " and " + MEALTIME_FOOD_COLUMN_FOOD_ID + " = "+ foodID;
        int count = mDB.delete(MEALTIME_FOOD_TABLE, where, null);
        Log.d(LOG_TAG," delete from " + MEALTIME_FOOD_TABLE +" "+ where+ " deleted = "+ count);
        return count;
    }

    public void addMealtimeFood (String mealtimeID, long food) {
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, mealtimeID);
        cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID,food);
        mDB.insert(MEALTIME_FOOD_TABLE, null, cv);
        Log.d(LOG_TAG," insert into " + MEALTIME_FOOD_TABLE + " food id = "+food);
    }



    // данные по типам еды
    public Cursor getFoodTypeData() {
        Cursor cursor = mDB.query(FOOD_TYPE_TABLE, null, null, null, null, null, null);
        return cursor;
    }

    // данные по еде конкретного типа
    public Cursor getFoodData(long mealtimeID, long foodTypeID) {
        String table = FOOD_TABLE ;
        table += " left join " + MEALTIME_FOOD_TABLE ;
        table += " on " + FOOD_TABLE + "." + FOOD_COLUMN_ID + " = " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID;
        table += " and " + MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_MEALTIME_ID  + " = " +  mealtimeID;

        String where = FOOD_COLUMN_TYPE + " = " + foodTypeID;


        String group = FOOD_TABLE + "." + FOOD_COLUMN_ID;

        String columns[] = { FOOD_TABLE + "." + FOOD_COLUMN_NAME + " as name",
                FOOD_TABLE + "." + FOOD_COLUMN_ID   + " as _id" ,
                MEALTIME_FOOD_TABLE + "." + MEALTIME_FOOD_COLUMN_FOOD_ID  + " as mealtime "  };

        Log.d(LOG_TAG," select " + table);
        Cursor cursor = mDB.query(table, columns, where, null, group, null, null);

        debugCursor(cursor);

        return cursor;
    }

    // добавить запись в DB_TABLE
    public void addRec(String txt, String time, int img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_IMG, img);
        cv.put(COLUMN_TIME, time);
        mDB.insert(DB_TABLE, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // вывод для Debug
    public int debugCursor (Cursor cursor){
        String str="";
        for (int i=0; i< cursor.getColumnCount(); i++) {
            str += cursor.getColumnName(i) + " | ";
        }


        if (cursor.getCount() == 0) {
            Log.d("LOG SQL", "cursor.getCount() = " + cursor.getCount());
            return 1;
        }

        Log.d("LOG SQL", str);
        cursor.moveToFirst();



        do {
            str="";
            for (int i=0; i< cursor.getColumnCount(); i++) {
                str += cursor.getString(i) + " | ";
            }

            Log.d("LOG SQL", str);
        }
        while (cursor.moveToNext());

        return 0;
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d (LOG_TAG, "onCreate");
            db.execSQL(DB_CREATE);
            ContentValues cv = new ContentValues();
            for (int i = 1; i < 5; i++) {
                cv.put(COLUMN_TXT, "sometext " + i);
                cv.put(COLUMN_IMG, R.drawable.ic_menu_camera);
                cv.put(COLUMN_TIME, "12-00");
                db.insert(DB_TABLE, null, cv);
            }

            cv.clear();

            // названия категорий еды
            String[] companies = mCtx.getResources().getStringArray(R.array.FoodType);
            // создаем и заполняем таблицу компаний
            db.execSQL(FOOD_TYPE_TABLE_CREATE);
            for (int i = 0; i < companies.length; i++) {
                cv.put(FOOD_TYPE_COLUMN_ID, i + 1);
                cv.put(FOOD_TYPE_COLUMN_NAME, companies[i]);
                db.insert(FOOD_TYPE_TABLE, null, cv);
            }



            // названия элементов
            String[] foodTypeMeats = mCtx.getResources().getStringArray(R.array.Meats);
            String[] foodTypeChees = mCtx.getResources().getStringArray(R.array.Chees);
            String[] foodTypeMilk = mCtx.getResources().getStringArray(R.array.Milk);
            String[] foodTypeBeans = mCtx.getResources().getStringArray(R.array.Beans);
            String[] foodTypeFish = mCtx.getResources().getStringArray(R.array.Fish);
            String[] foodTypeDriedFruits = mCtx.getResources().getStringArray(R.array.DriedFruits);

            // создаем и заполняем таблицу еды
            db.execSQL(FOOD_TABLE_CREATE);
            cv.clear();
            for (int i = 0; i < foodTypeMeats.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 1);
                cv.put(FOOD_COLUMN_NAME, foodTypeMeats[i]);
                db.insert(FOOD_TABLE, null, cv);
            }
            for (int i = 0; i < foodTypeChees.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 2);
                cv.put(FOOD_COLUMN_NAME, foodTypeChees[i]);
                db.insert(FOOD_TABLE, null, cv);
            }
            for (int i = 0; i < foodTypeMilk.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 3);
                cv.put(FOOD_COLUMN_NAME, foodTypeMilk[i]);
                db.insert(FOOD_TABLE, null, cv);
            }
            for (int i = 0; i < foodTypeBeans.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 4);
                cv.put(FOOD_COLUMN_NAME, foodTypeBeans[i]);
                db.insert(FOOD_TABLE, null, cv);
            }
            for (int i = 0; i < foodTypeFish.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 5);
                cv.put(FOOD_COLUMN_NAME, foodTypeFish[i]);
                db.insert(FOOD_TABLE, null, cv);
            }
            for (int i = 0; i < foodTypeDriedFruits.length; i++) {
                cv.put(FOOD_COLUMN_TYPE, 6);
                cv.put(FOOD_COLUMN_NAME, foodTypeDriedFruits[i]);
                db.insert(FOOD_TABLE, null, cv);
            }

            // создаем таблицу приёма пищи
            db.execSQL(MEALTIME_TABLE_CREATE);
            // заполняем таблицу приёма пищи для теста
            cv.clear();
            cv.put(MEALTIME_COLUMN_DATE_TIME, System.currentTimeMillis());
            cv.put(MEALTIME_COLUMN_NAME, "Завтрак");
            db.insert(MEALTIME_TABLE, null, cv);

            cv.clear();
            cv.put(MEALTIME_COLUMN_DATE_TIME, System.currentTimeMillis());
            cv.put(MEALTIME_COLUMN_NAME, "Обед");
            db.insert(MEALTIME_TABLE, null, cv);

            // создаем таблицу приёма пищи
            db.execSQL(MEALTIME_FOOD_TABLE_CREATE);
            // заполняем таблицу приёма пищи для теста
            cv.clear();
            cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, 1);
            cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID, 10);
            db.insert(MEALTIME_FOOD_TABLE, null, cv);

            cv.clear();
            cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, 1);
            cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID, 5);
            db.insert(MEALTIME_FOOD_TABLE, null, cv);

            cv.clear();
            cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, 1);
            cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID, 25);
            db.insert(MEALTIME_FOOD_TABLE, null, cv);

            cv.clear();
            cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, 2);
            cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID, 4);
            db.insert(MEALTIME_FOOD_TABLE, null, cv);

            cv.clear();
            cv.put(MEALTIME_FOOD_COLUMN_MEALTIME_ID, 2);
            cv.put(MEALTIME_FOOD_COLUMN_FOOD_ID, 7);
            db.insert(MEALTIME_FOOD_TABLE, null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d (LOG_TAG, "onUpdate");
            db.execSQL(DB_DELETE);
            Log.d (LOG_TAG, "myTab delete");

            db.execSQL(FOOD_TYPE_TABLE_DELETE);
            Log.d (LOG_TAG, "Food Type delete");
            db.execSQL(FOOD_TABLE_DELETE);
            Log.d (LOG_TAG, "Food delete");

            db.execSQL(MEALTIME_TABLE_DELETE);
            Log.d (LOG_TAG, "MEALTIME delete");
            db.execSQL(MEALTIME_FOOD_TABLE_DELETE);
            Log.d (LOG_TAG, "MEALTIME Food delete");

            onCreate(db);
            Log.d (LOG_TAG, "Food Type create");
            Log.d (LOG_TAG, "Food create");
            Log.d (LOG_TAG, "MEALTIME create");
            Log.d (LOG_TAG, "MEALTIME Food create");



        }
    }
}
