package com.pikkart.trial.teratour;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.migcomponents.migbase64.Base64;
import com.pikkart.ar.recognition.IRecognitionListener;
import com.pikkart.ar.recognition.RecognitionFragment;
import com.pikkart.ar.recognition.RecognitionOptions;
import com.pikkart.ar.recognition.data.CloudRecognitionInfo;
import com.pikkart.ar.recognition.items.Marker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by root on 7/1/17.
 */

public class ImageCloudRecoClass extends AppCompatActivity implements IRecognitionListener, View.OnTouchListener {

    static ARView m_arView;
    public static String markerId = "";
    Marker currentMarker;
    private static File videoFile;
    private ListView menuList;

    public String GetMarkerId(){
        return markerId;
    }

    public static void LoadNewVideo(){
        m_arView.getRenderer().getVideoMesh().UnLoadVideo();
        m_arView.getRenderer().getVideoMesh().LoadVideo(markerId + ".mp4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();

    }

    private void initLayout()
    {



        setContentView(R.layout.activity_main);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //RecognitionFragment btn1 = ((RecognitionFragment) getFragmentManager().findFragmentById(R.id.btn3));






        //create layer to place views
        RelativeLayout cameraTopLayer = new RelativeLayout(this);
        cameraTopLayer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));


        //menu layout


        /*NavigationView drawerLayout = (NavigationView) inflater.inflate(R.layout.menu, null);

        cameraTopLayer.addView(drawerLayout);*/

        /*menuList = drawerLayout.findViewById(R.id.left_drawer);
        String[] testList = {"Profile", "Name"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList);
        menuList.setAdapter(arrayAdapter);*/




        //comment launcher view
        Button commentButton = (Button) inflater.inflate(R.layout.comment_launcher, cameraTopLayer, false);
        cameraTopLayer.addView(commentButton);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), CommentLayout.class));
            }
        });

        //like button view
        Button likeButton = (Button) inflater.inflate(R.layout.like_view, cameraTopLayer, false);
        cameraTopLayer.addView(likeButton);

        //add sliding drawer view
        //RelativeLayout slidingLayout = new RelativeLayout(this);
        //slidingLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //RelativeLayout slideLayout = (RelativeLayout) inflater.inflate(R.layout.sliding_drawer, cameraTopLayer, false);
        //slidingLayout.addView(slideLayout);

        //add slide up launcher
        Button slideUp = (Button)inflater.inflate(R.layout.slideup_layout, cameraTopLayer, false);
        cameraTopLayer.addView(slideUp);

        //add slide up button

        final CardView cardView =  (CardView)inflater.inflate(R.layout.marker_info_layout, cameraTopLayer, false);

        cameraTopLayer.addView(cardView);

        slideUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardView.getVisibility() == View.VISIBLE){
                    cardView.setVisibility(View.INVISIBLE);
                    cardView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));

                }else{

                    cardView.setVisibility(View.VISIBLE);
                    cardView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                }

            }
        });


        //define navigation layout

        NavigationView navigationView = new NavigationView(this);
        navigationView.inflateHeaderView(R.layout.layout2);
        navigationView.inflateMenu(R.menu.nav_menu);


        NavigationView.LayoutParams navParams = new NavigationView.LayoutParams(
                NavigationView.LayoutParams.WRAP_CONTENT,
                NavigationView.LayoutParams.MATCH_PARENT
        );
        cameraTopLayer.addView(navigationView, navParams);


        //define dashboard Button
        Button dashButton = (Button) inflater.inflate(R.layout.profile_launcher, cameraTopLayer, false);
        cameraTopLayer.addView(dashButton);

            //assign dashButton handler
            dashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getBaseContext(), DashBoard.class));
                }
            });


        m_arView = new ARView(this);
        m_arView.setOnTouchListener(this);
        addContentView(m_arView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addContentView(cameraTopLayer, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));


        //assign menu list handler
        /*menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ImageCloudRecoClass.this, "AR App in Progress " + i, Toast.LENGTH_SHORT).show();
            }
        });*/

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        MarkerDetailsDialogFragment markerDetailsDialogFragment = MarkerDetailsDialogFragment.newInstance("");
        markerDetailsDialogFragment.show(fragmentManager, "fragment_2");*/


        RecognitionFragment _cameraFragment = ((RecognitionFragment) getFragmentManager().findFragmentById(R.id.pikkart_ar_fragment));
        _cameraFragment.startRecognition(new RecognitionOptions(
                        RecognitionOptions.RecognitionStorage.GLOBAL,
                        RecognitionOptions.RecognitionMode.TAP_TO_SCAN,
                        new CloudRecognitionInfo(new String[]{"artdatabase_314"})
                ),
                this);

    }

    private void doRecognition()
    {
        RecognitionFragment _cameraFragment = ((RecognitionFragment) getFragmentManager().findFragmentById(R.id.pikkart_ar_fragment));
        _cameraFragment.startRecognition(
                new RecognitionOptions(
                        RecognitionOptions.RecognitionStorage.GLOBAL,
                        RecognitionOptions.RecognitionMode.TAP_TO_SCAN,
                        new CloudRecognitionInfo(new String[]{"artdatabase_314"})
                ),
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecognitionFragment _cameraFragment = ((RecognitionFragment) getFragmentManager().findFragmentById(R.id.pikkart_ar_fragment));

        _cameraFragment.startRecognition(
                new RecognitionOptions(
                        RecognitionOptions.RecognitionStorage.GLOBAL,
                        RecognitionOptions.RecognitionMode.TAP_TO_SCAN,
                        new CloudRecognitionInfo(new String[]{"artdatabase_314"})
                ),
                this);


        if(m_arView!=null) m_arView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(m_arView!=null) m_arView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        doRecognition();
        return false;
    }

    @Override
    public void executingCloudSearch() {
        //Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cloudMarkerNotFound() {

    }

    @Override
    public void internetConnectionNeeded() {

    }

    @Override
    public void markerFound(Marker marker) {

        Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();

        //String customData = currentMarker.getCustomData();


        if(markerId != marker.getId()){
            markerId = marker.getId();

            videoFile = new File(getCacheDir().getPath() + "/" + markerId + ".mp4");
            if (!videoFile.exists()){
                new Base64Convertion().execute();
            }

            if (videoFile.exists()){
                LoadNewVideo();
            }

        }




        //Toast.makeText(this, var1, Toast.LENGTH_SHORT).show();
        //customData = marker.getCustomData();
        //Log.d("customDatamsg", customData);
        //System.out.print(customData);
    }

    @Override
    public void markerNotFound() {

    }

    @Override
    public void markerTrackingLost(String s) {

    }

    @Override
    public void ARLogoFound(String s, int i) {

    }

    @Override
    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private class Base64Convertion extends AsyncTask<String, Void, String>{

        public String encodedString = null;

        FileInputStream inputStream = null;

        public Base64Convertion(){

        }

        public void Mp4ToBase64() {
            //Encode Video To String With mig Base64.

            File tempFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/test.mp4");

            //AssetManager am = context.getResources().getAssets();


            try {
                //AssetManager assetManager = context.getAssets();
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
            encodedString = Base64.encodeToString(bytes, true);

            //ZipOutputStream compressedStringFile = new ZipOutputStream(Environment.getExternalStorageDirectory() + "/" + encodedString);

            /*try {

                FileOutputStream out = new FileOutputStream(
                        Environment.getExternalStorageDirectory() + "/" + markerId + ".txt");
                out.write(encodedString.getBytes());
                out.close();
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("Error", e.toString());

            }*/

            Log.i("Strng", encodedString);

        }
        public void Base64ToMp4(){
            //Decode String To Video With mig Base64.
            byte[] decodedBytes = Base64.decodeFast(encodedString.getBytes());

            try {

                FileOutputStream out = new FileOutputStream(
                        getCacheDir().getPath()
                                + "/" + markerId + ".mp4");
                out.write(decodedBytes);

                out.close();

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("Error", e.toString());

            }
        }


        @Override
        protected String doInBackground(String... customDataParam) {
            //String customData = customDataParam[0];
            Mp4ToBase64();
            Base64ToMp4();


            return null;
        }

        @Override
        protected void onPostExecute(String x) {
            videoFile = new File(getCacheDir().getPath() + "/" + markerId + ".mp4");
            if(videoFile.exists()) {
                LoadNewVideo();
            }
        }
    }
}
