package com.pikkart.trial.teratour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 8/28/17.
 */

public class MarkerModel extends RealmObject {

    @PrimaryKey
    private String markerID;

    private byte[] imageByteArray;
    private String imageName;
    private String imageString;
    private String commentCount;
    private String likes;
    private String shares;

    public String getMarkerID() {
        return markerID;
    }

    public void setMarkerID(String markerID) {
        this.markerID = markerID;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(String imageByteArray) {
        Bitmap bmp = BitmapFactory.decodeFile(imageByteArray);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        this.imageByteArray = stream.toByteArray();
    }

    public String getImageName(){return imageName; }
    public void setImageName(String imageName){this.imageName = imageName; }

    public String getImageString(){return imageString; }
    public void setImageString(String imageString){this.imageString = imageString; }

    public String getCommentCount(){return commentCount; }
    public void setCommentCount(String commentCount){this.commentCount = commentCount; }

    public String getLikes(){return likes; }
    public void setLikes(String likes){this.likes = likes; }

    public String getShares(){return shares; }
    public void setShares(String shares){this.shares = shares; }

    public static List<MarkerModel> getObjectList(String imgPath, int position){



        List<MarkerModel> dataList = new ArrayList<>();
        MarkerModel markerObject = new MarkerModel();

        markerObject.setMarkerID(String.valueOf(position));
        markerObject.setImageByteArray(imgPath);

        dataList.add(markerObject);

        return dataList;
    }

    private static byte[] bitmapToByte(Bitmap bitmap){                        //not in use now
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }



}
