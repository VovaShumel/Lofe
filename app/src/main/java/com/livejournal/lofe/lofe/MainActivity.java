package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.livejournal.lofe.lofe.MyLog.d;
import static com.livejournal.lofe.lofe.MyUtil.log;
import static com.livejournal.lofe.lofe.DBHelper.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    public ListView lvData;
    long tagId = 0L;
    ArrayList<Integer> tagIds = null;
    long msStartTime;
    RecordsSortParams sortParams;
    int position = 0;
    SimpleCursorAdapter scAdapter;
    ImageButton ibFilter;
    Parcelable state;
    TextView tvSearch;
    HTTPD httpd;
    WSServer WSS;
    Boolean disallowBack;

    public void RedrawItemsList(Cursor cursor) {
        scAdapter.swapCursor(cursor);
        if(state != null) {
            lvData.onRestoreInstanceState(state);
            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(tvSearch.getWindowToken(), 0);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        disallowBack = intent.getBooleanExtra("disallowBack", false);

        msStartTime = MyUtil.getCurTimeMS();

        httpd = new HTTPD();

        WSS = new WSServer();
        WSS.start();
        log("" + WSS.getConnectionLostTimeout());

        ibFilter = findViewById(R.id.imgFilter);
        ibFilter.setOnClickListener(this);

        tvSearch = findViewById(R.id.TV_AM_Find);
        tvSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() >= 3)
                    RedrawItemsList(GetWithSubstr(str));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        //db.AddColumn("RECORD", "PRECEDENT_ACTION");
        //db.AddColumn("RECORD", "NEXT_ACTION");

        //db.GetColumnNames();
        //db.DeleteTag("Дата и время");
        //db.DeleteTag(15);
        //db.GetTagTable();

        String[] from = new String[] { R_COLUMN_TEXT };
        int[] to = new int[] {R.id.tv__item_record__recordText};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item_record, null, from, to, 0);
        lvData = findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        registerForContextMenu(lvData);

        final SwipeDetector swipeDetector = new SwipeDetector();
        lvData.setOnTouchListener(swipeDetector);

        lvData.setOnItemClickListener((parent, view, position, id) -> {

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
                Intent intent1 = new Intent(MainActivity.this, AddEditRecordActivity.class);  // Будем передавать в экран AddEditRecord
                intent1.putExtra("id", id);                         // id записи, которую необходимо отредактировать
                //intent.putExtra("position", lvData.getFirstVisiblePosition());
                intent1.putExtra("tagId", tagId);
                intent1.putExtra("position", position);
                startActivity(intent1);
            }
        });

        position = intent.getIntExtra("position", 0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //RedrawItemsList(GetCursor(tagId, msStartTime, sortParams));
        RedrawItemsList(GetCursor(tagIds, msStartTime, sortParams));

        //getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        //fab.setImageResource(R.drawable.img_add2);
        fab.setBackgroundResource(R.drawable.img_add2);
        fab.setOnClickListener(view -> {
            Intent intent12 = new Intent(MainActivity.this, AddEditRecordActivity.class); // Будем передавать в экран AddEditRecord
            intent12.putExtra("id", 0L);                                                   // код "0", сигнализурющий о том, что запись нужно
                                                                                        // не редактировать, а добавлять
            //intent.putExtra("tagId", tagId);
            startActivity(intent12);
            //startActivityForResult(intent, 1);
            //getSupportLoaderManager().getLoader(0).forceLoad();                         // получаем новый курсор с данными
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
    public void onResume() {
        msStartTime = MyUtil.getCurTimeMS();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        RecordsSortParams newSortParams = data.getParcelableExtra(RecordsSortParams.class.getCanonicalName());
        if (newSortParams != null) {
            //if (sortParams.sortByIncTime)
            sortParams = newSortParams;
        } else {
            sortParams = null;
            //tagId = data.getLongExtra("tagId", 0);
            tagIds = data.getIntegerArrayListExtra("tagIds");
            msStartTime = data.getLongExtra("msStartTime", 0);
            //log("При возвращении из сортировки ярлыков получили " + msStartTime + " мс");
        }
        //getSupportLoaderManager().restartLoader(0, null, this);
        //getSupportLoaderManager().getLoader(0).forceLoad();
        //RedrawItemsList(GetCursor(tagId, msStartTime, sortParams));
        RedrawItemsList(GetCursor(tagIds, msStartTime, sortParams));
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
                delRec(acmi.id); // извлекаем id записи и удаляем соответствующую запись в БД
                break;

            default:
                return super.onContextItemSelected(item);
        }
        //getSupportLoaderManager().getLoader(0).forceLoad();     // получаем новый курсор с данными
        //RedrawItemsList(GetCursor(tagId, msStartTime, sortParams));
        RedrawItemsList(GetCursor(tagIds, msStartTime, sortParams));
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        httpd.destroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new Loader<>(this);
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

    @Override
    public void onBackPressed() {
        if (!disallowBack)
            super.onBackPressed();
    }
}

