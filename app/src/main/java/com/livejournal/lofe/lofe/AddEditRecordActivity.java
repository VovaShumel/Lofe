package com.livejournal.lofe.lofe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.livejournal.lofe.lofe.model.Alarm;
import com.livejournal.lofe.lofe.model.LofeRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.livejournal.lofe.lofe.DBHelper.*;
import static com.livejournal.lofe.lofe.MyUtil.getCurTimeMS;
import static com.livejournal.lofe.lofe.MyUtil.log;

public class AddEditRecordActivity extends FragmentActivity implements View.OnClickListener,
                                                                       SeekBar.OnSeekBarChangeListener,
                                                                       LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    EditText etRecordText;
    ImageButton ibOk;
    Button btnAddTag;
    GridView gvTags;
    TextView tvDate;
    SeekBar sbPriority;
    long recordId, tagId, ms, showedPriority;
    int position;
    Boolean disallowBack;
    LofeRecord record;
    RadioButton rbNeed, rbCan;

    Tags2Adapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_record);

        Intent intent = getIntent();
        recordId = intent.getLongExtra("id", 0L);
        tagId = intent.getLongExtra("tagId", 0L);
        position = intent.getIntExtra("position", 0);
        disallowBack = intent.getBooleanExtra("disallowBack", false);

        ibOk = findViewById(R.id.imgBtnOkEdtRecord);
        ibOk.setOnClickListener(this);

        btnAddTag = findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(this);

        tvDate = findViewById(R.id.tvDate);
        tvDate.setOnClickListener(this);

        sbPriority = findViewById(R.id.sbPriority);
        sbPriority.setOnSeekBarChangeListener(this);

        etRecordText = findViewById(R.id.etRecordText);

        rbNeed = findViewById(R.id.AddEditRecord_RG_NeedCan_rbNeed);
        rbCan = findViewById(R.id.AddEditRecord_RG_NeedCan_rbCan);

        if (recordId > 0) {                                                                               // редактирование записи
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);       // Чтобы автоматически не отображалась
                                                                                                    // клавиатура с фокусом ввода в etRecordText
            etRecordText.setText(getRecordText(recordId)); // TODO совместить эти действия в одно
            //SetDateText(getRecordDate(recordId));REFACT удалить после проверки нового
            record = getRecord(recordId);
            if (record.isNeed())
                rbNeed.setChecked(true);
            else
                rbCan.setChecked(true);

            showedPriority = record.getPriority();

            log("Зашли в ветку по recordId > 0, и record.time=" + record.getTime());
        } else {                                                                                    // В новой записи, сразу должна появляться клавиатура,
            //tvDateTime.setTextColor(0);                                                             // фокус ввода — поле ввода текста записи
            //SetDateText(MyUtil.getCurTimeMS());REFACT удалить после проверки нового
            record = new LofeRecord(MyUtil.getCurTimeMS());
            showedPriority = 10;
        }

        SetDateText(record.getTime());
        sbPriority.setProgress((int)showedPriority);

        String[] from = new String[] { TAG_COLUMN_NAME, TAG_COLUMN_ID };                      // формируем столбцы сопоставления
        int[] to = new int[] { R.id.tvTag2Text, R.id.cbTag2Checked };

        scAdapter = new Tags2Adapter(this, R.layout.item_tag2, null, from, to, 0);                  // создааем адаптер и настраиваем список
        gvTags = findViewById(R.id.gvTags);
        gvTags.setAdapter(scAdapter);

        gvTags.setOnItemClickListener((parent, v, position, id) -> {
            switch ((int)id) {                                        // TODO теоретически возможная ошибка
                case 11:                                              // TODO что за? Пиши подробнее это вроде клик по одному ярлыку
                    startActivity(new Intent(AddEditRecordActivity.this, AlarmActivity.class));
                    break;
            }

            long idRecord = AddEditRecordActivity.this.recordId;
            if (idRecord > 0)
                invertTag(idRecord, id);
            else
                AddEditRecordActivity.this.recordId = addRecordText(etRecordText.getText().toString());
        });

        registerForContextMenu(gvTags);                                          // добавляем контекстное меню к списку

        getSupportLoaderManager().initLoader(0, null, this);     // создаем лоадер для чтения данных
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){}
    @Override
    public void onStartTrackingTouch(SeekBar seekBar){}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //tvDate.setText(String.valueOf(seekBar.getProgress()));
        showedPriority = seekBar.getProgress();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgBtnOkEdtRecord:
                String s = etRecordText.getText().toString();
                if (s != null) { // TODO тут надо проверять не на нулл, а на пустую строку
                    if (recordId > 0)
                        edtRecordText(s, recordId);
                    else {
                        recordId = addRecordText(s);   // TODO потом слить в одну операцию
                        edtRecordDate(recordId, getCurTimeMS());
                        record.setId(recordId); // REFACT потом recordId убрать, использовать только recordId
                    }

                    if (ms != 0)
                        edtRecordDate(recordId, ms);


                    edtRecordPriority(recordId, showedPriority);

                    // REFACT вместо отдельных записей выше и ниже подготовить сначала record а потом записать её?

                    //edtRecordNeeding(recordId, rbNeed.isChecked()); TODO реализовать это

                    ArrayList<ChTag> chTags = scAdapter.getChTags();
                    for(int i = 0; i < chTags.size(); i++)
                        invertTag(recordId, chTags.get(i).id);

                    Alarm alarm = record.getAlarm();

                    if (alarm.isEnabled()) {
                        AlarmReceiver.setReminderAlarm(this, alarm);
                        log("Будильник установлен на " + new SimpleDateFormat("dd.MM.yy HH:mm").format(alarm.getTime()));
                    } else {
                        AlarmReceiver.cancelReminderAlarm(this, alarm);
                        log("Будильник для записи " + recordId + " выключен");
                    }

                    NotificationManagerCompat.from(this).cancel((int)recordId);

                } else if (recordId > 0)
                    delRec(recordId);

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("tagId", tagId);
                intent.putExtra("position", position);
                startActivity(intent);
                break;

            case R.id.btnAddTag:
                intent = new Intent(this, AddEditTagActivity.class);                         // Будем передавать в экран AddEditTag
                intent.putExtra("id", 0);                                                           // Новый ярлык
                startActivity(intent);
                break;
            case R.id.tvDate:
                intent = new Intent(this, AlarmActivity.class);
                intent.putExtra("recordId", recordId);
                intent.putExtra("RECORD", record);
                startActivityForResult(intent, 1);
                break;
        }
    }

    void SetDateText(long ms) {
        if (ms == 0)
            tvDate.setText(R.string.DATE_UNDEFINED);
        else
            tvDate.setText(new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(ms)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}         // TODO тут тоже соответственно упростить? А что это значит, "тоже соответсвенно упростить"?
                                            // пиши яснее
        //ms = data.getLongExtra("ms", 0);    // REFACT потом заменить на работу с LofeRecord
        record = data.getParcelableExtra("RECORD");
        ms = record.getTime();// REFACT потом убрать это, использовать только объект record
        SetDateText(record.getTime());
    }

    @Override
    public void onBackPressed() {
        if (disallowBack) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("disallowBack", true);
            startActivity(i);
        } else
            super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, this.recordId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {
        long id;

        MyCursorLoader(Context context, long id) {
            super(context);
            this.id = id;
        }

        @Override
        public Cursor loadInBackground() {
            return getRecordTags(id);
        }
    }
}
