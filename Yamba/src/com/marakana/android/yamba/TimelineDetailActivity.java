package com.marakana.android.yamba;

import android.app.Activity;
import android.os.Bundle;


public class TimelineDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_detail_activity);

        TimelineDetailFragment frag = (TimelineDetailFragment) getFragmentManager()
            .findFragmentByTag(getString(R.string.timeline_detail_fragment));

        frag.setDetails(getIntent().getExtras());
    }
}
