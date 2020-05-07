package com.livejournal.lofe.lofe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.livejournal.lofe.lofe.model.LofeRecord;

import java.util.ArrayList;
import java.util.List;

public class RecordAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<LofeRecord> mResults;

    public RecordAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<LofeRecord>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public LofeRecord getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.dropdown_record_item, parent, false);
        }
        LofeRecord record = getItem(position);
        ((TextView) convertView.findViewById(R.id.dropdownListItemRecordText)).setText(record.getText());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    //List<LofeRecords> LofeRecords = findRecords(mContext, constraint.toString());
                    // Assign the data to the FilterResults
                    //filterResults.values = LofeRecords;
                    //filterResults.count = LofeRecords.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //mResults = (List<LofeRecords>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<LofeRecord> findRecords(String bookTitle) {
        // GoogleBooksService is a wrapper for the Google Books API
//        GoogleBooksService service = new GoogleBooksService (mContext, MAX_RESULTS);
//        return service.findBooks(bookTitle);
        return null;
    }
}
