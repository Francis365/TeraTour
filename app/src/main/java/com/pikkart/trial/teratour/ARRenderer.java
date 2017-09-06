/* ===============================================================================
 * Copyright (c) 2016 Pikkart S.r.l. All Rights Reserved.
 * Pikkart is a trademark of Pikkart S.r.l., registered in Europe,
 * the United States and other countries.
 *
 * This file is part of Pikkart AR SDK Tutorial series, a series of tutorials
 * explaining how to use and fully exploits Pikkart's AR SDK.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============================================================================*/
package com.pikkart.trial.teratour;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.pikkart.ar.recognition.RecognitionFragment;
import com.pikkart.ar.recognition.items.Marker;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * \class ARRenderer
 * \brief Our ARView renderer
 *
 * This class implements the GLTextureView.Renderer interface and its used by our OpenGL View
 * ( ARView ) to renderer our 3d objects using OpenGL ES 2
 */
public class ARRenderer implements GLTextureView.Renderer
{
    public boolean IsActive = false; /**< is active (and rendering stuff) */

    private int ViewportWidth; /**< viewport true screen size */
    private int ViewportHeight; /**< viewport true screen height */
    private int Angle; /**< screen orientation, with 0 being landscape and 90 portrait */
    private Context context; /**< the parent context */

    //private Mesh monkeyMesh = null; /**< the 3d object we want to renderer */
    //private Mesh blue_monkeyMesh = null; /**< the 3d object we want to renderer */

    public VideoMesh videoMesh = null;

    ImageCloudRecoClass imageInfo = null;
    /**
     * \brief Constructor
     * @param con the parent ocntext.
     */
    public ARRenderer()
    {

    }


    public ARRenderer(Context con)
    {

        context = con;
        imageInfo = new ImageCloudRecoClass();
    }

    /**
     * \brief Called when the surface is created or recreated.
     * @param gl
     * @param config the new opengl configuration
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //we create our content here
        //monkeyMesh=new Mesh();
        //monkeyMesh.InitMesh(context.getAssets(),"media/monkey.json", "media/texture.png");

        //blue_monkeyMesh=new Mesh();
        //blue_monkeyMesh.InitMesh(context.getAssets(),"media/monkey.json", "media/texture2.png");

        videoMesh = new VideoMesh((Activity)context);

        videoMesh.InitMesh(context.getApplicationContext().getAssets(), "", "", 0, true, null);

        if(imageInfo.GetMarkerId() != "") {
            videoMesh.UnLoadVideo();
            videoMesh.LoadVideo(imageInfo.GetMarkerId() + ".mp4");
        }

    }

    /**
     * \brief Called when the surface changed size.
     * @param gl
     * @param width the new surface width
     * @param height the new surface height
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {

    }

    /**
     * \brief Called when the surface is destroyed.
     */
    public void onSurfaceDestroyed()
    {

    }

    /**
     * \brief Compute the correct model-view-projection matrix.
     *
     * Compute the correct model-view-projection matrix, considering video aspect ratio,
     * screen orientation and tracked marker
     * @param mvpMatrix where to store the computed matrix
     * @return true if tracking something
     */
    public boolean computeProjectionMatrix(float[] pMatrix) {
        RenderUtils.matrix44Identity(pMatrix);
        float w = (float)640;
        float h = (float)480;

        float ar = (float)ViewportHeight / (float)ViewportWidth;
        if (ViewportHeight > ViewportWidth) ar = 1.0f / ar;
        float h1 = h, w1 = w;
        if (ar < h/w)
            h1 = w * ar;
        else
            w1 = h / ar;

        float a = 0.f, b = 0.f;
        switch (Angle) {
            case 0: a = 1.f; b = 0.f;
                break;
            case 90: a = 0.f; b = 1.f;
                break;
            case 180: a = -1.f; b = 0.f;
                break;
            case 270: a = 0.f; b = -1.f;
                break;
            default: break;
        }

        float[] angleMatrix = new float[16];

        angleMatrix[0] = a; angleMatrix[1] = b; angleMatrix[2]=0.0f; angleMatrix[3] = 0.0f;
        angleMatrix[4] = -b; angleMatrix[5] = a; angleMatrix[6] = 0.0f; angleMatrix[7] = 0.0f;
        angleMatrix[8] = 0.0f; angleMatrix[9] = 0.0f; angleMatrix[10] = 1.0f; angleMatrix[11] = 0.0f;
        angleMatrix[12] = 0.0f; angleMatrix[13] = 0.0f; angleMatrix[14] = 0.0f; angleMatrix[15] = 1.0f;

        float[] projectionMatrix = RecognitionFragment.getCurrentProjectionMatrix().clone();
        projectionMatrix[5] = projectionMatrix[5] * (h / h1);

        RenderUtils.matrixMultiply(4,4,angleMatrix,4,4,projectionMatrix,pMatrix);

        return true;
    }

    public boolean computeModelViewProjectionMatrix(float[] mvMatrix, float[] pMatrix) {
        RenderUtils.matrix44Identity(mvMatrix);
        RenderUtils.matrix44Identity(pMatrix);

        float w = (float)640;
        float h = (float)480;
        float ar = (float)ViewportHeight / (float)ViewportWidth;
        if (ViewportHeight > ViewportWidth) ar = 1.0f / ar;
        float h1 = h, w1 = w;
        if (ar < h/w)
            h1 = w * ar;
        else
            w1 = h / ar;

        float a = 0.f, b = 0.f;
        switch (Angle) {
            case 0: a = 1.f; b = 0.f;
                break;
            case 90: a = 0.f; b = 1.f;
                break;
            case 180: a = -1.f; b = 0.f;
                break;
            case 270: a = 0.f; b = -1.f;
                break;
            default: break;
        }
        float[] angleMatrix = new float[16];
        angleMatrix[0] = a; angleMatrix[1] = b; angleMatrix[2]=0.0f; angleMatrix[3] = 0.0f;
        angleMatrix[4] = -b; angleMatrix[5] = a; angleMatrix[6] = 0.0f; angleMatrix[7] = 0.0f;
        angleMatrix[8] = 0.0f; angleMatrix[9] = 0.0f; angleMatrix[10] = 1.0f; angleMatrix[11] = 0.0f;
        angleMatrix[12] = 0.0f; angleMatrix[13] = 0.0f; angleMatrix[14] = 0.0f; angleMatrix[15] = 1.0f;

        float[] projectionMatrix = RecognitionFragment.getCurrentProjectionMatrix().clone();
        projectionMatrix[5] = projectionMatrix[5] * (h / h1);

        RenderUtils.matrixMultiply(4,4,angleMatrix,4,4,projectionMatrix,pMatrix);

        if( RecognitionFragment.isTracking() ) {
            float[] tMatrix = RecognitionFragment.getCurrentModelViewMatrix();
            mvMatrix[0]=tMatrix[0]; mvMatrix[1]=tMatrix[1]; mvMatrix[2]=tMatrix[2]; mvMatrix[3]=tMatrix[3];
            mvMatrix[4]=tMatrix[4]; mvMatrix[5]=tMatrix[5]; mvMatrix[6]=tMatrix[6]; mvMatrix[7]=tMatrix[7];
            mvMatrix[8]=tMatrix[8]; mvMatrix[9]=tMatrix[9]; mvMatrix[10]=tMatrix[10]; mvMatrix[11]=tMatrix[11];
            mvMatrix[12]=tMatrix[12]; mvMatrix[13]=tMatrix[13]; mvMatrix[14]=tMatrix[14]; mvMatrix[15]=tMatrix[15];
            return true;
        }
        return false;
    }


    /**
     * \brief Called to draw the current frame
     * @param gl
     */
    /** Called to draw the current frame. */
    public void onDrawFrame(GL10 gl) {
        if (!IsActive) return;


        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Call our native function to render camera content
        RecognitionFragment.renderCamera(ViewportWidth, ViewportHeight, Angle);

        if(RecognitionFragment.isTracking()) {

            //Here we decide which 3d object to draw and we draw it
            float[] mvMatrix = new float[16];
            float[] pMatrix = new float[16];
            if (computeModelViewProjectionMatrix(mvMatrix, pMatrix)) {

                //Render Video Only if the video is loaded
                videoMesh.DrawMesh(mvMatrix, pMatrix);
                RenderUtils.checkGLError("completed video mesh Render");

            }
        }

        if(RecognitionFragment.isTracking() && !videoMesh.isPlaying()) {
            File videoFile = new File(context.getCacheDir().getPath() + "/" + RecognitionFragment.getCurrentMarker().getId() + ".mp4");

            if(videoFile.exists()){
                playOrPauseVideo();

                float[] mvMatrix = new float[16];
                float[] pMatrix = new float[16];
                if (computeModelViewProjectionMatrix(mvMatrix, pMatrix)) {

                    //Render Video Only if the video is loaded
                    videoMesh.DrawMesh(mvMatrix, pMatrix);
                    RenderUtils.checkGLError("completed video mesh Render");

                }
            }
        }

        //if the video is still playing and we have lost tracking, we still draw the video,
        //but in a fixed frontal position
        if(!RecognitionFragment.isTracking() && videoMesh.isPlaying()) {
            float[] mvMatrix = new float[16];
            float[] pMatrix = new float[16];
            computeProjectionMatrix(pMatrix);

            if(Angle==0) {
                mvMatrix[0] = 1.0f; mvMatrix[1] = 0.0f; mvMatrix[2] = 0.0f; mvMatrix[3] = -0.5f;
                mvMatrix[4] = 0.0f; mvMatrix[5] = -1.0f; mvMatrix[6] = 0.0f; mvMatrix[7] = 0.4f;
                mvMatrix[8] = 0.0f; mvMatrix[9] = 0.0f; mvMatrix[10] = -1.0f; mvMatrix[11] = -1.3f;
                mvMatrix[12] = 0.0f; mvMatrix[13] = 0.0f; mvMatrix[14] = 0.0f; mvMatrix[15] = 1.0f;
            }
            else if(Angle==90) {
                mvMatrix[0] = 0.0f; mvMatrix[1] = 1.0f; mvMatrix[2] = 0.0f; mvMatrix[3] = -0.5f;
                mvMatrix[4] = 1.0f; mvMatrix[5] = 0.0f; mvMatrix[6] = 0.0f; mvMatrix[7] = -0.5f;
                mvMatrix[8] = 0.0f; mvMatrix[9] = 0.0f; mvMatrix[10] = -1.0f; mvMatrix[11] = -1.3f;
                mvMatrix[12] = 0.0f; mvMatrix[13] = 0.0f; mvMatrix[14] = 0.0f; mvMatrix[15] = 1.0f;
            }
            else if(Angle==180) {
                mvMatrix[0] = -1.0f; mvMatrix[1] = 0.0f; mvMatrix[2] = 0.0f; mvMatrix[3] = 0.5f;
                mvMatrix[4] = 0.0f; mvMatrix[5] = 1.0f; mvMatrix[6] = 0.0f; mvMatrix[7] = -0.4f;
                mvMatrix[8] = 0.0f; mvMatrix[9] = 0.0f; mvMatrix[10] = -1.0f; mvMatrix[11] = -1.3f;
                mvMatrix[12] = 0.0f; mvMatrix[13] = 0.0f; mvMatrix[14] = 0.0f; mvMatrix[15] = 1.0f;
            }
            else if(Angle==270) {
                mvMatrix[0] = 0.0f; mvMatrix[1] = -1.0f; mvMatrix[2] = 0.0f; mvMatrix[3] = 0.5f;
                mvMatrix[4] = -1.0f; mvMatrix[5] = 0.0f; mvMatrix[6] = 0.0f; mvMatrix[7] = 0.5f;
                mvMatrix[8] = 0.0f; mvMatrix[9] = 0.0f; mvMatrix[10] = -1.0f; mvMatrix[11] = -1.3f;
                mvMatrix[12] = 0.0f; mvMatrix[13] = 0.0f; mvMatrix[14] = 0.0f; mvMatrix[15] = 1.0f;
            }

            videoMesh.DrawMesh(mvMatrix, pMatrix);
            RenderUtils.checkGLError("completed video mesh Render");

        }
        gl.glFinish();
    }

    public void playOrPauseVideo() {
        if(videoMesh!=null) videoMesh.playOrPauseVideo();
    }

    public void pauseVideo() {
        if(videoMesh!=null) videoMesh.pauseVideo();
    }

    /**
     * \brief update viewport params
     * @param viewportWidth the new viewport width
     * @param viewportHeight the new viewport height
     * @param angle the new viewport angle
     */
    public void UpdateViewport(int viewportWidth, int viewportHeight, int angle)
    {
        ViewportWidth = viewportWidth;
        ViewportHeight = viewportHeight;
        Angle = angle;
    }

    public VideoMesh getVideoMesh(){
        return videoMesh;
    }





}