package com.bitrient.mcchymns.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 7/3/15
 */
public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(String name, Context context) {
        Typeface typeface = fontCache.get(name);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + name);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(name, typeface);
        }
        return typeface;
    }
}
