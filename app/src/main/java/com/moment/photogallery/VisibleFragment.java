package com.moment.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(requireContext(), "Got a broadcast:" + intent.getAction(), Toast.LENGTH_SHORT).show();
            // If we receive this, we're visible, so cancel the notification
            Log.i(TAG, "canceling notification");
            int resultCode = Activity.RESULT_CANCELED;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(PollWorker.Companion.ACTION_SHOW_NOTIFICATION);
        requireActivity().registerReceiver(receiver, filter, PollWorker.Companion.PERM_PRIVATE, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(receiver);
    }
}
