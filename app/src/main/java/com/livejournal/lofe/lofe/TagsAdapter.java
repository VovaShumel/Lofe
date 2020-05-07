package com.livejournal.lofe.lofe;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import static com.livejournal.lofe.lofe.DBHelper.*;

public class TagsAdapter extends SimpleCursorAdapter {

    protected ChTag[] chTags;

    public TagsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        super.setViewBinder(new MyViewBinder());
    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View v, Cursor c, int from) {

            if (v.getId() == R.id.cbTag2Checked) {
                ((CheckBox)v).setOnCheckedChangeListener(myCheckChangList);
                ((CheckBox)v).setChecked(chTags[c.getPosition()].checked);
                v.setTag(c.getPosition());
                return true;
            } else
                return false;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        if (c.getCount() > 0) {
            chTags = new ChTag[c.getCount()];
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                chTags[i] = new ChTag(false, c.getLong(c.getColumnIndex(TAG_COLUMN_ID)));
                c.moveToNext();
            }
        }

        return super.swapCursor(c);
    }

    public ArrayList<Integer> getCheckedTags() {
        ArrayList result = new ArrayList<Long>();
        if (chTags != null) {
            for (int i = 0; i < chTags.length; i++) {
                if (chTags[i].checked) {
                    result.add(chTags[i].id);
                }
            }
        }
        return result;
    }

    public boolean setAllTags() {
        for (int i = 0; i < chTags.length; i++) {
            chTags[i].checked = true;
        }
        return true;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            chTags[(int)buttonView.getTag()].checked = isChecked;
        }
    };
}
