package com.grgbanking.ct.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.grgbanking.ct.R;

;

/**
 * Created by lazylee on 2017/8/3.
 */

public class AudioManagerUtil {
    private Context mContext;
    private AudioManager manager;
    private MediaPlayer player;

    public AudioManagerUtil(Context mContext) {
        this.mContext = mContext;
        manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        player = MediaPlayer.create(mContext, R.raw.msg);
    }

    public  void playDiOnce(){
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                player.release();
            }
        });
    }
}
