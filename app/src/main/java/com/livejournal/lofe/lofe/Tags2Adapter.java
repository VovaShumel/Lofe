package com.livejournal.lofe.lofe;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class Tags2Adapter extends TagsAdapter {

    ChTag[] chTags, startChTags;

    public Tags2Adapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        //ctx = context;
        //lInflater = (LayoutInflater) ctx
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        super.setViewBinder(new MyViewBinder());
    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View v, Cursor c, int from) {

            if (v.getId() == R.id.cbTag2Checked) {
                ((CheckBox)v).setOnCheckedChangeListener(myCheckChangList);
                v.setTag(c.getPosition());
                ((CheckBox) v).setChecked(chTags[c.getPosition()].checked);

                return true;
            } else
                return false;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        if (c.getCount() > 0) {
            startChTags = new ChTag[c.getCount()];
            chTags = new ChTag[c.getCount()];
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                startChTags[i] = new ChTag(!c.isNull(c.getColumnIndex(DB.RECORD_TAG_COLUMN_RECORD_ID)),
                                            c.getLong(c.getColumnIndex(DB.TAG_COLUMN_ID)));
                chTags[i] = new ChTag(!c.isNull(c.getColumnIndex(DB.RECORD_TAG_COLUMN_RECORD_ID)),
                                      c.getLong(c.getColumnIndex(DB.TAG_COLUMN_ID)));
                c.moveToNext();
            }
        }
        return super.swapCursor(c);
    }

    public ArrayList<ChTag> getChTags() {
        ArrayList result = new ArrayList<ChTag>();
        if (chTags != null) {
            for (int i = 0; i < chTags.length; i++) {
                if (startChTags[i].checked != chTags[i].checked)
                    result.add(chTags[i]);
            }
        }
        return result;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            chTags[(int)buttonView.getTag()].checked = isChecked;
        }
    };

}
