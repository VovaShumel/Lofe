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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Активити выбора ярлыков для сортировки по ярлыкам
public class ChooseTagsActivity extends FragmentActivity implements View.OnClickListener,
                                                        LoaderCallbacks<Cursor> {
    Button ibOk, ibNone, ibAll;
    TextView tvDate;
    GridView gvTags;
    DB db;
    long ms;

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

        tvDate = (TextView) findViewById(R.id.tvActChTagsDate);
        tvDate.setOnClickListener(this);

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
            case R.id.tvActChTagsDate:
                //Toast.makeText(this, "На дату нажимается", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AlarmActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnChooseTagsDialogNone:
                break;
            case R.id.btnChooseTagsDialogAll:
                tagsAdapter.setAllTags();
                break;
            case R.id.btnChooseTagsDialogOk:
                db.close();
                ArrayList<Integer> chTaags = tagsAdapter.getCheckedTags();
                intent = new Intent();
                if (chTaags.size() > 0) {
                    intent.putExtra("id", chTaags.get(0));
                } else {
                    intent.putExtra("id", 0);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}         // TODO тут тоже соответственно упростить?
        ms = data.getLongExtra("ms", 0);
        if (ms == 0)
            tvDate.setText(R.string.DATE_UNDEFINED);
        else
            tvDate.setText("Не раньше " + new SimpleDateFormat("dd.MM.yy").format(new Date(ms)));
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

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
