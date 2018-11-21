package ru.seminma.rfood;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MealTime extends AppCompatActivity implements LoaderCallbacks <Cursor> {

    private int DIALOG_TIME = 2;
    int myHour = 14;
    int myMinute = 35;


    TextView tvTime;

    private static final int CM_DELETE_ID = 1;
    private SimpleCursorAdapter scAdapter;

    private Data data;

    private String new_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_time);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mealtimeToolbar);
        setSupportActionBar(toolbar);

        tvTime = (TextView) findViewById(R.id.tvTime);
/*
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FoodActivity.class);
                intent.putExtra("new_id", new_id+"");
                startActivityForResult(intent, 1);
            }
        });*/

        FloatingActionButton fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG Mealtime","Click fabOk");
                Intent intent = new Intent();

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        new_id = intent.getStringExtra("new_id");
        Log.d("LOG Mealtime",new_id);

        data = new Data(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        TextView tvDay = (TextView) findViewById(R.id.tvDay);
        long day = Long.parseLong(data.getMealtimeDay(Long.parseLong( new_id)));
        tvDay.setText(sdf.format(day));

        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText(data.getMealtime(Long.parseLong( new_id)));

        // формируем столбцы сопоставления
        String[] from = new String[] {"food"};
        int[] to = new int[] {R.id.tvText2 };

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.mealtime_item, null, from, to, 0);
        ListView lvMealTimeFood = (ListView) findViewById(R.id.lvMealTimeFood);
        lvMealTimeFood.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvMealTimeFood);

        lvMealTimeFood.setClickable(true);

        lvMealTimeFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Log.d("LOG Mealtime", "on click: position = " + position + " arg3 = " + id);
                Intent intent = new Intent(v.getContext(), FoodActivity.class);
                intent.putExtra("new_id", new_id);
                startActivityForResult(intent, 1);
            }
        });

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            data.deleteMealtimeFood(Long.parseLong(new_id), acmi.id);
            Log.d("LOG MealTime", "delete : id = " + acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
   /* public void onClickOK(View view) {
        Intent intent = new Intent();

        setResult(RESULT_OK, intent);
        finish();
    }*/

    public void onClickAdd(View view) {

        Intent intent = new Intent(this, FoodActivity.class);
        intent.putExtra("new_id", new_id+"");
        startActivityForResult(intent, 1);
    }

    public void onClickTime(View view) {
            showDialog(DIALOG_TIME);
        }



    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            String stringTime = hourOfDay + "-" + minute;
            tvTime.setText(stringTime);

        }
    };

    protected void onDestroy() {
        super.onDestroy();
        data.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (intentData == null) {return;}
        String foodName = intentData.getStringExtra("foodName");


       // data.addJournalItem(foodName , R.drawable.ic_menu_camera);



        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // получаем новый курсор с данными
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MealtimeFoodCursorLoader(this, data.getDB(), Long.parseLong(new_id));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
