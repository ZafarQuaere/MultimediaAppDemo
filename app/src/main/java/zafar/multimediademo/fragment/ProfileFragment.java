package zafar.multimediademo.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


import zafar.multimediademo.R;
import zafar.multimediademo.model.Detail;
import zafar.multimediademo.model.ProfileData;
import zafar.multimediademo.parser.ParseManager;
import zafar.multimediademo.utility.AppController;
import zafar.multimediademo.utility.AppDialogLoader;
import zafar.multimediademo.utility.Constants;
import zafar.multimediademo.utility.UploadAud;
import zafar.multimediademo.utility.UploadVid;
import zafar.multimediademo.utility.Utils;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by Zafar on 07-01-2017.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final int RequestPermissionCode = 1;
    private static Bitmap Image = null;
    SharedPreferences shre;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "MYAPPAUDIO";
    MediaPlayer mediaPlayer;
    private View view;
    private AppDialogLoader loader;
    private ImageView imgProfile;

    private LinearLayout lytMicroPhone;
    private LinearLayout lytCamera;
    private LinearLayout lytVideo;
    private Button btnRecordAudio;
    private Button btnStopAudio;
    private Button btnPlayAudio;
    private Button btnStopPlayingAudio;
    private Button btnUploadImage;
    private Button btnUploadVideo;
    private Button btnUploadAudio;
    private Uri fileUri;
    private String profileName,profileEmail,profilePhone;
    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtPhoneNumber;
    private TextView txtGetUploaded;
    private String encodedImage;
    private String encodedVideo;
    private String encodedAudio;
    private String videoPath;
    private WebView webViewProifle;
    private String email ;

    String mURL = "";

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = null;
        if (type == MEDIA_TYPE_VIDEO){
            // External sdcard location
            mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.VIDEO_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(Constants.VIDEO_DIRECTORY_NAME, "Oops! Failed create "
                            + Constants.VIDEO_DIRECTORY_NAME + " directory");
                    return null;
                }
            }   
           
        }

        else if (type == MEDIA_TYPE_AUDIO){

            // External sdcard location
            mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.AUDIO_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(Constants.AUDIO_DIRECTORY_NAME, "Oops! Failed create "
                            + Constants.AUDIO_DIRECTORY_NAME + " directory");
                    return null;
                }
            }

        }

        else if (type == MEDIA_TYPE_IMAGE){

            // External sdcard location
            mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGE_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(Constants.IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                            + Constants.IMAGE_DIRECTORY_NAME + " directory");
                    return null;
                }
            }

        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }
        else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }
        else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "AUD_" + timeStamp + ".mp3");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        loader = AppDialogLoader.getLoader(getActivity());

        initUi();
        shre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String previouslyEncodedImage = shre.getString("image_data", "");

         email = Utils.getLoginEmail(getActivity());

        callProfileApi();

        loader.show();
        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imgProfile.setImageBitmap(bitmap);
        }

        mURL = "http://activateyourproducts.com/profile.php?email="+email;

        return view;
    }

    private void initUi() {

        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);

        ImageButton imgBtnMicroPhone = (ImageButton) view.findViewById(R.id.imgBtnMicroPhone);
        ImageButton imgBtnCamera = (ImageButton) view.findViewById(R.id.imgBtnCamera);
        ImageButton imgBtnVideo = (ImageButton) view.findViewById(R.id.imgBtnVideo);

        lytMicroPhone = (LinearLayout) view.findViewById(R.id.lytMicroPhone);
        lytCamera = (LinearLayout) view.findViewById(R.id.lytCamera);
        lytVideo = (LinearLayout) view.findViewById(R.id.lytVideo);

        btnRecordAudio = (Button) view.findViewById(R.id.btnRecordAudio);
        btnStopAudio = (Button) view.findViewById(R.id.btnStopAudio);
        btnPlayAudio = (Button) view.findViewById(R.id.btnPlayAudio);
        btnStopPlayingAudio = (Button) view.findViewById(R.id.btnStopPlayingAudio);

        btnUploadImage = (Button) view.findViewById(R.id.btnUploadImage);
        btnUploadAudio = (Button) view.findViewById(R.id.btnUploadAudio);
        btnUploadVideo = (Button) view.findViewById(R.id.btnUploadVideo);

        Button btnCameraPic = (Button) view.findViewById(R.id.btnCameraPic);
        Button btnGalleryPic = (Button) view.findViewById(R.id.btnGalleryPic);

        Button btnRecordVideo = (Button) view.findViewById(R.id.btnRecordVideo);
        Button btnGalleryVideo = (Button) view.findViewById(R.id.btnGalleryVideo);

        txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
        txtGetUploaded = (TextView) view.findViewById(R.id.txtGetUploaded);

        txtGetUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtGetUploadedImageApi();
            }
        });

        imgProfile.setOnClickListener(this);
        imgBtnMicroPhone.setOnClickListener(this);
        imgBtnCamera.setOnClickListener(this);
        imgBtnVideo.setOnClickListener(this);

        btnRecordAudio.setOnClickListener(this);
        btnStopAudio.setOnClickListener(this);
        btnPlayAudio.setOnClickListener(this);
        btnStopPlayingAudio.setOnClickListener(this);

        btnUploadImage.setOnClickListener(this);
        btnUploadAudio.setOnClickListener(this);
        btnUploadVideo.setOnClickListener(this);

        btnCameraPic.setOnClickListener(this);
        btnGalleryPic.setOnClickListener(this);

        btnRecordVideo.setOnClickListener(this);
        btnGalleryVideo.setOnClickListener(this);

        btnStopAudio.setEnabled(false);
        btnPlayAudio.setEnabled(false);
        btnStopPlayingAudio.setEnabled(false);

        random = new Random();
    }



    private void txtGetUploadedImageApi() {
        String tag_json_obj = "json_obj_req";
        loader.show();
        String url = "http://activateyourproducts.com/getTest.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        loader.dismiss();
                        Utils.DEBUG("download Response "+response);
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), response , Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.toString());
                            int result = jsonObject.optInt("success");
                            if (result == 1) {
                                String stringImg = jsonObject.optString("details");
                                Bitmap bitmap = decodeBitmap(stringImg);
                              //  imgProfile.setImageBitmap(bitmap);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int result = jsonObject.optInt("success");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loader.dismiss();
                        Utils.DEBUG("Volley Error : "+volleyError);
                       }

                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                // params.put("path", encodedImage);

                params.put("id", "51");

                //returning parameters
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgProfile:
                break;
            case R.id.imgBtnMicroPhone:
                lytMicroPhone.setVisibility(lytMicroPhone.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                lytCamera.setVisibility(View.GONE);
                lytVideo.setVisibility(View.GONE);
                break;

            case R.id.imgBtnCamera:
                lytCamera.setVisibility(lytCamera.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                lytMicroPhone.setVisibility(View.GONE);
                lytVideo.setVisibility(View.GONE);
                break;

            case R.id.imgBtnVideo:
                lytVideo.setVisibility(lytVideo.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                lytCamera.setVisibility(View.GONE);
                lytMicroPhone.setVisibility(View.GONE);

                break;

            case R.id.btnRecordAudio:
                recordAudio();
                break;

            case R.id.btnStopAudio:
                stopAudio();
                break;

            case R.id.btnPlayAudio:
                playRecordedAudio();
                break;

            case R.id.btnStopPlayingAudio:
                break;

            case R.id.btnCameraPic:
                captureFromCamera();
                break;
            case R.id.btnGalleryPic:
                selectFromGallery();
                break;

            case R.id.btnRecordVideo:
                recordVideo();

            case R.id.btnGalleryVideo:
                selectVideo();
                break;

            case R.id.btnUploadImage:
                uploadImage(Constants.UPLOAD_IMAGE_TYPE);
                break;

            case R.id.btnUploadAudio:
                //uploadAudio(AudioSavePathInDevice);
                uploadImage(Constants.UPLOAD_AUDIO_TYPE);
                break;

            case R.id.btnUploadVideo:
                uploadImage(Constants.UPLOAD_VIDE0_TYPE);
                //uploadVideo();
                break;
        }
    }

    private void uploadAudio(final String audioSavePathInDevice) {

        class UploadTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
                loader.show();
            }

            @Override
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);
                loader.dismiss();
                Utils.DEBUG("upload audio response "+msg);

            }

            @Override
            protected String doInBackground(Void... params) {
                UploadAud audio = new UploadAud();
                String msg = audio.uploadAudio(audioSavePathInDevice);

                return msg;
            }
        }
        UploadTask uv = new UploadTask();
        uv.execute();
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
                loader.show();
            }

            @Override
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);
                loader.dismiss();
                Utils.DEBUG("upload video response "+msg);

            }

            @Override
            protected String doInBackground(Void... params) {
                UploadVid u = new UploadVid();
                String msg = u.uploadVideo(videoPath);

                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();


    }

    private void uploadImage(final String uploadType) {
        String tag_json_obj = "json_obj_req";
        loader.show();
        String url = "";
       if (uploadType.equalsIgnoreCase("Image")){
            url = "http://activateyourproducts.com/uploadFile.php";
           Utils.DEBUG("Url called :"+url);
        }
        else if (uploadType.equalsIgnoreCase("Audio")){
           url = "http://activateyourproducts.com/uploadAudio.php";
           Utils.DEBUG("Url called :"+url);
       }
       else if (uploadType.equalsIgnoreCase("Video")){
           url = "http://activateyourproducts.com/uploadVideo.php";
           Utils.DEBUG("Url called :"+url);
       }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        loader.dismiss();
                        Utils.DEBUG("Upload Response "+response);
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), response , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loader.dismiss();
                        Utils.DEBUG("Volley Error : "+volleyError);
                        //Showing toast
                       // Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }

                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
               // params.put("path", encodedImage);
                if (uploadType.equalsIgnoreCase("Image")){
                    params.put("path", encodedImage);
                }
                else if (uploadType.equalsIgnoreCase("Video")) {
                    params.put("path", encodedVideo);
                }
                else if (uploadType.equalsIgnoreCase("Audio")){
                    params.put("path",encodedAudio);
                }
                params.put("email", profileEmail);
                params.put("name", profileName);
                params.put("type", uploadType);

               // System.out.println("path "+encodedImage+" email "+profileEmail+" name "+profileName+" type "+uploadType);
                //returning parameters
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }

    private void selectVideo() {

        Intent intent = new Intent();

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), Constants.REQUEST_TAKE_GALLERY_VIDEO);

    }

    private void recordVideo() {

        /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // start the video capture Intent
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);*/

        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/myvideo.mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFile);
       // Uri videoUri = Uri.fromFile(mediaFile);

       // intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("DetailFragment", "onActivityCreated()");
        if (savedInstanceState != null) {
            mURL = savedInstanceState.getString("currentURL", "");
        }
        if(!mURL.trim().equalsIgnoreCase("")){
            webViewProifle = (WebView) getView().findViewById(R.id.webViewProifle);
            webViewProifle.getSettings().setJavaScriptEnabled(true);
            webViewProifle.setWebViewClient(new MyWebViewClient());
            webViewProifle.loadUrl(mURL.trim());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentURL", mURL);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.DEBUG("OnActivityResult called");

        if (requestCode == Constants.CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Utils.DEBUG("Inside Capture Image >>>>>>>>>>>>>>");
                Image = (Bitmap) data.getExtras().get("data");
                encodedImage = getStringImage(Image);
                Utils.DEBUG("Image path >>>>>>>" + encodedImage);

                Bitmap bitmap = decodeBitmap(encodedImage);
              //  imgProfile.setImageBitmap(bitmap);
            }
            else {
                Utils.showToast(getActivity(), "Sorry! Failed to load Image");
            }
            //onCaptureImageResult(data);
        } else if (requestCode == Constants.SELECT_PICTURE  ) {
            if (resultCode == RESULT_OK) {
                try {
                    Utils.DEBUG("Inside Select from gallery Image >>>>>>>>>>>>>>");
                    Uri filePath = data.getData();
                    Image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    encodedImage = getStringImage(Image);
                    // imgUpload.setImageBitmap(Image);
                    Utils.DEBUG("Image path >>>>>>>" + encodedImage);
                    Bitmap bitmap = decodeBitmap(encodedImage);
                  //  imgProfile.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Utils.showToast(getActivity(), "Failed to load Image" + e);
                } catch (IOException e) {
                    Utils.showToast(getActivity(), "Failed to load Image" + e);
                    e.printStackTrace();
                }
            }
            else {
                Utils.showToast(getActivity(), "Sorry! Failed to load Image");
            }
        } else if (requestCode == Constants.REQUEST_TAKE_GALLERY_VIDEO) {
            if (resultCode == RESULT_OK) {
                Utils.DEBUG("Inside Select from gallery Video >>>>>>>>>>>>>>");

                Uri selectedImageUri = data.getData();
                 videoPath = getVideoPath(selectedImageUri);
                Utils.DEBUG("Selected Video Path : "+videoPath);

                //encodedVideo = getStringVideo(videoPath);
                encodedVideo = Utils.getBase64EncodedVideo(videoPath);

                System.out.println("Encoded Video :"+encodedVideo);

                // previewVideo("Gallery");
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Utils.showToast(getActivity(), "User cancelled ");
            } else {
                // failed to record video
                Utils.showToast(getActivity(),
                        "Sorry! Failed to record video");
            }
        } else if (requestCode == Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Utils.DEBUG("Inside Record Video >>>>>>>>>>>>>>");
                // video successfully recorded
                Uri selectedImageUri = data.getData();
                videoPath = getVideoPath(selectedImageUri);
               // encodedVideo = getStringVideo(videoPath);
                Utils.DEBUG("Captured Video Path : "+videoPath);

                encodedVideo = Utils.getBase64EncodedVideo(videoPath);

                System.out.println("Encoded Video :"+encodedVideo);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Utils.showToast(getActivity(), "User cancelled video recording");
            } else {
                // failed to record video
                Utils.showToast(getActivity(), "Sorry! Failed to record video");
            }
        }


    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        InputStream is = new ByteArrayInputStream(decodedString);

//        Bitmap mBitmap = BitmapFactory.decodeStream(new FlushedInputStream(is));
       // imageView.setImageBitmap(b);
       /* byte[] decodedString = Base64.decode(encodedImage, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/

        return decodedByte;
    }



    private String getVideoPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    private void captureFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.CAPTURE_IMAGE);
    }

    private void selectFromGallery() {
        //Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), Constants.SELECT_PICTURE);
    }

    private void playRecordedAudio() {

        btnStopAudio.setEnabled(false);
        btnRecordAudio.setEnabled(false);
        btnStopPlayingAudio.setEnabled(true);

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

        Toast.makeText(getActivity(), "Recording Playing", Toast.LENGTH_LONG).show();
    }

    private void stopAudio() {
        mediaRecorder.stop();

        btnStopAudio.setEnabled(false);
        btnPlayAudio.setEnabled(true);
        btnRecordAudio.setEnabled(true);
        btnStopPlayingAudio.setEnabled(false);
        btnUploadAudio.setEnabled(true);

        Utils.DEBUG("upload audio path..>>>>>>>>:"+AudioSavePathInDevice);
        Utils.showToast(getActivity(), "Recording Completed");
        Utils.showToast(getActivity(), "Recording path :"+AudioSavePathInDevice);

        encodedAudio = Utils.getBase64EncodedAudio(AudioSavePathInDevice);

      System.out.println("encodedAudio~~~~~~~~ Encoded: "+ encodedAudio);
    }

    private void recordAudio() {

        Utils.showToast(getActivity(), "Audio record clicked");
        if (Utils.checkPermission(getActivity())) {
           // AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constants.AUDIO_DIRECTORY_NAME + "Recording.mp3";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            btnRecordAudio.setEnabled(false);
            btnStopAudio.setEnabled(true);
            btnUploadAudio.setEnabled(false);

            Utils.showToast(getActivity(), "Recording started");
        } else {

            requestPermission();

        }

    }

    public void MediaRecorderReady() {

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);

    }



    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Utils.showToast(getActivity(), "Permission Granted");
                    } else {
                        Utils.showToast(getActivity(), "Permission Denied");

                    }
                }

                break;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void callProfileApi() {
        String tag_json_obj = "json_obj_req";

        loader.show();

        String url = "http://activateyourproducts.com/getProfile.php";


        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.DEBUG(" Profile response ------> " + response.toString());
                //msgResponse.setText(response.toString());
                loader.dismiss();
                if (response == null) {
                    return;
                }
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
                            profilePhone = profDetail.get(0).getPhone();

                        }
                        txtUsername.setText(profileName);
                        txtEmail.setText(profileEmail);
                        txtPhoneNumber.setText(profilePhone);

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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loader.dismiss();
        }
    }
}
