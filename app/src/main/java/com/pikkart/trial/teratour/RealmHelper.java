package com.pikkart.trial.teratour;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by root on 8/31/17.
 */

public class RealmHelper {
    Realm realm;

    public RealmHelper(Realm realm){
        this.realm = realm;
    }

    //Write
    public void save(final List<MarkerModel> dashBoardObject){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(dashBoardObject);
            }
        });
    }

    //Read
    public List<MarkerModel> retrieve(){
        List<MarkerModel> dashBoardobjects = new ArrayList<>();
        RealmResults<MarkerModel> markerModels = realm.where(MarkerModel.class).findAll();

        for (MarkerModel objects: markerModels) {
            dashBoardobjects.add(objects);
        }

        return dashBoardobjects;
    }
}
