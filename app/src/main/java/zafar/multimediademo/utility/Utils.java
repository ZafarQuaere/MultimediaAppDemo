package zafar.multimediademo.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


import zafar.multimediademo.R;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by zafar.imam on 27-12-2016.
 */

public class Utils {

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

    }

    public static void DEBUG(String sb) {
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    Log.d("Parent >> ", "Parent >> " + sb.substring(4000 * i));
                } else {
                    Log.d("Parent >> ", "Parent >> " + sb.substring(4000 * i, max));
                }
            }
        } else {
            Log.d("OT >> ", "OT >> " + sb.toString());
        }
    }


    public static void setFirstTimeLaunch(Activity activity, boolean isFirstTime) {
        if (activity == null)
            return;
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        sp.put(activity.getString(R.string.first_time_launch), isFirstTime);

    }

    public static boolean isFirstTimeLaunch(Activity activity) {

        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        Object object = sp.get(activity.getString(R.string.first_time_launch));

        return (object == null ? false : true);
    }

    public static boolean isValidEmail(String email) {

        /* return Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();*/

        boolean isValidate = false;

        if (email != null && email.length() - 1 > 2) {
            if (email.charAt(email.length() - 2) == '-') {
                return false;
            }
            if (email.charAt(email.length() - 2) == '_') {
                return false;
            }
            if (Character.isDigit(email.charAt(email.length() - 2))) {
                return false;
            }
        }
        String regex = "^[A-Za-z0-9][\\.A-Za-z0-9_-]*\\@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)?(\\.[_A-Za-z0-9-])?([A-Za-z]{1,50})$";
        // lis-b.eth.sagen

        Pattern pattern = Pattern.compile(regex);
        String targetString = email;
        Matcher matcher = pattern.matcher(targetString);
        if (matcher.find()) {
            isValidate = true;
            // System.out.println("True");
        } else {
            // System.out.println("False");
        }

        return isValidate;
    }


    public static boolean isInternetConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static void showPopUp(Context context, String info, final DialogInterface okButton) {

        if (context == null)
            return;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Info !");
        alert.setCancelable(false);
        alert.setMessage(info);
        alert.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {

                // TODO Auto-generated method stub
                // mContext.finish();
                // ((Activity)mContext).finish();
                if (okButton != null) {
                    okButton.onClickOk();
                }
            }
        });

        alert.show();
    }

    /**
     * used to decompress given gzip to string
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] str) throws IOException, UnsupportedEncodingException {
        if (str == null || str.length == 0) {
            return str;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }
        return outStr.getBytes();
    }

    /**
     * used to print givne message in error mode
     *
     * @param message
     */
    public static void ERROR(String message) {
        Log.e("OT >> ", "OT >> " + message);
    }

    public static void moveToFragment(Activity activity, Fragment fragment, Object data, int viewType) {
        Utils.DEBUG("moveToFragment() called: " + fragment);
        if (activity == null || fragment == null) {
            return;
        }

        android.support.v4.app.FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.lytMain, fragment, fragment.getClass().getName());

        Bundle bundle = new Bundle();


        if (data != null/* && (fragment.getClass().getSimpleName().equals(new FlightPassSearchFragment().getClass().getSimpleName())
                                || fragment.getClass().getSimpleName().equals(new FlightPassSearchSelectFragment().getClass().getSimpleName()))*/) {
            bundle.putSerializable(activity.getString(R.string.key_serializable), (Serializable) data);
        }


        fragment.setArguments(bundle);

        transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();


    }

    public static String getBase64EncodedVideo(String videoPath) {
        //File tempFile = new File(Environment.getExternalStorageDirectory() + "/my/part/my_0.mp4");
        File tempFile = new File(videoPath);
        String encodedString = null;

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(tempFile);
        } catch (Exception e) {
            // TODO: handle exception
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.i("Strng", encodedString);
        return  encodedString;
    }

    public static String getBase64EncodedAudio(String selectedPath) {
        byte[] audioBytes;
        String encodedAudio = "";
        try {

            // Just to check file size.. Its is correct i-e; Not Zero
            File audioFile = new File(selectedPath);
            long fileSize = audioFile.length();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(selectedPath));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();

            // Here goes the Base64 string
            encodedAudio = Base64.encodeToString(audioBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Utils.DEBUG("Audio Encoding Exception :" +e);
        }

        return encodedAudio;
    }


    public static interface DialogInterface {
        public void onClickOk();
    }

    public static boolean checkPermission(Context context) {

        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


   /* public static boolean isUserLoggedIn(Activity activity) {
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        Object object = sp.get(activity.getString(R.string.key_json_loginData));

        return (object == null ? false : true);
    }*/

    public static String getLoginEmail(Activity activity) {
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        return (String) sp.get(activity.getString(R.string.key_login_username));
    }

    /**
     * used to get username
     *
     * @param activity
     * @param username
     */
    public static void setLoginEmail(Activity activity, String username) {
        if(activity == null)
            return;
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        sp.put(activity.getString(R.string.key_login_username), username);
    }

    /**
     * used to set password
     *
     * @param activity
     * @return
     */
    public static String getLoginPassword(Activity activity) {
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        return (String) sp.get(activity.getString(R.string.key_login_password));
    }

    /**
     * used to get password
     *
     * @param activity
     * @param password
     */
    public static void setLoginPassword(Activity activity, String password) {
        if(activity == null)
            return;
        AppSharedPrefs sp = AppSharedPrefs.getInstance(activity);
        sp.put(activity.getString(R.string.key_login_password), password);
    }

  /*  public static String getBase64EncodedVideo(String videoPath) {

        String videoData = "";
        try {
            @SuppressWarnings("resource")
            FileInputStream v_input = new FileInputStream(videoPath);
            ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
            byte[] byteBufferString = new byte[8192];
            for (int readNum; (readNum = v_input.read(byteBufferString)) != -1;)
            {
                objByteArrayOS.write(byteBufferString, 0, readNum);
                System.out.println("read " + readNum + " bytes,");
            }

            videoData = encodeBytes(byteBufferString);//Base64.encodeToString(objByteArrayOS.toByteArray(), Base64.DEFAULT);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return videoData;
    }

    public static String encodeBytes( byte[] source ) {
        // Since we're not going to have the GZIP encoding turned on,
        // we're not going to have an java.io.IOException thrown, so
        // we should not force the user to have to catch it.
        String encoded = null;
        try {
            encoded = encodeBytes(source, 0, source.length);
        } catch (java.io.IOException ex) {
            assert false : ex.getMessage();
        }   // end catch
        assert encoded != null;
        return encoded;
    }


    public static String encodeBytes( byte[] source, int off, int len ) throws java.io.IOException {
        byte[] encoded = encodeBytesToBytes(source, off, len);

        // Return value according to relevant encoding.
        try {
            return new String(encoded, "US-ASCII");
        }   // end try
        catch (java.io.UnsupportedEncodingException uue) {
            return new String(encoded);
        }   // end catch

    }*/
}
