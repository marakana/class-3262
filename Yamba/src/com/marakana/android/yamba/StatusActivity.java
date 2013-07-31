
package com.marakana.android.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class StatusActivity extends Activity {
    private static final String TAG = "STATUS";

    private TextView count;
    private EditText status;

    private int okColor;
    private int warnColor;
    private int errColor;
    private int fogColor;

    private int maxStatusLen;
    private int warnMax;
    private int errMax;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        Resources rez = getResources();

        okColor = rez.getColor(R.color.status_ok);
        warnColor = rez.getColor(R.color.status_warn);
        errColor = rez.getColor(R.color.status_err);
        fogColor = rez.getColor(R.color.fog);

        maxStatusLen = rez.getInteger(R.integer.status_max_len);
        warnMax = rez.getInteger(R.integer.warn_max);
        errMax = rez.getInteger(R.integer.err_max);

        setContentView(R.layout.activity_status);

        count = (TextView) findViewById(R.id.status_count);
        status = (EditText) findViewById(R.id.status_status);
        status.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void afterTextChanged(Editable str) { updateCount(); }

                @Override
                public void beforeTextChanged(CharSequence str, int s, int c, int a) { }

                @Override
                public void onTextChanged(CharSequence str, int s, int c, int a) { }
            } );
    }

    void updateCount() {
        int n = maxStatusLen - status.getText().length();

        int c;
        if (n > warnMax) { c = okColor; }
        else if (n > errMax) { c = warnColor; }
        else { c = errColor; }

        count.setTextColor(c);
        count.setText(String.valueOf(n));

        status.setBackgroundColor(
                (c == okColor)
                    ? fogColor
                    : (c & 0x0ffffff) | 0x09f7f7f7f);
    }
}
