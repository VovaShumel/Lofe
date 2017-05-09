package com.livejournal.lofe.lofe;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;

public class TagsAdapter extends SimpleCursorAdapter {
//public class TagsAdapter extends BaseAdapter {

    //Context ctx;
    //LayoutInflater lInflater;
    protected ChTag[] chTags;
    //Cursor c;

//    ArrayList<Product> objects;


    public TagsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        //ctx = context;
        //lInflater = (LayoutInflater) ctx
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        super.setViewBinder(new MyViewBinder());
    }

//    TagsAdapter(Context context) {
//        ctx = context;
//        //objects = products;
//        lInflater = (LayoutInflater) ctx
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }




//    BoxAdapter(Context context, ArrayList<Product> products) {
//        ctx = context;
//        objects = products;
//        lInflater = (LayoutInflater) ctx
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

//    // кол-во элементов
//    @Override
//    public int getCount() {
//        return objects.size();
//    }
//
//    // элемент по позиции
//    @Override
//    public Object getItem(int position) {
//        return objects.get(position);
//    }
//
//    // id по позиции
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View v, Cursor c, int from) {

            if (v.getId() == R.id.cbTag2Checked) {
                //v.setOnCheckedChangeListener(myCheckChangList);
                ((CheckBox)v).setOnCheckedChangeListener(myCheckChangList);

                //CheckBox chb =
                // пишем позицию
                //cbBuy.setTag(position);
                // заполняем данными из товаров: в корзине или нет
                //((CheckBox)v).setChecked(p.box);
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
                chTags[i] = new ChTag(false, c.getLong(c.getColumnIndex(DB.TAG_COLUMN_ID)));
                c.moveToNext();
            }
        }

        //chTags = new boolean[c.getCount()];
        return super.swapCursor(c);
    }

//    // пункт списка
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // используем созданные, но не используемые view
//        View view = convertView;
//        if (view == null) {
//            view = lInflater.inflate(R.layout.tag2, parent, false);
//        }
//
//        Product p = getProduct(position);
//
//        // заполняем View в пункте списка данными из товаров: наименование, цена
//        // и картинка
//        ((TextView) view.findViewById(R.id.tvTag2Text)).setText(p.name);
//        //((TextView) view.findViewById(R.id.tvPrice)).setText(p.price + "");
//        //((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
//
//        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
//        // присваиваем чекбоксу обработчик
//        cbBuy.setOnCheckedChangeListener(myCheckChangList);
//        // пишем позицию
//        cbBuy.setTag(position);
//        // заполняем данными из товаров: в корзине или нет
//        cbBuy.setChecked(p.box);
//        return view;
//    }



//    // товар по позиции
//    Product getProduct(int position) {
//        return ((Product) getItem(position));
//    }

    // содержимое корзины
//    ArrayList<Product> getBox() {
//        ArrayList<Product> box = new ArrayList<Product>();
//        for (Product p : objects) {
//            // если в корзине
//            if (p.box)
//                box.add(p);
//        }
//        return box;
//    }

    public ArrayList<Integer> getCheckedTags() {
        //ArrayList result = new ArrayList<Integer>();
        ArrayList result = new ArrayList<Long>();
        if (chTags != null) {
            for (int i = 0; i < chTags.length; i++) {
                if (chTags[i].checked) {
                    //result.add((int)chTags[i].id);
                    result.add(chTags[i].id);
                }
            }
        }
        return result;
    }

//    long[] getCheckedTags() {
//        long[] result = new long[0];
//        if (chTags != null) {
//            result = new long[chTags.length];
//
//            for (int i = 0; i < chTags.length; i ++) {
//                result[i] = chTags[i].id;
//            }
//        }
//        return result;
//    }


    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            MyLog.d("Сейчас бит чекед = " + chTags[(int)buttonView.getTag()].checked);

            chTags[(int)buttonView.getTag()].checked = isChecked;
        }
    };
}
