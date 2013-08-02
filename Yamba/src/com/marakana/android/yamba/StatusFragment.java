/* $Id: $
   Copyright 2013, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.marakana.android.yamba.svc.YambaService;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class StatusFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        Resources rez = getResources();

        okColor = rez.getColor(R.color.status_ok);
        warnColor = rez.getColor(R.color.status_warn);
        errColor = rez.getColor(R.color.status_err);
        fogColor = rez.getColor(R.color.fog);

        maxStatusLen = rez.getInteger(R.integer.status_max_len);
        warnMax = rez.getInteger(R.integer.warn_max);
        errMax = rez.getInteger(R.integer.err_max);

        View v = inflater.inflate(R.layout.fragment_status, parent, false);

        Button button = (Button) v.findViewById(R.id.status_submit);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) { post(); }
                });

        count = (TextView) v.findViewById(R.id.status_count);
        status = (EditText) v.findViewById(R.id.status_status);
        status.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void afterTextChanged(Editable str) { updateCount(); }

                @Override
                public void beforeTextChanged(CharSequence str, int s, int c, int a) { }

                @Override
                public void onTextChanged(CharSequence str, int s, int c, int a) { }
            } );

        return v;
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

        YambaService.post(getActivity(), msg);
        status.setText("");
    }
}
