
package net.debian.debiandroid.contentfragments;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.ListDisplayFragment;
import net.debian.debiandroid.R;
import net.debian.debiandroid.apiLayer.UDD;
import net.debian.debiandroid.utils.QRCodeUtils;
import net.debian.debiandroid.utils.UIUtils;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uberspot.storageutils.Cacher;
import com.uberspot.storageutils.StorageUtils;

public class CIFFragment extends ItemFragment {

    private EditText mailInput;
    private String developerMail, scannedMail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateUIReceiver,
                new IntentFilter(ListDisplayFragment.LIST_ACTION));
    }

    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentFragID.equals(ContentHelper.CIF)) {
                if (ItemFragment.isInListDisplayFrag) {
                    SherlockFragmentActivity sa = getSherlockActivity();
                    if (sa != null) {
                        // Process the received action
                        String action = intent.getStringExtra(ListDisplayFragment.LIST_ACTION);
                        if (action.equals(ListDisplayFragment.REFRESH_ACTION)) {
                            // Pop listdisplayfragment from backstack
                            sa.getSupportFragmentManager().popBackStack();
                            new CIFSearchTask().execute(true);
                        }
                    }
                }

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cif_fragment, container, false);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.find_common_interests);

        ImageButton searchButton = (ImageButton) rootView.findViewById(R.id.cifSearchButton);
        mailInput = (EditText) rootView.findViewById(R.id.cifInputSearch);

        Button qrScanButton = (Button) rootView.findViewById(R.id.cifScanQRButton);
        qrScanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(getSherlockActivity());
                integrator.initiateScan();
            }
        });

        ImageView qrcodeView = (ImageView) rootView.findViewById(R.id.cifQRCodeView);
        try {
            developerMail = StorageUtils.getInstance(getSherlockActivity()).getPreference("ddemail", "empty");
            if (!Patterns.EMAIL_ADDRESS.matcher(developerMail).matches()) {
                UIUtils.showToast(getActivity(), getString(R.string.no_mail_in_settings_msg));
            }

            Bitmap bm = QRCodeUtils.encodeAsBitmap(developerMail, BarcodeFormat.QR_CODE, 250, 250);
            if (bm != null) {
                qrcodeView.setImageBitmap(bm);
            }
        } catch (WriterException e) {
        }

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input = mailInput.getText().toString().trim();
                if ((input != null) && !input.equals("")) {
                    scannedMail = input;
                    doCIFSearch();
                }
            }
        });

        mailInput.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = mailInput.getText().toString().trim();
                if ((actionId == EditorInfo.IME_ACTION_SEARCH) && (input != null) && !input.equals("")) {
                    scannedMail = input;
                    doCIFSearch();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if ((scanResult != null) && (scanResult.getContents() != null)) {
            scannedMail = scanResult.getContents();
            doCIFSearch();
        }
    }

    private void doCIFSearch() {
        developerMail = StorageUtils.getInstance(getSherlockActivity()).getPreference("ddemail", "empty");

        if (!Patterns.EMAIL_ADDRESS.matcher(scannedMail).matches()) {
            UIUtils.showToast(getActivity(), getString(R.string.invalid_mail_msg, scannedMail));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(developerMail).matches()) {
            UIUtils.showToast(getActivity(), getString(R.string.invalid_mail_msg, developerMail));
        } else {
            UIUtils.hideSoftKeyboard(getActivity(), mailInput);
            new CIFSearchTask().execute();
        }
    }

    class CIFSearchTask extends AsyncTask<Boolean, Void, Void> {

        private ProgressDialog progressDialog;
        private Bundle arguments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.searching),
                    getString(R.string.searching_info_please_wait), true, false);
            arguments = new Bundle();
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            //If called with execute(true) disable the cache to always bring fresh results
            if ((params.length != 0) && params[0]) {
                Cacher.disableCache();
            }

            ArrayList<String> titles = new ArrayList<String>(Arrays.asList(
                    getString(R.string.maintains_following, developerMail, scannedMail),
                    getString(R.string.maintains_following, scannedMail, developerMail)));
            UDD udd = new UDD(getSherlockActivity());
            ArrayList<String> packages = new ArrayList<String>(Arrays.asList(
                    arrayToString(udd.getOverlappingInterests(developerMail, scannedMail)),
                    arrayToString(udd.getOverlappingInterests(scannedMail, developerMail))));

            ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
            items.add(titles);
            items.add(packages);

            arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
            arguments
                    .putString(ListDisplayFragment.LIST_HEADER_ID, getString(R.string.overlapping_interests));
            arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.overlapping_interests));

            if ((params.length != 0) && params[0]) {
                Cacher.enableCache();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    return;
                }
            } else {
                return;
            }

            ListDisplayFragment.loadAndShow(getSherlockActivity().getSupportFragmentManager(), arguments);
        }

        protected String arrayToString(String[] items) {
            StringBuilder builder = new StringBuilder();
            for (String s : items) {
                builder.append(s);
                builder.append("\n");
            }
            return builder.toString();
        }
    }
}
