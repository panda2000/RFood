package ru.seminma.rfood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks <Cursor> {

    Context ctx;

    private int DIALOG_DATE = 1;
    private int myYear = 2018;
    private int myMonth = 10;
    private int myDay = 13;

    TextView tvDay;

    LoaderCallbacks <Cursor> lc = (LoaderCallbacks<Cursor>) this;

    private static final int CM_DELETE_ID = 2;
    SimpleCursorAdapter scAdapter;
    private long startDate = 0;
    private long stopDate = 0;

    Data data;
    //Presenter presenter = new Presenter();
    Model model = new Model();

    Animation animInForward;
    Animation animOutForward;
    Animation animInBackward;
    Animation animOutBackward;

    CoordinatorLayout clMainLayout;

    private GestureDetectorCompat lSwipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long new_id = data.newMealtime (System.currentTimeMillis());

                Intent intent = new Intent(view.getContext(), FoodActivity.class);
                intent.putExtra("new_id", new_id+"");
                startActivityForResult(intent, 1);
            }
        });

        tvDay = (TextView) findViewById(R.id.tvDay);

        Log.d("LOG MAIN", "new GestureDetectorCompat ");
        lSwipeDetector =new GestureDetectorCompat(this, new MyGestureListener());
        Log.d("LOG MAIN", "R.id.clMainLayout ");
        clMainLayout = (CoordinatorLayout) findViewById(R.id.clMainLayout) ;

        clMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("LOG MAIN", "onTouch ");
                return lSwipeDetector.onTouchEvent(event);
            }
        });

        data = new Data(this);

        long timeStamp =  System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        final Date currentDate = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        myYear = calendar.get(Calendar.YEAR);
//Add one to month {0 - 11}
        myMonth = calendar.get(Calendar.MONTH);
        myDay = calendar.get(Calendar.DAY_OF_MONTH);

        startDate = DateTimeConverter.String2UTS (sdf.format(currentDate));
        stopDate = startDate + 24 * 60 * 60 * 1000;


        tvDay.setText(sdf.format(currentDate));

        Log.d("LOG MAIN", "Start Main : timestamp = " + new Date (timeStamp).toString() + " start = " + new Date (startDate).toString() + " stop = " + new Date (stopDate).toString());

        // формируем столбцы сопоставления
        String[] from = new String[] { "name", "food"};
        int[] to = new int[] { R.id.tvText, R.id.tvText2   };

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.mealtime_item, null, from, to, 0);
        ListView lvMealTimes = (ListView) findViewById(R.id.lvMealTimes);
        lvMealTimes.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvMealTimes);

        lvMealTimes.setClickable(true);

        lvMealTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Log.d("LOG MAIN", "on click: position = " + position + " arg3 = " + id );
                Intent intent = new Intent(v.getContext(), MealTime.class);
                intent.putExtra("new_id", id+"");


                startActivityForResult(intent, 1);
            }
        });

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

        //animInForward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        //animOutForward = AnimationUtils.loadAnimation(this, R.anim.flipout);
        animInBackward = AnimationUtils.loadAnimation(this,
                R.anim.flipin_reverse);
        animOutBackward = AnimationUtils.loadAnimation(this,
                R.anim.flipout_reverse);

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("LOG MAIN", "onDown ");
            return true;
        }
        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            Log.d("LOG MAIN", "onFling ");
            Intent intent = new Intent(ctx, BodyVolumesActivity.class);
            startActivity(intent);
            return  false;
        }
    }

/*
    public void onClickAdd(View view) {

        long new_id = data.newMealtime ();

        Intent intent = new Intent(this, MealTime.class);
        intent.putExtra("new_id", new_id+"");
        startActivityForResult(intent, 1);
    }

    public void onClickToFAB(View view) {

        Intent intent = new Intent(this, FABActivity.class);
        startActivity(intent);
    }
*/

    public void next(View view) {

        Intent intent = new Intent(this, BodyVolumesActivity.class);
        startActivity(intent);
    }

    public void onClickDate(View view) {

        showDialog(DIALOG_DATE);
    }



    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            String stringDate = myDay + "-" + myMonth + "-" + myYear;
            tvDay.setText(stringDate);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Date date = null;
            try {
                date = sdf.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = DateTimeConverter.String2UTS (sdf.format(date));
            stopDate = startDate + 24 * 60 * 60 * 1000;

            // получаем новый курсор с данными
            getSupportLoaderManager().restartLoader(0,null,lc);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (intentData == null) {return;}
        String foodName = intentData.getStringExtra("foodName");
        String time = intentData.getStringExtra("time");


        data.addJournalItem(foodName , time, R.drawable.ic_menu_camera);



        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // получаем новый курсор с данными
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            data.deleteMealtime(acmi.id);
            Log.d("LOG MAIN", "delete: " + acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        data.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MealtimeCursorLoader(this, data.getDB(),startDate, stopDate);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }



    public void onClickShare(View view) {

        String message = tvDay.getText() + "\n";
        message += data.getMessage(startDate, stopDate);
        Intent intent = model.share(message,"com.whatsapp");
        startActivity(intent);
    }

}
