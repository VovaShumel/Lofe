package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

// Активити выбора ярлыков для сортировки по ярлыкам
public class ChooseTagsActivity extends FragmentActivity implements View.OnClickListener,
                                                        LoaderCallbacks<Cursor> {
    Button ibOk, ibNone, ibAll, ibCncl;
    GridView gvTags;
    DB db;

    long[] idTags = {0};

    TagsAdapter tagsAdapter;
    SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_tags);

        ibOk = (Button) findViewById(R.id.btnChooseTagsDialogOk);
        ibOk.setOnClickListener(this);

        ibNone = (Button) findViewById(R.id.btnChooseTagsDialogNone);
        ibNone.setOnClickListener(this);

        ibAll = (Button) findViewById(R.id.btnChooseTagsDialogAll);
        ibAll.setOnClickListener(this);

        ibCncl = (Button) findViewById(R.id.btnChooseTagsDialogCncl);
        ibCncl.setOnClickListener(this);

        db = new DB(this);                                                                          // открываем подключение к БД
        db.open();

        String[] from = new String[] { DB.TAG_COLUMN_NAME, DB.TAG_COLUMN_ID };                      // формируем столбцы сопоставления
        int[] to = new int[] { R.id.tvTag2Text, R.id.cbTag2Checked};

        tagsAdapter = new TagsAdapter(this, R.layout.item_tag2, null, from, to, 0);                 // создааем адаптер и настраиваем список
        gvTags = (GridView) findViewById(R.id.gvDialogTags);
        gvTags.setAdapter(tagsAdapter);

        getSupportLoaderManager().initLoader(0, null, this);                                        // создаем лоадер для чтения данных
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChooseTagsDialogNone:
                break;
            case R.id.btnChooseTagsDialogAll:
                tagsAdapter.setAllTags();
                break;
            case R.id.btnChooseTagsDialogOk:
            case R.id.btnChooseTagsDialogCncl:
                db.close();
                ArrayList<Integer> chTaags = tagsAdapter.getCheckedTags();
                Intent intent = new Intent();
                if (chTaags.size() > 0) {
                    intent.putExtra("id", chTaags.get(0));
                } else {
                    intent.putExtra("id", 0);
                }
                setResult(RESULT_OK, intent);
                finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        tagsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getAllTag();
        }
    }
}
