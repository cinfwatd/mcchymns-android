package com.bitrient.mcchymns.util;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 7/5/15
 */
public class FontManager {
    public static HashMap<String, String> enumerateFonts(Context context) {
        HashMap<String, String> fonts = new HashMap<>();

        try {
            final String[] fontArr = context.getAssets().list("fonts");

            for (String font : fontArr) {
                fonts.put(font, font.substring(0, font.lastIndexOf(".")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fonts;
    }
}
