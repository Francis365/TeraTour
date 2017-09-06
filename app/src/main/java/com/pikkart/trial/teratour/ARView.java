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

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * \class ARView
 * \brief The app own derivative implementation of Pikkart's GLTextureView
 *
 * The app own derivative implementation of our GLTextureView, with params and stuff
 * specific to this app (like our own new ARRenderer)
 */
public class ARView extends GLTextureView
{
    private Context _context; /**< the parent activity */
    private ARRenderer _renderer; /**< our own renderer with our own 3d objects */


    public ARRenderer getRenderer(){
        return _renderer;
    }

    /**
     * \brief The View onConfigurationChanged function
     *
     * Here we modify the layout params
     * @param newConfig the new app configuration
     */

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()==MotionEvent.ACTION_UP)
            _renderer.playOrPauseVideo();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        _renderer.pauseVideo();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig)
    {
        if (getParent() instanceof FrameLayout)
        {
            setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        }
        else if (getParent() instanceof RelativeLayout)
        {
            setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * \brief The View onLayout function
     *
     * Here we compute the correct screen orientation and update viewport variables of our
     * ARRenderer
     * @param changed has layout changed
     * @param left new left position of left side of view
     * @param top new top position of top side of view
     * @param right new right position of right side of view
     * @param bottom new bottom position of bottom side of view
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if(!changed) return;
        int angle = 0;

        Display display = ((WindowManager) _context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (rotation) {
                case 0:
                    angle = 0;
                    break;
                case 1:
                    angle = 0;
                    break;
                case 2:
                    angle = 180;
                    break;
                case 3:
                    angle = 180;
                    break;
                default:
                    break;
            }
        } else {
            switch (rotation) {
                case 0:
                    angle = 90;
                    break;
                case 1:
                    angle = 270;
                    break;
                case 2:
                    angle = 270;
                    break;
                case 3:
                    angle = 90;
                    break;
                default:
                    break;
            }
        }

        int realWidth;
        int realHeight;
        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
            }
        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        _renderer.UpdateViewport(right-left, bottom-top, angle);
    }

    /**
     * \brief Constructor.
     * @param context the app context
     */
    public ARView(Context context)
    {
        super(context);
        _context = context;
        init();
        _renderer = new ARRenderer(this._context);
        setRenderer(_renderer);
        ((ARRenderer)_renderer).IsActive = true;
        setOpaque(true);
    }

    /**
     * \brief Initialization.
     */
    public void init()
    {
        setEGLContextFactory(new ContextFactory());
        setEGLConfigChooser(new ConfigChooser(8, 8, 8, 0, 16, 0));
    }


    /**
     * \brief Creates OpenGL contexts.
     */
    private static class ContextFactory implements EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            EGLContext context;
            //Log.i("PikkartCore3","Creating OpenGL ES 2.0 context");
            checkEglError("Before eglCreateContext", egl);
            int[] attrib_list_gl20 = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
            context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list_gl20);
            checkEglError("After eglCreateContext", egl);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    /**
     * \brief Checks the OpenGL error.
     * @param prompt log tag
     * @param egl
     */
    private static void checkEglError(String prompt, EGL10 egl) {
        int error;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            Log.e("PikkartCore3", String.format("%s: EGL error: 0x%x", prompt, error));
        }
    }

    /**
     * \brief The config chooser.
     */
    private static class ConfigChooser implements EGLConfigChooser {
        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
            mRedSize = r;
            mGreenSize = g;
            mBlueSize = b;
            mAlphaSize = a;
            mDepthSize = depth;
            mStencilSize = stencil;
        }

        private EGLConfig getMatchingConfig(EGL10 egl, EGLDisplay display, int[] configAttribs) {
            // Get the number of minimally matching EGL configurations
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, configAttribs, null, 0, num_config);
            int numConfigs = num_config[0];
            if (numConfigs <= 0)
                throw new IllegalArgumentException("No matching EGL configs");
            // Allocate then read the array of minimally matching EGL configs
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, configAttribs, configs, numConfigs, num_config);
            // Now return the "best" one
            return chooseConfig(egl, display, configs);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            // This EGL config specification is used to specify 2.0 com.pikkart.ar.rendering. We use a minimum size of 4 bits for
            // red/blue/blue, but will perform actual matching in chooseConfig() below.
            final int EGL_OPENGL_ES2_BIT = 0x0004;
            final int[] s_configAttribs_gl20 = {EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE};
            return getMatchingConfig(egl, display, s_configAttribs_gl20);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            boolean bFoundDepth = false;
            //boolean bFoundStencil=false;
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                if (d == mDepthSize) bFoundDepth = true;
            }
            if (bFoundDepth == false) mDepthSize = 16; //min value
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
                // We need at least mDepthSize and mStencilSize bits
                if (d < mDepthSize || s < mStencilSize)
                    continue;
                // We want an *exact* match for red/blue/blue/alpha
                int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);

                if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize)
                    return config;
            }

            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, mValue))
                return mValue[0];
            return defaultValue;
        }

        // Subclasses can adjust these values:
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];
    }

}