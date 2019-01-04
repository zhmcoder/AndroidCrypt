package com.dewdrop623.androidcrypt.FilePicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dewdrop623.androidcrypt.MainActivity;
import com.dewdrop623.androidcrypt.R;
import com.dewdrop623.androidcrypt.SettingsHelper;

import java.io.File;

/**
 * Implementation of FilePickerFragment that displays files and folders in a list.
 */

public class ListFilePickerFragment extends FilePickerFragment {
    /*inflates a layout, gets its listView
 * passes the listView and a callback for the adapter to the parent class
  */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_picker, container, false);
        fileListView = (ListView) view.findViewById(R.id.fileListView);
        initializeFilePickerWithViewAndFileListAdapterGetViewCallback(view, fileListAdapterGetViewCallback);
        return view;
    }

    //implementation of FilePickerFragment.FileListAdapterGetViewCallback for IconFilePickerFragment
    private FileListAdapterGetViewCallback fileListAdapterGetViewCallback = new FileListAdapterGetViewCallback() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent, FilePickerFragment.FileListAdapter fileListAdapter) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_listfileviewer_item, parent, false);
            }
            File file = fileListAdapter.getItem(position);
            ImageView listItemImageView = (ImageView) convertView.findViewById(R.id.listItemImageView);
            TextView listItemTextView = (TextView) convertView.findViewById(R.id.listItemTextView);
            if (file.isDirectory()) {
                listItemImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_folder));
            } else {
                listItemImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_file));
            }
            listItemTextView.setText(file.getName());

            /*Change text to match theme*/
            if (SettingsHelper.getUseDarkTeme(getContext())) {
                listItemTextView.setTextColor(((MainActivity)getActivity()).getDarkThemeColor(android.R.attr.textColorPrimary));
            }
            return convertView;
        }
    };
}
