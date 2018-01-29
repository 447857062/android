package com.deplink.homegenius.util.qrcode.qrcodecapture;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.deplink.homegenius.activity.device.AddDeviceNameActivity;
import com.deplink.homegenius.activity.device.AddDeviceQRcodeActivity;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.util.qrcode.CameraManager;
import com.deplink.homegenius.util.qrcode.CaptureActivityHandler;
import com.deplink.homegenius.util.qrcode.InactivityTimer;
import com.deplink.homegenius.util.qrcode.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "CaptureActivity";

    public static final int CAPTURE_TYPE_STRING = 1;
    public static final int CAPTURE_TYPE_SWITCH = 2;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private SurfaceView surfaceView;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate = true;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        CameraManager.init(this);
        viewInit();
        if (getIntent() != null) {
            Log.i(TAG, "onCreate" + getIntent().getIntExtra("requestType", 0));
            type = getIntent().getIntExtra("requestType", 0);
        }

    }

    private void viewInit() {
        surfaceView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        TextView textview_cancel = findViewById(R.id.textview_cancel);
        textview_cancel.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            Log.i(TAG, "initCamera characterSet=" + characterSet);
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void playBeepSoundAndVibrate() {
        Log.i(TAG, "playBeepSoundAndVibrate playBeep=" + playBeep + "mediaPlayer!=null" + (mediaPlayer != null) + "vibrate" + vibrate);
        if (playBeep && mediaPlayer != null) {
            Log.i(TAG, "播放声音");
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200L);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_cancel:
                finish();
                onDestroy();
                break;

        }
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 处理扫描结果
     *
     * @param obj
     * @param barcode
     */
    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        String deviceSn = obj.getText();
        Log.i(TAG, "扫描设备二维码返回: " + deviceSn);
        Intent intent = new Intent(CaptureActivity.this, AddDeviceQRcodeActivity.class);
        switch (type) {
            case CAPTURE_TYPE_STRING:
                intent.putExtra("deviceSN", deviceSn);
                this.setResult(RESULT_OK, intent);
                finish();
                break;
            case CAPTURE_TYPE_SWITCH:
                intent = new Intent(this, AddDeviceNameActivity.class);
                intent.putExtra("switchqrcode", deviceSn);
                intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_SWITCH);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
