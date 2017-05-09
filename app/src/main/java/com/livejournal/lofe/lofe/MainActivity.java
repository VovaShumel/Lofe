package com.livejournal.lofe.lofe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    public ListView lvData;
    DB db;
    long idd = 0;
    SimpleCursorAdapter scAdapter;
    Button btn;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dlg1 = new ChooseTags_Dialog();

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        MyLog.d("dg");

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.R_COLUMN_TEXT };
        int[] to = new int[] {R.id.tv__item_record__recordText};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_record, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvData);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "
//                        + id);
                //MyLog.d("Нажатие");
                Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                //Intent intent = new Int
                intent.putExtra("id", id);                         // id записи, которую необходимо отредактировать
                // не редактировать, а добавлять
                startActivity(intent);
            }
        });

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
//                intent.putExtra("id", 0);                               // код "0", сигнализурющий о том, что запись нужно
//                // не редактировать, а добавлять
//                startActivity(intent);
//                // получаем новый курсор с данными
//                getSupportLoaderManager().getLoader(0).forceLoad();
//            }
//        });

    }


    // обработка нажатия кнопки
    public void onButtonClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.imgBtnAddRecord:

                intent = new Intent(this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                intent.putExtra("id", 0);                               // код "0", сигнализурющий о том, что запись нужно
                // не редактировать, а добавлять
                startActivity(intent);
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;

            case R.id.button:
                intent = new Intent(this, ChooseTagsActivity.class);  // Будем передавать в экран AddEditRecord
                //startActivity(intent);
                startActivityForResult(intent, 1);
                break;

            case R.id.buttonToClock:
                intent = new Intent(this, AlarmActivity.class);
                startActivity(intent);
                //startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MyLog.d("Мы вернулись!");
        if (data == null) {return;}
        //long id = data.getLongExtra("id", 0);
        idd = data.getLongExtra("id", 0);
//        MyLog.d(id + "");
//        if (id > 0) {
        //idd = id;
        MyLog.d(idd + "");
        //getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();
//        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {

        // получаем из пункта контекстного меню данные по пункту списка
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case CM_EDIT_ID:
                // извлекаем id записи и вызываем меню редактирования записи
                Intent intent = new Intent(this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                //Intent intent = new Int
                intent.putExtra("id", acmi.id);                         // id записи, которую необходимо отредактировать
                // не редактировать, а добавлять
                startActivity(intent);
                break;

            case CM_DELETE_ID:
                // извлекаем id записи и удаляем соответствующую запись в БД
                db.delRec(acmi.id);
                break;

            default:
                return super.onContextItemSelected(item);
        }
        // получаем новый курсор с данными
        getSupportLoaderManager().getLoader(0).forceLoad();
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db, idd);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;
        long id;

        public MyCursorLoader(Context context, DB db, long id) {
            super(context);
            this.db = db;
            this.id = id;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            MyLog.d("Курсор обновляется");
            if (id == 0)
                cursor = db.getAllData();
            else {
                cursor = db.getTagedRecord(id);

            }
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return cursor;
        }
    }
}

