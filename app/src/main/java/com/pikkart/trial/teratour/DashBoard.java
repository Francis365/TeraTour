package com.pikkart.trial.teratour;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
/*
import com.pikkart.trial.R;*/

public class DashBoard extends AppCompatActivity {

    Realm realm;
    private List<MarkerModel> objectList;
    File[] listFile;
    File file;
    String filePathStrings[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        //get realm instance
        realm = Realm.getDefaultInstance();

        //get realm helper class object
        RealmHelper realmHelper = new RealmHelper(realm);



        if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/dbTest") != null){
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/dbTest");
            Snackbar.make(findViewById(R.id.recycler_view), "path does not, or No SD Card", Snackbar.LENGTH_SHORT).show();

        }else{
            Snackbar.make(findViewById(R.id.recycler_view), "path does not, or No SD Card", Snackbar.LENGTH_SHORT).show();
        }

        if(file.isDirectory()) {
            listFile = file.listFiles();
            filePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++){
                filePathStrings[i] = listFile[i].getAbsolutePath();

                //save to Ream database
                realmHelper.save(MarkerModel.getObjectList(filePathStrings[i], i));
            }
        }







        //retrieve from Ream database
        objectList = realmHelper.retrieve();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        MyAdapter adapter = new MyAdapter(this, objectList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


}
