package com.debian.debiandroid;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

public class CIFFragment extends ItemDetailFragment {

	private ImageView qrcodeView;
	private Button qrScanButton;
	private ImageButton searchButton;
	private EditText mailInput;
	private String developerMail;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.cif_item_detail, container, false);
    	
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.find_common_interests));
    	
    	searchButton = (ImageButton) rootView.findViewById(R.id.cifSearchButton);
    	mailInput = (EditText) rootView.findViewById(R.id.cifInputSearch);
    	
    	qrScanButton = (Button) rootView.findViewById(R.id.cifScanQRButton);
    	qrScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	IntentIntegrator integrator = new IntentIntegrator(getSherlockActivity());
            	integrator.initiateScan();
            }
        });
    	
    	qrcodeView = (ImageView) rootView.findViewById(R.id.cifQRCodeView);
    	try {
    		developerMail = PreferenceManager.getDefaultSharedPreferences(
    				getSherlockActivity()).getString("ddemail", "empty");
    		if(!Patterns.EMAIL_ADDRESS.matcher(developerMail).matches())
    			Toast.makeText(getSherlockActivity(), 
    					getString(R.string.no_mail_in_settings_msg), Toast.LENGTH_SHORT).show();
    		
    	    Bitmap bm = QRCodeEncoder.encodeAsBitmap(developerMail, BarcodeFormat.QR_CODE, 250, 250);
    	    if(bm != null) {
    	    	qrcodeView.setImageBitmap(bm);
    	    }
    	} catch (WriterException e) { }
    	
    	searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String input = mailInput.getText().toString().trim();
            	if(input!=null && !input.equals("")) {
            		doCIFSearch(input);
            	}
            }
        });
  		
  		mailInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        String input = mailInput.getText().toString().trim();
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH && input!=null && !input.equals("")) {
  		        	doCIFSearch(input);
  		            return true;
  		        }
  		        return false;
  		    }
  		});
  		
        return rootView;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null && scanResult.getContents()!=null) {
			  doCIFSearch(scanResult.getContents());
		  }
	}
	
	private void doCIFSearch(String scannedMail) {
		if(!Patterns.EMAIL_ADDRESS.matcher(scannedMail).matches() ) {
			Toast.makeText(getSherlockActivity(), 
					getString(R.string.invalid_mail_msg) + " " + scannedMail, Toast.LENGTH_SHORT).show();
		} else if (!Patterns.EMAIL_ADDRESS.matcher(developerMail).matches() ){
			Toast.makeText(getSherlockActivity(), 
					getString(R.string.invalid_mail_msg) + " " + developerMail, Toast.LENGTH_SHORT).show();
		} else {
			//do search and display results
		}
	}
}
