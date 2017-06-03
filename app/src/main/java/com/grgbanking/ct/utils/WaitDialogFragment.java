package com.grgbanking.ct.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ：     cmy
 * @version :     2017/5/6.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class WaitDialogFragment extends DialogFragment  {
    private String noteMsg;
    private View rootView;  //表示这个fragment的根视图
    private ImageView dialogImageView;  //实现转圈的动画
    private TextView dialogTextView;   //文字提示
    private Animation animation;

    public WaitDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
