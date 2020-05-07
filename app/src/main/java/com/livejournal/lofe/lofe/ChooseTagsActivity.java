package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.livejournal.lofe.lofe.DBHelper.*;
import static com.livejournal.lofe.lofe.MyUtil.log;

// TODO эту активити и соотв слой переименовать, сортировка не только по ярлыкам

// Активити выбора ярлыков для сортировки по ярлыкам
public class ChooseTagsActivity extends FragmentActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    RelativeLayout SortingLayout;
    Button bExpandCollapse, ibOk, ibNone, ibAll, bByDate, bOpenMap;
    RadioButton rbHighPriorityFirst;
    TextView tvDate;
    GridView gvTags;
    CheckBox cbApplyTime;
    DB db;
    long msStartTime;   // Момент времени, с которого будем отображать ярлыки

    TagsAdapter tagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tags);

        SortingLayout = findViewById(R.id.ChooseTagsSortingOptionsLayout);

        cbApplyTime = findViewById(R.id.cbApplyTime_aChooseTags);

        bExpandCollapse = findViewById(R.id.btnChooseTagsExpandCollapse);
        bExpandCollapse.setOnClickListener(this);

        bOpenMap = findViewById(R.id.btnChooseTagsMap);
        bOpenMap.setOnClickListener(this);

        ibOk = findViewById(R.id.btnChooseTagsDialogOk);
        ibOk.setOnClickListener(this);

        ibNone = findViewById(R.id.btnChooseTagsDialogNone);
        ibNone.setOnClickListener(this);

        ibAll = findViewById(R.id.btnChooseTagsDialogAll);
        ibAll.setOnClickListener(this);

        rbHighPriorityFirst = findViewById(R.id.RBP_HighFirst);

        bByDate = findViewById(R.id.btnChooseTagsDialogOrderByDate);
        bByDate.setOnClickListener(this);

        tvDate = findViewById(R.id.tvDate_aChooseTags);
        tvDate.setOnClickListener(this);

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        msStartTime = calendar.getTimeInMillis();
        tvDate.setText("" + new SimpleDateFormat("dd.MM.yy").format(new Date(msStartTime)));

        String[] from = new String[] { TAG_COLUMN_NAME, TAG_COLUMN_ID };                      // формируем столбцы сопоставления
        int[] to = new int[] { R.id.tvTag2Text, R.id.cbTag2Checked};

        tagsAdapter = new TagsAdapter(this, R.layout.item_tag2, null, from, to, 0);                 // создааем адаптер и настраиваем список
        gvTags = findViewById(R.id.gvDialogTags);
        gvTags.setAdapter(tagsAdapter);

        tagsAdapter.swapCursor(getAllTag());
    }

    public void onClick(View v) {
        Intent intent;
        RecordsSortParams sortParams;
        switch (v.getId()) {
            case R.id.tvDate_aChooseTags:
                intent = new Intent(this, AlarmActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnChooseTagsExpandCollapse:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SortingLayout.getWidth(), 300);
                if (SortingLayout.getHeight() < 1000) {
                    params.height = 1000;
                    bExpandCollapse.setText(R.string.BtnCollapse);
                } else
                    bExpandCollapse.setText(R.string.BtnExpand);

                SortingLayout.setLayoutParams(params);
                //SortingLayout.setLayoutParams(new LinearLayout.LayoutParams(SortingLayout.getWidth(), (SortingLayout.getHeight() < 1000) ? 1000 : 300));
                break;
            case R.id.btnChooseTagsDialogNone:
                break;
            case R.id.btnChooseTagsDialogAll:
                tagsAdapter.setAllTags();
                break;
            case R.id.btnChooseTagsDialogOrderByDate:
                sortParams = new RecordsSortParams(true); // TODO задефайнить
                intent = new Intent(this, ChooseTagsActivity.class);
                intent.putExtra(RecordsSortParams.class.getCanonicalName(), sortParams);
                log("sort param3");
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.btnChooseTagsDialogOk:
                ArrayList<Integer> chTaags = tagsAdapter.getCheckedTags();
                intent = new Intent();
                if (chTaags.size() > 0)
                    intent.putExtra("tagId", chTaags.get(0));
                else
                    intent.putExtra("tagId", 0);

                if (cbApplyTime.isChecked())                            // Ярлыки отображать с учётом времени?
                    intent.putExtra("msStartTime", msStartTime);
                else
                    intent.putExtra("msStartTime", 0);

                if (rbHighPriorityFirst.isChecked()) {
                    sortParams = new RecordsSortParams();
                    sortParams.byDecPriority = true;
                    intent.putExtra(RecordsSortParams.class.getCanonicalName(), sortParams);
                }

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
        //return new Loader<Cursor>(this, db);
        return new Loader<>(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        tagsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
