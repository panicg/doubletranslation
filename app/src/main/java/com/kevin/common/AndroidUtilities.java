/*
 * This is the source code of Telegram for Android v. 1.4.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.kevin.common;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class AndroidUtilities {

    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();
    private static int prevOrientation = -10;
    private static boolean waitingForSms = false;
    private static final Object smsLock = new Object();

    public static int statusBarHeight = 0;
    public static int softKeyHeight = 0;
    public static float density = 1;
    public static Point displaySize = new Point();
    public static Integer photoSize = null;
    private static Boolean isTablet = null;

    static {
        density = CommonApplication.Companion.getAppContext().getResources().getDisplayMetrics().density;
        checkSize();
    }

    public static void checkSize() {
        checkDisplaySize();
        checkStatusBarHeight();
    }

    public static void lockOrientation(Activity activity) {
        if (activity == null || prevOrientation != -10) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            WindowManager manager = (WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE);
            if (manager != null && manager.getDefaultDisplay() != null) {
                int rotation = manager.getDefaultDisplay().getRotation();
                int orientation = activity.getResources().getConfiguration().orientation;
                int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
                int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
                if (Build.VERSION.SDK_INT < 9) {
                    SCREEN_ORIENTATION_REVERSE_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    SCREEN_ORIENTATION_REVERSE_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }

                if (rotation == Surface.ROTATION_270) {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                } else if (rotation == Surface.ROTATION_90) {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else if (rotation == Surface.ROTATION_0) {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else {
                        activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (prevOrientation != -10) {
                activity.setRequestedOrientation(prevOrientation);
                prevOrientation = -10;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Typeface getTypeface(String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(CommonApplication.Companion.getAppContext().getAssets(), assetPath);
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return typefaceCache.get(assetPath);
        }
    }

    public static boolean isWaitingForSms() {
        boolean value = false;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }

    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputManager.isActive(view);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static File getCacheDir() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state == null || state.startsWith(Environment.MEDIA_MOUNTED)) {
            try {
                File file = CommonApplication.Companion.getAppContext().getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            File file = CommonApplication.Companion.getAppContext().getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File("");
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static float dpf2(float value) {
        return density * value;
    }

    public static float dpFromResource(int res) {
        return getResources().getDimensionPixelSize(res);
    }


    private static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) CommonApplication.Companion.getAppContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if (Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkStatusBarHeight() {
        try {
            int result = 0;
            int resourceId = CommonApplication.Companion.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = CommonApplication.Companion.getAppContext().getResources().getDimensionPixelSize(resourceId);
            }
            statusBarHeight = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkSoftKeyHeight(Activity act) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DisplayMetrics metrics = new DisplayMetrics();
                act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int usableHeight = metrics.heightPixels;
                act.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                int realHeight = metrics.heightPixels;
                if (realHeight > usableHeight)
                    softKeyHeight = realHeight - usableHeight;
                else
                    softKeyHeight = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long makeBroadcastId(int id) {
        return 0x0000000100000000L | ((long) id & 0x00000000FFFFFFFFL);
    }

    public static int getMyLayerVersion(int layer) {
        return layer & 0xffff;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & 0xffff;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return layer & 0xffff0000 | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return layer & 0x0000ffff | (version << 16);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            CommonApplication.Companion.getApplicationHandler().post(runnable);
        } else {
            CommonApplication.Companion.getApplicationHandler().postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        CommonApplication.Companion.getApplicationHandler().removeCallbacks(runnable);
    }

    public static boolean isTablet() {
//        if (isTablet == null) {
//            isTablet = CommonApplication.applicationContext.getResources().getBoolean(R.bool.isTablet);
//        }
        return isTablet;
    }

    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 700;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = smallSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return smallSide - leftSide;
        } else {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            int leftSide = maxSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            if (Build.VERSION.SDK_INT >= 16) {
                photoSize = 1280;
            } else {
                photoSize = 800;
            }
        }
        return photoSize;
    }

    public static String formatTTLString(int ttl) {
        /*if (ttl < 60) {
            return LocaleController.formatPluralString("Seconds", ttl);
        } else if (ttl < 60 * 60) {
            return LocaleController.formatPluralString("Minutes", ttl / 60);
        } else if (ttl < 60 * 60 * 24) {
            return LocaleController.formatPluralString("Hours", ttl / 60 / 60);
        } else if (ttl < 60 * 60 * 24 * 7) {
            return LocaleController.formatPluralString("Days", ttl / 60 / 60 / 24);
        } else {
            int days = ttl / 60 / 60 / 24;
            if (ttl % 7 == 0) {
                return LocaleController.formatPluralString("Weeks", days / 7);
            } else {
                return String.format("%s %s", LocaleController.formatPluralString("Weeks", days / 7), LocaleController.formatPluralString("Days", days % 7));
            }
        }*/
        return null;
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText == null || Build.VERSION.SDK_INT < 12) {
            return;
        }
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.setInt(editText, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        try {
            Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
                Rect insets = (Rect) mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentActionBarHeight() {
        if (isTablet()) {
            return dp(64);
        } else if (CommonApplication.Companion.getAppContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return dp(48);
        } else {
            return dp(56);
        }
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) CommonApplication.Companion.getAppContext().getSystemService(Context.WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    size.set((Integer) mGetRawW.invoke(windowManager.getDefaultDisplay()), (Integer) mGetRawH.invoke(windowManager.getDefaultDisplay()));
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static void setListViewEdgeEffectColor(AbsListView listView, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = AbsListView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(listView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }

                field = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(listView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        Drawable drawable = null;
        if (view instanceof ListView) {
            drawable = ((ListView) view).getSelector();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
            }
        } else {
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    public static Spannable replaceBold(String str) {
        int start;
        ArrayList<Integer> bolds = new ArrayList();
        while ((start = str.indexOf("<b>")) != -1) {
            int end = str.indexOf("</b>") - 3;
            str = str.replaceFirst("<b>", "").replaceFirst("</b>", "");
            bolds.add(start);
            bolds.add(end);
        }
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(str);
        for (int a = 0; a < bolds.size() / 2; a++) {
            TypefaceSpan span = new TypefaceSpan(String.valueOf(AndroidUtilities.getTypeface("fonts/rmedium.ttf")));
            stringBuilder.setSpan(span, bolds.get(a * 2), bolds.get(a * 2 + 1), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return stringBuilder;
    }

    public static Toast toast = null;

    public static void toast(int res) {
        toast(getString(res));
    }

    public static void toast(final String msg) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(CommonApplication.Companion.getAppContext(), msg, Toast.LENGTH_SHORT);
                TextView tv = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                toast.show();
            }
        });
    }

    public static Locale getLocale() {
        return CommonApplication.Companion.getAppContext().getResources().getConfiguration().locale;
    }

    public static String getLanguage() {
        return getLocale().getLanguage();
    }

    public static String getCountry() {
        return getLocale().getCountry();
    }


    /**
     * 선택된 uri의 사진 Path를 가져온다.
     * uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     *
     * @param uri
     * @return
     */
    public static File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = CommonApplication.Companion.getAppContext().getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return new File(path);
    }

    /**
     * 파일 복사
     *
     * @param srcFile  : 복사할 File
     * @param destFile : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static final String testPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wy_test";

    private static void mkDir(File file) {
        if (!file.exists()) file.mkdirs();
    }

    public static String setSaveTestImagePNG(Bitmap bitmap) {
        String path = null;
        try {
            File f = new File(testPath);
            mkDir(f);
            path = f.getAbsolutePath() + File.separator + "tmp_" + System.currentTimeMillis() + ".png";
            FileOutputStream fo = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fo);
            fo.flush();
            fo.close();
            setMediaScanningToFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String setSaveTestImageJPG(Bitmap bitmap) {
        String path = null;
        try {
            File f = new File(testPath);
            mkDir(f);
            path = f.getAbsolutePath() + File.separator + "tmp_" + System.currentTimeMillis() + ".jpg";
            FileOutputStream fo = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.flush();
            fo.close();
            setMediaScanningToFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String setSaveImage(Bitmap bitmap, int quality) {
        String path = null;
        try {
            File f = new File(CommonApplication.Companion.getAppContext().getFilesDir() + File.pathSeparator + "tmp");
            mkDir(f);
            path = f.getAbsolutePath() + File.separator + "tmp_" + System.currentTimeMillis() + ".png";
            FileOutputStream fo = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fo);
            fo.flush();
            fo.close();
            setMediaScanningToFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    public static boolean setSaveImage(Bitmap bitmap, String path, String fileName) {
        try {
            File f = new File(path);
            mkDir(f);
            FileOutputStream fo = new FileOutputStream(path + File.separator + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fo);
            fo.flush();
            fo.close();
            setMediaScanningToFile(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setSaveImageToAlbum(Bitmap bitmap, String fileName) {
        try {
            final File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    fileName);
            FileOutputStream fo = new FileOutputStream(file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.flush();
            fo.close();
            setMediaScanningToFile(file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String setSaveImageToAlbum(byte[] bytes, String fileName) throws Exception {
        final File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                fileName);
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(file.getAbsolutePath());
            fo.write(bytes);
            fo.flush();
            fo.close();
            setMediaScanningToFile(file.getAbsolutePath());
            return file.getAbsolutePath();
        } finally {
            if (fo != null) {
                fo.close();
            }

        }
    }

    /**
     * Media Scanning
     *
     * @param fullpath
     * @throws Exception
     */
    public static void setMediaScanningToFile(String fullpath) throws Exception {
        new ScanListener(CommonApplication.Companion.getAppContext(), fullpath);
    }

    /**
     * Media Scanning for 'kitkat'
     *
     * @author SU
     */

    static class ScanListener implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private String path;

        public ScanListener(Context context, String path) {
            this.path = path;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(path, null);

        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMs.disconnect();
        }
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getAndroidID() {
        return Settings.Secure.getString(CommonApplication.Companion.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) CommonApplication.Companion.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getIMSI() {
        TelephonyManager telephonyManager = (TelephonyManager) CommonApplication.Companion.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    public static String getMacaddress() {
        WifiManager wifiManager = (WifiManager) CommonApplication.Companion.getAppContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    public interface ADIDCallback {
        void getId(String id);
    }


    public static String getUDID() {

        TelephonyManager tm = (TelephonyManager) CommonApplication.Companion.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(CommonApplication.Companion.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        return deviceUuid.toString();
    }

    /**
     * gms only
     *
     * @param context
     * @return
     */
    public static boolean checkPlayServices(Context context) {

        //TODO 주석한 부분 확인해 주세요
//        try {
//            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//            int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//
//            return resultCode == ConnectionResult.SUCCESS;
//        } catch (Exception e) {
//        }
        return false;
    }

    public static void moveStore() {
        moveStore(CommonApplication.Companion.getAppContext().getPackageName());
    }

    public static void moveStore(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        CommonApplication.Companion.getAppContext().startActivity(intent);
    }

    /**
     * 웹 이동
     *
     * @param url
     */
    public static void moveWeb(String url) {

        if (url == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonApplication.Companion.getAppContext().startActivity(intent);
    }

    /**
     * scheme 앱이동
     * scheme 등록시 잘 움직일 것이다!
     *
     * @param url
     */
    public static void moveApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonApplication.Companion.getAppContext().startActivity(intent);
    }

    /**
     * 웹 이동
     *
     * @param context
     * @param url
     */
    public static void moveWeb(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 앱 실행 및 스토어이동
     *
     * @param packageName
     * @return
     */
    public static void setCheckAppAndMoveStore(String packageName) {
        Intent startLink = CommonApplication.Companion.getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
        if (startLink == null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            CommonApplication.Companion.getAppContext().startActivity(intent);
        } else {
            CommonApplication.Companion.getAppContext().startActivity(startLink);
        }
    }

    /**
     * 앱 실행 및 스토어이동
     *
     * @param context
     * @param packageName
     */
    public static void setCheckAppAndMoveStore(Context context, String packageName) {
        Intent startLink = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (startLink == null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                context.startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                moveWeb(context, "https://play.google.com/store/apps/details?id=" + packageName);
            }
        } else {
            context.startActivity(startLink);
        }
    }

    public static String getFilterdLanguage() {
        Locale locale = CommonApplication.Companion.getAppContext().getResources().getConfiguration().locale;
        if (locale.getLanguage().equals(Locale.KOREAN.getLanguage())) {
            return "ko";
        } else {
            return "en";
        }
    }

    public static String getVersionName() {
        try {
            PackageInfo i = CommonApplication.Companion.getAppContext().getPackageManager().getPackageInfo(CommonApplication.Companion.getAppContext().getPackageName(), 0);
            String currentVersion = i.versionName;

            return currentVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode() {
        try {
            PackageInfo i = CommonApplication.Companion.getAppContext().getPackageManager().getPackageInfo(CommonApplication.Companion.getAppContext().getPackageName(), 0);
            int currentCode = i.versionCode;

            return currentCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Drawable getInstalledIcon(String packageName) {
        PackageManager manager = CommonApplication.Companion.getAppContext().getPackageManager();
        Drawable d = null;
        try {
            d = manager.getApplicationIcon(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static void sendSimpleShareText(Context context, String str, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_TEXT, str);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plan");
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static boolean isCheckNetworkState(Context context) {
        boolean result = true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // boolean isWifiAvail = ni.isAvailable();
        boolean isWifiConn = ni.isConnected();

        boolean isMobileConn = false;
        try {
            ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            // boolean isMobileAvail = ni.isAvailable();
            isMobileConn = ni.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isAirplaneMode = false;
        try {
            isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((isWifiConn == false && isMobileConn == false) || isAirplaneMode) {
            result = false;
        }
        return result;
    }

    public static String firstUpperString(String data) {
        String trans = data.substring(0, 1);
        trans = trans.toUpperCase();
        trans += data.substring(1);
        return trans;
    }

    /**
     * 숫자 3자리씩 끊기
     *
     * @param str
     * @return
     */
    public static String makeStringComma(String str) {
        if (str.length() == 0)
            return "";
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    public static boolean isCheckNetworkState() {
        return isCheckNetworkState(CommonApplication.Companion.getAppContext());
    }

    public static String getString(int res, Object... obj) {
        return CommonApplication.Companion.getAppContext().getResources().getString(res, obj);
    }

    public static String getString(int res) {
        return CommonApplication.Companion.getAppContext().getResources().getString(res);
    }

    public static String[] getStringArray(int res) {
        return CommonApplication.Companion.getAppContext().getResources().getStringArray(res);
    }

    public static Resources getResources() {
        return CommonApplication.Companion.getAppContext().getResources();
    }

    public static int getColor(int resId) {
        try {
            int color = CommonApplication.Companion.getAppContext().getResources().getColor(resId);
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ColorStateList getColorStateList(int resId) {
        try {
            ColorStateList color = CommonApplication.Companion.getAppContext().getResources().getColorStateList(resId);
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRandomColor() {
        int r = new Random().nextInt(255);
        int g = new Random().nextInt(255);
        int b = new Random().nextInt(255);

        return Color.rgb(r, g, b);
    }

    public static Drawable getDrawable(int resId) {
        return CommonApplication.Companion.getAppContext().getDrawable(resId);
    }


    public static String decodeUrlString(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 이메일 정규식
     */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    /**
     * 동적 자릿수
     */

    public static String roundToDecimal(double value, int fractionDigits) {
        double multiplier = Math.pow(10, fractionDigits);
        return BigDecimal.valueOf(Math.round(value * multiplier)).divide(new BigDecimal(multiplier)).toPlainString();
    }

//    //자릿수 조정
//    func roundToDecimal(_ fractionDigits: Int) -> Double {
//        let multiplier = pow(10, Double(fractionDigits))
//        return Darwin.round(self * multiplier) / multiplier
//    }

    /**
     * 이메일 체크
     *
     * @param emailStr
     * @return
     */
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static Date startOfYear(int dayCount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.DAY_OF_YEAR, dayCount);

        return cal.getTime();
    }

    //거리계산
    public static float distance(Location locationA, Location locationB){
        return locationA.distanceTo(locationB);
    }

}
