package zafar.multimediademo.acitivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zafar.multimediademo.R;
import zafar.multimediademo.fragment.HomeFragment;
import zafar.multimediademo.fragment.ProfileFragment;
import zafar.multimediademo.fragment.SearchFragment;
import zafar.multimediademo.model.Detail;
import zafar.multimediademo.model.ProfileData;
import zafar.multimediademo.parser.ParseManager;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.Constants;
import zafar.multimediademo.utility.Utils;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView img_upload_profile;
    private TextView txtUsername;
    private TextView txtEmail;
    private static Bitmap Image = null;
    private static Bitmap rotateImage = null;
    SharedPreferences shre,prefs;
    private View headerView;
    private String profileName,profileEmail;
    private AppDialogLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        shre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String previouslyEncodedImage = shre.getString("image_data", "");
        loader = AppDialogLoader.getLoader(MainActivity.this);
        callProfileApi();
        initUI();

        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            img_upload_profile.setImageBitmap(bitmap);
        }

    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        headerView = navigationView.getHeaderView(0);

        txtUsername = (TextView) headerView.findViewById(R.id.txtUsername);
        txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);

        img_upload_profile = (ImageView)headerView.findViewById(R.id.img_upload_profile);
        img_upload_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        Utils.moveToFragment(this,new ProfileFragment(),null,0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Utils.moveToFragment(this,new ProfileFragment(),null,0);
        } else if (id == R.id.nav_home) {
            Utils.moveToFragment(this,new HomeFragment(),null,0);
        } else if (id == R.id.nav_search) {
            Utils.moveToFragment(this,new SearchFragment(),null,0);
        } else if (id == R.id.nav_signout) {
           signOut();
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "APP NAME (Open it in Google Play Store to Download the Application)");

           // sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_TEXT,"");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else if (id == R.id.img_upload_profile) {
            selectImage();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void signOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit the app? ")
                .setMessage(
                        "Are you sure you want to sign out of your account ?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                /*SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("logged");
                                editor.remove("email");
                                editor.remove("password");
                                editor.commit();*/
                                finish();

                              /*  startActivity(new Intent(
                                        getApplicationContext(),
                                        LoginActivity.class));*/
                            }
                        });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }

    private void selectImage() {
        final Dialog openDialog = new Dialog(this);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.upload_pic_dialog);
        Window window = openDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        openDialog.getWindow().getAttributes().verticalMargin = 0.09F;
        openDialog.getWindow().getAttributes().horizontalMargin = 0.09F;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        openDialog.show();

        final TextView galleryImgTv = (TextView) openDialog.findViewById(R.id.gallery_image);
        final TextView cameraImgTv = (TextView) openDialog.findViewById(R.id.camera_image);

        galleryImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                openDialog.dismiss();

            }
        });

        cameraImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constants.CAPTURE_IMAGE);
                openDialog.dismiss();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == Constants.CAPTURE_IMAGE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            storeImage(thumbnail);
            onCaptureImageResult(data);

        } else if (requestCode == Constants.SELECT_PICTURE) {
            try {
                Uri mImageUri = data.getData();
                Image = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), mImageUri);
                storeImage(Image);
           /*   if (getOrientation(getActivity(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getActivity(), mImageUri));
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix,true);
                  //storeImage(rotateImage);
                    profile_pic.setImageBitmap(rotateImage);
                } else{
                 // storeImage(rotateImage);
                 profile_pic.setImageBitmap(Image);
              }*/

            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(),"Failed to load Image"+e,Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to load Image"+e,Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    public void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        storeImage(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        /*thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profile_pic.setImageBitmap(thumbnail);*/
        //  profile_pic.setBackgroundDrawable(drawable);

    }

    private void storeImage(Bitmap thumbnail) {
        // Removing image saved earlier
        shre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor edit = shre.edit();
        edit.remove("image_data");
        edit.commit();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;

        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img_upload_profile.setImageBitmap(thumbnail);
        byte[] b = bytes.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //saving image to shared preferences
        edit.putString("image_data", encodedImage);
        edit.commit();
    }

    private void callProfileApi() {
        String tag_json_obj = "json_obj_req";

        loader.show();
        final String email = Utils.getLoginEmail(MainActivity.this);
        //String url = "http://activateyourproducts.com/getProfile.php";


        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.PROFILE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.DEBUG(" Register response ------> " + response.toString());
                //msgResponse.setText(response.toString());
                loader.dismiss();
                if (response == null) {
                    return;
                }
                Utils.DEBUG("ProfileResponse : " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);
                    ProfileData data = ParseManager.getInstance().fromJSON(object, ProfileData.class);

                    int success = object.optInt("success");
                    if (success == 1){
                        ArrayList<Detail> profDetail = data.getDetails();

                        for (int i = 0; i < profDetail.size(); i++) {
                            profileName = profDetail.get(0).getName();
                            Utils.DEBUG("Name :"+profileName);
                            profileEmail = profDetail.get(0).getEmail();
                        }
                        txtUsername.setText(profileName);
                        txtEmail.setText(profileEmail);


                    }
                    else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                loader.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                loader.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }
}
