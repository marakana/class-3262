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

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class TimelineFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private static final int TIMELINE_LOADER = 64;

    private static final String[] PROJ = new String[] {
        YambaContract.Timeline.Column.ID,
        YambaContract.Timeline.Column.TIMESTAMP,
        YambaContract.Timeline.Column.USER,
        YambaContract.Timeline.Column.STATUS
    };

    private static final String[] FROM = new String[PROJ.length - 1];
    static { System.arraycopy(PROJ, 1, FROM, 0, FROM.length); }

    private static final int[] TO = new int[] {
        R.id.timeline_timestamp,
        R.id.timeline_user,
        R.id.timeline_status
    };

    static class TimelineBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor c, int idx) {
            if (R.id.timeline_timestamp != view.getId()) { return false; }

            CharSequence s = "long ago";
            long t = c.getLong(idx);
            if (0 < t) { s = DateUtils.getRelativeTimeSpanString(t); }
            ((TextView) view).setText(s);

            return true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                YambaContract.Timeline.URI,
                PROJ,
                null,
                null,
                YambaContract.Timeline.Column.TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> l, Cursor cur) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(cur);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        View v = super.onCreateView(inflater, parent, state);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.timeline_row,
                null,
                FROM,
                TO,
                0);
        setListAdapter(adapter);
        adapter.setViewBinder(new TimelineBinder());

        getLoaderManager().initLoader(TIMELINE_LOADER, null, this);

        return v;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int p, long id) {
        Cursor c = (Cursor) lv.getItemAtPosition(p);

        Intent i = new Intent(getActivity(), TimelineDetailActivity.class);
        Bundle extra = TimelineDetailFragment.bundleDetails(
                c.getLong(c.getColumnIndex(YambaContract.Timeline.Column.TIMESTAMP)),
                c.getString(c.getColumnIndex(YambaContract.Timeline.Column.USER)),
                c.getString(c.getColumnIndex(YambaContract.Timeline.Column.STATUS)));
        i.putExtras(extra);
        startActivity(i);
    }
}
