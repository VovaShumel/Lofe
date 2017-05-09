package com.livejournal.lofe.lofe;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.livejournal.lofe.lofe.R.color.colorInactive;

public class AddEditRecordActivity extends FragmentActivity implements View.OnClickListener,
                                                        LoaderCallbacks<Cursor> {
    private static final int CM_EDIT_ID = 1;
    private static final int CM_DELETE_ID = 2;
    EditText etRecordText;
    ImageButton ibOk;
    ImageButton ibCncl;
    Button btnAddTag;
    GridView gvTags;
    TextView tvDateTime;
    long id;
    DB db;

    //SimpleCursorAdapter scAdapter;
    Tags2Adapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_record);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        ibOk = (ImageButton) findViewById(R.id.imgBtnOkEdtRecord);
        ibOk.setOnClickListener(this);

        ibCncl = (ImageButton) findViewById(R.id.imgBtnCnclEdtRecord);
        ibCncl.setOnClickListener(this);

        btnAddTag = (Button) findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(this);

        tvDateTime = (TextView) findViewById(R.id.tvADTDateTime);
        tvDateTime.setOnClickListener(this);

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        etRecordText = (EditText) findViewById(R.id.etRecordText);

        if (id > 0) {   // редактирование записи
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   // Чтобы автоматически не отображалась
                                                                                                // клавиатура с фокусом ввода в etRecordText
            etRecordText.setText(db.getRecordText(id));
        } else {
            tvDateTime.setTextColor(0);
            tvDateTime.setText("Время и дата не заданы");
        }
            // Если же новая запись, то сразу должна появляться клавиатура, фокус ввода -- поле ввода текста записи

//        // формируем столбцы сопоставления
//        String[] from = new String[] { DB.TAG_COLUMN_NAME, DB.TAG_COLUMN_ID };
//        int[] to = new int[] { R.id.tvTagText, R.id.tvTagChecked};
//
//        // создааем адаптер и настраиваем список
//        scAdapter = new SimpleCursorAdapter(this, R.layout.tag, null, from, to, 0);
//        gvTags = (GridView) findViewById(R.id.gvTags);
//        gvTags.setAdapter(scAdapter);

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.TAG_COLUMN_NAME, DB.TAG_COLUMN_ID };
        int[] to = new int[] { R.id.tvTag2Text, R.id.cbTag2Checked };

        // создааем адаптер и настраиваем список
        scAdapter = new Tags2Adapter(this, R.layout.item_tag2, null, from, to, 0);
        gvTags = (GridView) findViewById(R.id.gvTags);
        gvTags.setAdapter(scAdapter);

        MyLog.d("Зашли в ээ");

        gvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                switch ((int)id) { // TODO теоретически возможная ошибка
                    case 11:
                        //intent = new Intent(this, Alarm_Activity.class);  // Будем передавать в экран AddEditRecord
                        MyLog.d("Открыли будильник");
                        startActivity(new Intent(AddEditRecordActivity.this, AlarmActivity.class));
                        MyLog.d("Закрыли будильник");
                        break;
                }

                long idRecord = AddEditRecordActivity.this.id;
                MyLog.d("Кликнули по итему id = " + idRecord);
                if (idRecord > 0) {
                    db.invertTag(idRecord, id);
                } else {
                    AddEditRecordActivity.this.id = db.addRecordText(etRecordText.getText().toString());
                    //db.assignTag(AddEditRecord.this.id, id);
                    MyLog.d("Назначили ярлык" + db.assignTag(AddEditRecordActivity.this.id, id));
                }
            }
        });

        // добавляем контекстное меню к списку
        registerForContextMenu(gvTags);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {

        //Toast.makeText(AddEditRecord.this, "бебебе111", Toast.LENGTH_SHORT).show();

        // получаем из пункта контекстного меню данные по пункту списка
//        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

//        switch (item.getItemId()) {
//
//            case CM_EDIT_ID:
//                // извлекаем id записи и вызываем меню редактирования записи
//                Intent intent = new Intent(this, AddEditRecord.class);  // Будем передавать в экран AddEditRecord
//                //Intent intent = new Int
//                intent.putExtra("id", acmi.id);                         // id записи, которую необходимо отредактировать
//                // не редактировать, а добавлять
//                startActivity(intent);
//                break;
//
//            case CM_DELETE_ID:
//                // извлекаем id записи и удаляем соответствующую запись в БД
//                db.delRec(acmi.id);
//                break;
//
//            default:
//                return super.onContextItemSelected(item);
//        }
        // получаем новый курсор с данными
        //getSupportLoaderManager().getLoader(0).forceLoad();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgBtnOkEdtRecord:
                String s = etRecordText.getText().toString();
                if (s != null) {
                    if (id > 0) {
                        db.edtRecordText(s, id);
                    } else {
                        id = db.addRecordText(s);
                    }

                    ArrayList<ChTag> chTags = scAdapter.getChTags();
                    //MyLog.d("Таг меняется задумчиво9");
                    for(int i = 0; i < chTags.size(); i++) {
                        MyLog.d("Таг меняется задумчиво");
                        db.invertTag(id, chTags.get(i).id);
//                        if (chTags.get(i).checked) {
//                            db.
//                        }
                    }

                } else {
                    if (id > 0) {
                        db.delRec(id);
                    }
                }
                break;
            
            case R.id.imgBtnCnclEdtRecord:
                break;

            case R.id.btnAddTag:
                Intent intent = new Intent(this, AddEditTagActivity.class); // Будем передавать в экран AddEditTag
                intent.putExtra("id", 0);                               // Новый ярлык
                startActivity(intent);
                break;
        }

        if (v.getId() != R.id.btnAddTag) {
            db.close();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db, this.id);
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
//            if (id > 0) {
//                //cursor = db.getTagsDataByID(id);
//                cursor = db.getAllTag();
//            } else {
                //cursor = db.getAllTag();
            MyLog.d("ID записи, для которой задано посмотреть ярлыки = " + id);
                cursor = db.getRecordTags(id);
           // }
            return cursor;
        }
    }
}
