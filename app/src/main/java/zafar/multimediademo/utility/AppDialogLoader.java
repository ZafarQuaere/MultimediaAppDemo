package zafar.multimediademo.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import zafar.multimediademo.R;


/**
 * base class to use loader in the application
 * @author amit
 */
public class AppDialogLoader
{
    private static AppDialogLoader loader = null;
    private static AppDialogLoader previousFragmentLoader = null;
    private static Context con;
    private ProgressDialog pDialog;

    public AppDialogLoader(Context context)
    {
        pDialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
       // pDialog.setMessage("Loading...");

        pDialog.setCancelable(false);
    }



    public static AppDialogLoader getLoader(Context context)
    {
        con = context;

        //if(loader == null)
        {
            loader = new AppDialogLoader(context);
            previousFragmentLoader = loader;
        }

        return loader;
    }


    public boolean CheckLoaderStatus(){

        if(pDialog.isShowing() || previousFragmentLoader !=null){
            return true;
        }else {
            return false;
        }
    }

    public void show()
    {
        Utils.DEBUG("AppDialogLoader >> show()");
        if(!pDialog.isShowing() && !((Activity) con).isFinishing())//use this condition to check, for any reason if the activity is destroyed then it will prevent from crash
        {
           pDialog.show();
        }
    }

    public void dismiss()
    {
        Utils.DEBUG("AppDialogLoader >> dismiss() > " + pDialog);
        if(pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }

    public void hide()
    {
        Utils.DEBUG("AppDialogLoader >> hide() > " + pDialog);
        if(pDialog.isShowing())
        {
            pDialog.hide();
        }
    }


}
