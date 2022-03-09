package com.moment.photogallery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.moment.photogallery.fragment.PhotoGalleryFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private Boolean hasQuit = false;
    private static final String TAG = "ThumbnailDownload";
    private static final int MESSAGE_DOWNLOAD = 0;
    private RequestHandler requestHandler;
    private Handler responseHandler;
    private ConcurrentHashMap<T, String> requestMap = new ConcurrentHashMap<>();
    private FlickrFetchr flickrFetchr = new FlickrFetchr();

    public ThumbnailDownloader(String name) {
        super(name);
    }

    public ThumbnailDownloader(String name, Handler responseHandler) {
        super(name);
        this.responseHandler = responseHandler;
    }



    @Override
    public boolean quit() {
        hasQuit = true;
        return super.quit();
    }

    public LifecycleObserver fragmentLifecycleObserver = new LifecycleObserver() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void setup() {
            Log.d(TAG, "Starting background thread");
            start();
            getLooper();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void tearDown() {
            Log.d(TAG, "Destroying background thread");
            quit();
        }
    };

    public LifecycleObserver lifecycleObserver = new LifecycleObserver() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void clearQueue() {
            Log.d(TAG, "Clearing all requests from queue");
            requestHandler.removeMessages(MESSAGE_DOWNLOAD);
            requestMap.clear();
        }
    };



    public void queueThumbnail(T target, String url) {
        Log.d(TAG, "Got a URL " + url);
        requestMap.put(target, url);
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();

    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestHandler = new RequestHandler(this.getLooper());
        } else {
            requestHandler = new RequestHandler();
        }
    }


    public class RequestHandler extends Handler {
        public RequestHandler(@NonNull @NotNull Looper looper) {
            super(looper);
        }

        @SuppressWarnings("deprecation")
        public RequestHandler() {
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_DOWNLOAD) {
                T target = (T) msg.obj;
                Log.i(TAG, "Got a request for URL:" + requestMap.get(target));
                try {
                    handleRequest(target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleRequest(T target) throws IOException {
        String url;
        Bitmap bitmap;
        if (requestMap.get(target) != null) {
            url = requestMap.get(target);
            bitmap = flickrFetchr.fetchPhoto(url);
            if (bitmap == null) {
                return;
            }
        } else {
            return;
        }
        responseHandler.post(()->{
            if (requestMap.get(target) != url || hasQuit) {
                return;
            }

            requestMap.remove(target);
            onThumbnailDownloaded(target, bitmap);
        });
    }

    private void onThumbnailDownloaded(T target, Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        ((PhotoGalleryFragment.PhotoHolder) target).bindTitle(drawable);
    }


}
