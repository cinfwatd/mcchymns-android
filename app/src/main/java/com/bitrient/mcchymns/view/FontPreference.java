package com.bitrient.mcchymns.view;
/*
 * Copyright (C) 2011-2012 George Yunaev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.util.FontCache;
import com.bitrient.mcchymns.util.FontManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FontPreference extends DialogPreference implements DialogInterface.OnClickListener
{
    // Keeps the font file paths and names in separate arrays
    private List<String> fontPaths;
    private List<String> fontNames;

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public class FontAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return fontNames.size();
        }

        @Override
        public Object getItem(int position)
        {
            return fontNames.get( position );
        }

        @Override
        public long getItemId(int position)
        {
            // We use the position as ID
            return position;
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent )
        {
            View view = convertView;

            // This function may be called in two cases: a new view needs to be created,
            // or an existing view needs to be reused
            if ( view == null )
            {
                // Since we're using the system list for the layout, use the system inflater
                final LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                // And inflate the view android.R.layout.select_dialog_singlechoice
                // Why? See com.android.internal.app.AlertController method createListView()
                view = inflater.inflate( R.layout.my_select_dialog_singlechoice, parent, false);
            }

            if ( view != null )
            {
                // Find the text view from our interface
                CheckedTextView textView = (CheckedTextView) view.findViewById( R.id.my_text1 );

                // Replace the string with the current font name using our typeface
                Typeface typeface = FontCache.get(fontPaths.get(position), getContext());
                textView.setTypeface(typeface);

                // If you want to make the selected item having different foreground or background color,
                // be aware of themes. In some of them your foreground color may be the background color.
                // So we don't mess with anything here and just add the extra stars to have the selected
                // font to stand out.
                textView.setText(fontNames.get(position));
            }

            return view;
        }
    }

    public FontPreference(Context context, AttributeSet attrs )
    {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder( Builder builder )
    {
        super.onPrepareDialogBuilder(builder);

        // Get the fonts on the device
        HashMap< String, String > fonts = FontManager.enumerateFonts(getContext());
        fontPaths = new ArrayList<>();
        fontNames = new ArrayList<>();

        // Get the current value to find the checked item
        String selectedFontPath = getSharedPreferences().getString( getKey(), getContext().getString(R.string.pref_default_font));
        int idx = 0, checked_item = 0;

        for ( String path : fonts.keySet() )
        {
            if ( path.equals( selectedFontPath  ) )
                checked_item = idx;

            fontPaths.add( path );
            fontNames.add( fonts.get(path) );
            idx++;
        }

        // Create out adapter
        // If you're building for API 11 and up, you can pass builder.getContext
        // instead of current context
        FontAdapter adapter = new FontAdapter();

        builder.setSingleChoiceItems( adapter, checked_item, this );

        // The typical interaction for list-based dialogs is to have click-on-an-item dismiss the dialog
        builder.setPositiveButton(null, null);
    }

    public void onClick(DialogInterface dialog, int which)
    {
        if ( which >=0 && which < fontPaths.size() )
        {
            String selectedFontPath = fontPaths.get( which );
            Editor editor = getSharedPreferences().edit();
            editor.putString( getKey(), selectedFontPath );
//            editor.commit();
            editor.apply();

            callChangeListener(selectedFontPath);
            dialog.dismiss();
        }
    }
}