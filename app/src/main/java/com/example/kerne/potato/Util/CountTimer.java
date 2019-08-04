package com.example.kerne.potato.Util;

import android.os.CountDownTimer;
import android.widget.PopupWindow;

public class CountTimer extends CountDownTimer {
    private PopupWindow mPopupWindow;

    public CountTimer(long millisInFuture, long countDownInterval, PopupWindow popupWindow) {
        super(millisInFuture, countDownInterval);
        mPopupWindow = popupWindow;
        this.start();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (millisUntilFinished < 1000 && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onFinish() {
        cancel();
    }
}
