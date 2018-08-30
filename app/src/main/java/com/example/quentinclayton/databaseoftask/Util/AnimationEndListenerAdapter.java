package com.example.quentinclayton.databaseoftask.Util;

import android.animation.Animator;
import android.support.annotation.NonNull;

public class AnimationEndListenerAdapter implements Animator.AnimatorListener {
    private AnimationEndListener mAdapted;

    public AnimationEndListenerAdapter(@NonNull AnimationEndListener adapted) {
        this.mAdapted = adapted;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mAdapted.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
