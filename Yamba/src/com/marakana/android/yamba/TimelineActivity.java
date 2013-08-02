
package com.marakana.android.yamba;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.marakana.android.yamba.svc.YambaService;

public class TimelineActivity extends YambaActivity  {
    private static final String DETAIL_FRAGMENT = "TimelineActivity.DETAILS";

    private boolean usingFrags;

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (usingFrags) { launchDetailFragment(intent.getExtras()); }
        else { super.startActivityFromFragment(fragment, intent, requestCode); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        usingFrags = (null != findViewById(R.id.timeline_details));

        if (usingFrags) { addDetailFragment(); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        YambaService.stopPolling(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YambaService.startPolling(this);
    }

    private void addDetailFragment() {
        FragmentManager mgr = getFragmentManager();

        if (null != mgr.findFragmentByTag(DETAIL_FRAGMENT)) { return; }

        FragmentTransaction xact = mgr.beginTransaction();
        xact.add(
                R.id.timeline_details,
                new TimelineDetailFragment(),
                DETAIL_FRAGMENT);
        xact.commit();
    }

    private void launchDetailFragment(Bundle extras) {
        FragmentTransaction xact = getFragmentManager().beginTransaction();
        xact.replace(
                R.id.timeline_details,
                TimelineDetailFragment.newInstance(extras),
                DETAIL_FRAGMENT);
        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        xact.commit();
    }
}
