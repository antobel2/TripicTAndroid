package com.club.coolkids.clientandroid.create_post;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

//classe qui modifie une image view pour toujours retourner une
//image carré selon la grosseur de l'appareil (automatic square resize)
public class SquareImageView extends android.support.v7.widget.AppCompatImageView{

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
