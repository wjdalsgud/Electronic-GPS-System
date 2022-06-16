package com.example.test;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
public class ProgessDialog extends Dialog {
    public ProgessDialog(Context context)
    {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
    }
}
