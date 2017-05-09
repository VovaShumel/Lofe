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

public class ChooseTagsActivity extends FragmentActivity implements View.OnClickListener,
                                                        LoaderCallbacks<Cursor> {
    Button ibOk;
    Button ibCncl;
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

        ibCncl = (Button) findViewById(R.id.btnChooseTagsDialogCncl);
        ibCncl.setOnClickListener(this);

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.TAG_COLUMN_NAME, DB.TAG_COLUMN_ID };
        int[] to = new int[] { R.id.tvTag2Text, R.id.cbTag2Checked};

        // создааем адаптер и настраиваем список
        tagsAdapter = new TagsAdapter(this, R.layout.item_tag2, null, from, to, 0);
        //scAdapter = new TagsAdapter(this, R.layout.tag2, null, from, to, 0);
        gvTags = (GridView) findViewById(R.id.gvDialogTags);
        gvTags.setAdapter(tagsAdapter);
        //gvTags.setAdapter(scAdapter);


        MyLog.d("Зашли в фильтр");

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChooseTagsDialogOk:
            case R.id.btnChooseTagsDialogCncl:
                db.close();
                //Intent intent = new Intent(this, Main.class);
                //intent.putExtra("idTags", idTags);
                ArrayList<Integer> chTaags = tagsAdapter.getCheckedTags();
                Intent intent = new Intent();
                if (chTaags.size() > 0) {
                    //intent.putExtra("id", (long)chTaags.get(0));
                    intent.putExtra("id", chTaags.get(0));
                    //MyLog.d("long "+ (long) chTaags.get(0) + "");
//                    for (int i = 0; i < chTaags.size(); i++) {
//                        MyLog.d((int)chTaags.get(i) + "");
//                    }
                } else {
                    intent.putExtra("id", 0);
                }
                setResult(RESULT_OK, intent);
                finish();


                //startActivity(intent);
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
            //Cursor cursor =
            return db.getAllTag();
        }
    }
}
