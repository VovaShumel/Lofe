package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import static com.livejournal.lofe.lofe.MyLog.d;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
                                                                LoaderCallbacks<Cursor> {

    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    public ListView lvData;
    DB db;
    long tagId = 0;
    long msStartTime = MyUtil.getCurTimeMS();
    int position = 0;
    SimpleCursorAdapter scAdapter;
    ImageButton ibFilter;
    Parcelable state;
    AutoCompleteTextView tvSearch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ibFilter = (ImageButton) findViewById(R.id.imgFilter);
        ibFilter.setOnClickListener(this);

        tvSearch = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        tvSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() >= 3)
                    db.GetWithSubstr(str);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        db = new DB(this);  // TODO вроде бы это можно перенести непосредственно в работу с курсором
        db.open();

        //db.AddColumn();
        //db.GetColumnNames();
        //db.DeleteTag("Дата и время");
        //db.DeleteTag(15);
        //db.GetTagTable();

        String[] from = new String[] { DB.R_COLUMN_TEXT };
        int[] to = new int[] {R.id.tv__item_record__recordText};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item_record, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        registerForContextMenu(lvData);

        final SwipeDetector swipeDetector = new SwipeDetector();
        lvData.setOnTouchListener(swipeDetector);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (swipeDetector.swipeDetected()) {                                // свайп?
                    SwipeDetector.SwipeAction swipeAction = swipeDetector.getAction();

                    if (swipeAction.action == SwipeDetector.Action.RL) {
                        d("" + swipeAction.velocityX);
                        // TODO тут нужно прописать удаление дела с анимацией и возможностью отменить, типа как в гмайл
                    }else if (swipeAction.action == SwipeDetector.Action.LR) {
                        d("" + swipeAction.velocityX);
                        // TODO тут нужно сдвигать дело на завтра с анимацией
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                    intent.putExtra("id", id);                         // id записи, которую необходимо отредактировать
                    //intent.putExtra("position", lvData.getFirstVisiblePosition());
                    intent.putExtra("tagId", tagId);
                    intent.putExtra("position", position);
                    //startActivity(intent);
                    startActivityForResult(intent, 1);
                }
            }
        });

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setImageResource(R.drawable.img_add2);
        fab.setBackgroundResource(R.drawable.img_add2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditRecordActivity.class); // Будем передавать в экран AddEditRecord
                intent.putExtra("id", 0);                                                   // код "0", сигнализурющий о том, что запись нужно
                                                                                            // не редактировать, а добавлять
                intent.putExtra("tagId", tagId);
                //startActivity(intent);
                startActivityForResult(intent, 1);
                getSupportLoaderManager().getLoader(0).forceLoad();                         // получаем новый курсор с данными
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (position > 0) {
            //lvData.smoothScrollToPositionFromTop(position, 0, 0);
            position = 0;
        }
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imgFilter:
                intent = new Intent(this, ChooseTagsActivity.class);  // Будем передавать в экран AddEditRecord
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onPause() {
        state = lvData.onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        tagId = data.getLongExtra("tagId", 0);
        msStartTime = data.getLongExtra("msStartTime", 0);
        MyUtil.log("При возвращении из сортировки ярлыков получили " + msStartTime + " мс");
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
        return new MyCursorLoader(this, db, tagId, msStartTime);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
        if(state != null) {
            lvData.onRestoreInstanceState(state);
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tvSearch.getWindowToken(), 0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static private class MyCursorLoader extends CursorLoader {

        DB db;
        long id, msStartTime;

        private MyCursorLoader(Context context, DB db, long id, long msStartTime) {
            super(context);
            this.db = db;
            this.id = id;
            this.msStartTime = msStartTime;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            if (id == 0)
                cursor = db.getAllData(msStartTime);
            else {
                cursor = db.getTagedRecord(id);

            }
            return cursor;
        }
    }
}

