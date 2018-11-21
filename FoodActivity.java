package ru.seminma.rfood;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import java.util.HashSet;

public class FoodActivity extends AppCompatActivity  {

    private ExpandableListView elvMain;
    private Data data;
    private String new_id;
    private HashSet<Long> foodsAddID;
    private HashSet<Long> foodsDelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFood);
        setSupportActionBar(toolbar);

        FloatingActionButton fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                data.addMealtimeFood (new_id, foodsAddID);
                data.delMealtimeFood (new_id, foodsDelID);
                intent.putExtra("foodName", "TEST");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        foodsAddID = new HashSet<>();
        foodsDelID = new HashSet<>();

        Intent intent = getIntent();
        new_id = intent.getStringExtra("new_id");
        Log.d("LOG Food",new_id);

        data = new Data(this);

        // готовим данные по группам для адаптера
        Cursor cursor = data.getFoodTypeData();
        startManagingCursor(cursor);
        // сопоставление данных и View для групп
        String[] groupFrom = { DB.FOOD_TYPE_COLUMN_NAME };
        int[] groupTo = { android.R.id.text1 };
        // сопоставление данных и View для элементов
        String[] childFrom = { "name" ,"mealtime"};
        int[] childTo = { android.R.id.text1};

        // устанавливаем режим выбора пунктов списка
       // elvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // создаем адаптер и настраиваем список
        SimpleCursorTreeAdapter sctAdapter = new MyAdapter(this, cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_multiple_choice, childFrom,
                childTo);
        elvMain = (ExpandableListView) findViewById(R.id.elvFood);
        elvMain.setAdapter(sctAdapter);

        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

      /* You must make use of the View v, find the view by id and extract the text as below*/

                CheckedTextView ctv= (CheckedTextView) v.findViewById(android.R.id.text1);
                String data= ctv.getText().toString();
                ctv.toggle();
                if (ctv.isChecked()) {
                    foodsAddID.add(id);
                    foodsDelID.remove(id);
                } else {
                    foodsAddID.remove(id);
                    foodsDelID.add(id);
                }
                Log.d("LOG Food", "onClick checked : " + data + "  id = " + id + " child position = " + childPosition);
                return true;  // i missed this
            }
        });

        elvMain.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Log.d("LOG Food", "onGroupExpand groupPosition : " + groupPosition);
            }
        });
    }

    class MyAdapter extends SimpleCursorTreeAdapter {

        public MyAdapter(Context context, Cursor cursor, int groupLayout,
                         String[] groupFrom, int[] groupTo, int childLayout,
                         String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // получаем курсор по элементам для конкретной группы
            int idColumn = groupCursor.getColumnIndex(DB.FOOD_TYPE_COLUMN_ID);
            Cursor cursor = data.getFoodData(Long.parseLong(new_id), groupCursor.getInt(idColumn));

            return cursor;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

            CheckedTextView title = (CheckedTextView) view.findViewById(android.R.id.text1);
            title.setText(cursor.getString(cursor.getColumnIndex("name")));
            boolean chek = false;
            if (cursor.getInt(cursor.getColumnIndex("mealtime")) != 0 & !cursor.isNull(cursor.getColumnIndex("mealtime"))) {
                chek = true;
            }
            title.setChecked(chek);
        }
    }


}
