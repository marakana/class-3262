
package com.marakana.android.yamba;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity {
    private static final String TAG = "STATUS";

    Poster poster;

    class Poster extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... msg) {
            fakeSend(msg[0]);
            return Integer.valueOf(R.string.post_succeeded);
        }

        @Override
        protected void onPostExecute(Integer result) {
            poster = null;
            Toast.makeText(StatusActivity.this, result.intValue(), Toast.LENGTH_LONG).show();
        }
    }

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

        Button button = (Button) findViewById(R.id.status_submit);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) { post(); }
                });

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

    void post() {
        String msg = status.getText().toString();
        if (TextUtils.isEmpty(msg)) { return; }

        if (null != poster) { return; }
        poster = new Poster();

        poster.execute(msg);
        status.setText("");
    }

    void fakeSend(String msg) {
        Log.d(TAG, "Sending: " + msg);
        try { Thread.sleep(10 * 1000); }
        catch (InterruptedException e) { }
        Log.d(TAG, "Sent: " + msg);
    }
}
