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
import android.text.Layout;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.livejournal.lofe.lofe.MyUtil.log;

// TODO эту активити и соотв слой переименовать, сортировка не только по ярлыкам

// Активити выбора ярлыков для сортировки по ярлыкам
public class ChooseTagsActivity extends FragmentActivity implements View.OnClickListener,
                                                        LoaderCallbacks<Cursor> {
    RelativeLayout SortingLayout;
    Button bExpandCollapse, ibOk, ibNone, ibAll, bByDate, bOpenMap;
    TextView tvDate;
    GridView gvTags;
    CheckBox cbApplyTime;
    DB db;
    long msStartTime;   // Момент времени, с которого будем отображать ярлыки

    long[] idTags = {0};

    TagsAdapter tagsAdapter;
    SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tags);

        SortingLayout = (RelativeLayout) findViewById(R.id.ChooseTagsSortingOptionsLayout);

        cbApplyTime = (CheckBox) findViewById(R.id.cbApplyTime_aChooseTags);

        bExpandCollapse = (Button) findViewById(R.id.btnChooseTagsExpandCollapse);
        bExpandCollapse.setOnClickListener(this);

        bOpenMap = (Button) findViewById(R.id.btnChooseTagsMap);
        bOpenMap.setOnClickListener(this);

        ibOk = (Button) findViewById(R.id.btnChooseTagsDialogOk);
        ibOk.setOnClickListener(this);

        ibNone = (Button) findViewById(R.id.btnChooseTagsDialogNone);
        ibNone.setOnClickListener(this);

        ibAll = (Button) findViewById(R.id.btnChooseTagsDialogAll);
        ibAll.setOnClickListener(this);

        bByDate = (Button) findViewById(R.id.btnChooseTagsDialogOrderByDate);
        bByDate.setOnClickListener(this);

        tvDate = (TextView) findViewById(R.id.tvDate_aChooseTags);
        tvDate.setOnClickListener(this);

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        //Date date = calendar.getTime();
        msStartTime = calendar.getTimeInMillis();
        tvDate.setText("" + new SimpleDateFormat("dd.MM.yy").format(new Date(msStartTime)));

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
        Intent intent;
        switch (v.getId()) {
            case R.id.tvDate_aChooseTags:
                //Toast.makeText(this, "На дату нажимается", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AlarmActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnChooseTagsMap:
                intent = new Intent(this, AlarmActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnChooseTagsExpandCollapse:
                SortingLayout.setLayoutParams(new LinearLayout.LayoutParams(SortingLayout.getWidth(), (SortingLayout.getHeight() < 1000) ? 1000 : 300));
                break;
            case R.id.btnChooseTagsDialogNone:
                break;
            case R.id.btnChooseTagsDialogAll:
                tagsAdapter.setAllTags();
                break;
            case R.id.btnChooseTagsDialogOrderByDate:
                db.close();
                RecordsSortParams sortParams = new RecordsSortParams(true); // TODO задефайнить
                //sortParams.sortByIncTime = true;
                intent = new Intent(this, ChooseTagsActivity.class);
                intent.putExtra(RecordsSortParams.class.getCanonicalName(), sortParams);
                log("sort param3");
                //intent = new Intent();
                //intent.p
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.btnChooseTagsDialogOk:
                db.close();
                ArrayList<Integer> chTaags = tagsAdapter.getCheckedTags();
                intent = new Intent();
                if (chTaags.size() > 0) {
                    intent.putExtra("tagId", chTaags.get(0));
                } else {
                    intent.putExtra("tagId", 0);
                }

                if (cbApplyTime.isChecked())                            // Ярлыки отображать с учётом времени?
                    intent.putExtra("msStartTime", msStartTime);
                else
                    intent.putExtra("msStartTime", 0);

                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}         // TODO тут тоже соответственно упростить?
        msStartTime = data.getLongExtra("ms", 0);
        if (msStartTime == 0)
            tvDate.setText(R.string.DATE_UNDEFINED);
        else
            tvDate.setText("" + new SimpleDateFormat("dd.MM.yy").format(new Date(msStartTime)));
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
