package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    public ListView lvData;
    DB db;
    long idd = 0;
    int position = 0;
    SimpleCursorAdapter scAdapter;
    Button btn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);
        db.open();

        String[] from = new String[] { DB.R_COLUMN_TEXT };
        int[] to = new int[] {R.id.tv__item_record__recordText};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item_record, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        registerForContextMenu(lvData);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                intent.putExtra("id", id);                         // id записи, которую необходимо отредактировать
                //intent.putExtra("position", lvData.getFirstVisiblePosition());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class); // Будем передавать в экран AddEditRecord
                intent.putExtra("id", 0);                                                   // код "0", сигнализурющий о том, что запись нужно
                                                                                            // не редактировать, а добавлять
                startActivity(intent);
                getSupportLoaderManager().getLoader(0).forceLoad();                         // получаем новый курсор с данными
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (position > 0) {
            lvData.smoothScrollToPositionFromTop(position, 0, 0);
            position = 0;
        }
    }

    public void onButtonClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button:
                intent = new Intent(this, ChooseTagsActivity.class);  // Будем передавать в экран AddEditRecord
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        idd = data.getLongExtra("id", 0);
        getSupportLoaderManager().restartLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();  // получаем из пункта контекстного меню данные о пункте списка

        switch (item.getItemId()) {

            case CM_EDIT_ID:
                Intent intent = new Intent(this, AddEditRecordActivity.class);  // извлекаем id записи и вызываем меню редактирования записи
                intent.putExtra("id", acmi.id);
                startActivity(intent);
                break;

            case CM_DELETE_ID:
                db.delRec(acmi.id); // извлекаем id записи и удаляем соответствующую запись в БД
                break;

            default:
                return super.onContextItemSelected(item);
        }
        getSupportLoaderManager().getLoader(0).forceLoad();     // получаем новый курсор с данными
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
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

    static private class MyCursorLoader extends CursorLoader {

        DB db;
        long id;

        private MyCursorLoader(Context context, DB db, long id) {
            super(context);
            this.db = db;
            this.id = id;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            if (id == 0)
                cursor = db.getAllData();
            else {
                cursor = db.getTagedRecord(id);

            }
            return cursor;
        }
    }
}

