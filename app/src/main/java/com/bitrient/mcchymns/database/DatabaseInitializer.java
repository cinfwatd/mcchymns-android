//package com.bitrient.mcchymns.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.res.Resources;
//import android.text.TextUtils;
//
//import com.bitrient.mcchymns.R;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
///**
// * @author Cinfwat Probity <czprobity@bitrient.com>
// * @since 6/20/15
// */
//public class DatabaseInitializer {
//
//    Context mContext;
//    public DatabaseInitializer(Context context) {
//        mContext = context;
//    }
//
//    private void load(String table) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    doLoading();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }
//
//    private void doLoading() throws IOException {
//        final Resources resources = mContext.getResources();
//        InputStream inputStream = resources.openRawResource(R.raw.hymns);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        mContext.getAssets().
//        try {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] strings = TextUtils.split(line, "-");
//
////                trim()
//                long id = addWord(strings[0].trim(), strings[1].trim());
//            }
//
//        } finally {
//            reader.close();
//        }
//    }
//
//    public long addWord(String word, String definition) {
//        ContentValues values = new ContentValues();
//        database.insert()
//    }
//}
