package com.hltc.mtmap.helper;


import android.os.Handler;

import com.dd.processbutton.ProcessButton;

import java.util.Random;

public class ProgressGenerator {

    public interface OnCompleteListener {
        public void onComplete();
    }

    private OnCompleteListener mListener;
    private int mProgress;
    private boolean ifStop = false;

    public ProgressGenerator(OnCompleteListener listener) {
        mListener = listener;
    }

    public void start(final ProcessButton button) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress += 10;
                button.setProgress(mProgress);
//                if (mProgress < 100) {
//                    handler.postDelayed(this, generateDelay());
//                } else {
//                    mListener.onComplete();
//                }
                if (!ifStop) {
                    handler.postDelayed(this, generateDelay());
                } else {
                    mListener.onComplete();
                }
            }

        }, generateDelay());
    }

    public void stop() {
        ifStop = true;
    }

    private Random random = new Random();

    private int generateDelay() {
        return random.nextInt(1000);
    }

}