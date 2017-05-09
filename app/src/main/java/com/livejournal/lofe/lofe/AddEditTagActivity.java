package com.livejournal.lofe.lofe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddEditTagActivity extends Activity implements View.OnClickListener {

    EditText etTagText;
    ImageButton ibOk;
    ImageButton ibCncl;
    long id;
    DB db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_tags);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        ibOk = (ImageButton) findViewById(R.id.imgBtnOkEdtTag);
        ibOk.setOnClickListener(this);

        ibCncl = (ImageButton) findViewById(R.id.imgBtnCnclEdtTag);
        ibCncl.setOnClickListener(this);

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        etTagText = (EditText) findViewById(R.id.etTagName);

        if (id != 0) {   // редактирование записи
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   // Чтобы автоматически не отображалась
            // клавиатура с фокусом ввода в etRecordText
            //etRecordText.setText(db.getRecordText(id));
            etTagText.setText(id + "");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgBtnOkEdtTag:
                String s = etTagText.getText().toString();
                if (s != null) {
                    if (id > 0) {
                        //db.edtRecordText(s, id);
                    } else {
                        db.addTag(s);
                    }
                } else {
                    if (id > 0) {
                        //db.delTag(id);
                    }
                }
                break;

            case R.id.imgBtnCnclEdtTag:
                break;
        }

        db.close();

        Log.d("DEBUG", "Закрываем AddEditTag_Activity");

        Intent intent = new Intent(this, AddEditRecordActivity.class);
        //intent.putExtra("id", id);
        intent.putExtra("id", 0);
        startActivity(intent);

    }
}
