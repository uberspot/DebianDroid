package com.debian.debiandroid;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class CIFFragment extends ItemDetailFragment {

	private ImageView qrcodeView;
	private Button qrScanButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.cif_item_detail, container, false);
    	
    	getSherlockActivity().getSupportActionBar().setTitle("Find Common Interests");
    	
    	qrScanButton = (Button) rootView.findViewById(R.id.cifScanQRButton);
    	qrScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	IntentIntegrator integrator = new IntentIntegrator(getSherlockActivity());
            	integrator.initiateScan();
            }
        });
    	
    	qrcodeView = (ImageView) rootView.findViewById(R.id.cifQRCodeView);
    	try {
    		String developerName = PreferenceManager.getDefaultSharedPreferences(
    				getSherlockActivity()).getString("ddusername", "empty");
    	    Bitmap bm = QRCodeEncoder.encodeAsBitmap(developerName, BarcodeFormat.QR_CODE, 250, 250);
 
    	    if(bm != null) {
    	    	qrcodeView.setImageBitmap(bm);
    	    }
    	} catch (WriterException e) { }
    	
        return rootView;
    }
	
	 public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null && scanResult.getContents()!=null) {
		    Log.i("QRCODE", scanResult.getContents());
		  }
	}
}
