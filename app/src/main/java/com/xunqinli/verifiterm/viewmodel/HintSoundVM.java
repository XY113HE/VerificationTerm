package com.xunqinli.verifiterm.viewmodel;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.interf.MainInterf;

import java.io.IOException;

public class HintSoundVM {
    private MediaPlayer mediaPlayer;
    private static final String TAG = "lmy_hintsound";
    private MainInterf.MainView mMainView;
    private AssetFileDescriptor file;

    public HintSoundVM(MainInterf.MainView mMainView) {
        this.mMainView = mMainView;
        file = mMainView.getActivity().getResources().openRawResourceFd(R.raw.beep);
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();//这个我定义了一个成员函数
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
            }
        });
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(0.5f , 0.5f);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage() == null ? "" : ioe.getMessage());
            mediaPlayer = null;
        }

    }

    public void startSound() {
        mediaPlayer.start();

    }

}
