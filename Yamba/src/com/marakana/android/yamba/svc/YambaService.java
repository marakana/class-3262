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
package com.marakana.android.yamba.svc;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.R;
import com.marakana.android.yamba.YambaContract;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;


public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final int POLL_REQ = 42;

    private static final String PARAM_STATUS = "YambaService.STATUS";
    private static final String PARAM_OP = "YambaService.OP";
    private static final int OP_POST_COMPLETE = -1;
    private static final int OP_POST = -2;
    private static final int OP_POLL = -3;

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }

    public static void startPolling(Context ctxt) {
        long pollInterval = 1000 * 60 * ctxt.getResources().getInteger(R.integer.poll_interval);
        AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        mgr.setInexactRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 100,
                pollInterval,
                getPollingIntent(ctxt));
    }

    private static PendingIntent getPollingIntent(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL);
        return PendingIntent.getService(
                ctxt,
                POLL_REQ,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static class Hdlr extends Handler {
        private final YambaService svc;

        public Hdlr(YambaService svc) { this.svc = svc; }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case OP_POST_COMPLETE:
                    Toast.makeText(svc, msg.arg1, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    private int maxPolls;
    private volatile YambaClient client;
    private volatile Hdlr hdlr;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();
        maxPolls = getResources().getInteger(R.integer.poll_max);
        hdlr = new Hdlr(this);
        client = new YambaClient(
                "student",
                "password",
                "http://yamba.marakana.com/api");
    }

    @Override
    protected void onHandleIntent(Intent i) {
        int op = i.getIntExtra(PARAM_OP, 0);
        Log.d(TAG, "Handle intent:  " + op);
        switch (op) {
            case OP_POST:
                doPost(i.getStringExtra(PARAM_STATUS));
                break;
            case OP_POLL:
                doPoll();
                break;
            default:
                Log.w(TAG, "Unexpected op: " + op);
        }
    }

    private void doPoll() {
        try { parseTimeline(client.getTimeline(maxPolls)); }
        catch (Exception e) { Log.w(TAG, "Poll failed: " + e, e); }
    }

    private void doPost(String status) {
        Log.d(TAG, "Posting: " + status);
        int ret = R.string.post_failed;
        try {
            client.postStatus(status);
            ret = R.string.post_succeeded;
            Log.d(TAG, "Post succeeded!");
        }
        catch (YambaClientException e) {
            Log.w(TAG, "Post failed: " + e, e);
        }

        Message.obtain(hdlr, OP_POST_COMPLETE, ret, 0).sendToTarget();
    }

    private void parseTimeline(List<Status> timeline) {
        if (null == timeline) { return; }

        List<ContentValues> rows = new ArrayList<ContentValues>();
        for (Status status: timeline) {
            ContentValues row = new ContentValues();
            row.put(
                    YambaContract.Timeline.Column.ID,
                    Long.valueOf(status.getId()));
            row.put(
                    YambaContract.Timeline.Column.TIMESTAMP,
                    Long.valueOf(status.getCreatedAt().getTime()));
            rows.add(row);
        }

        getContentResolver().bulkInsert(
                YambaContract.Timeline.URI,
                rows.toArray(new ContentValues[rows.size()]));
    }
}
