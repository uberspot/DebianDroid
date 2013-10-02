package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemDetailFragment;
import net.debian.debiandroid.ListDisplayFragment;
import net.debian.debiandroid.apiLayer.UDD;
import net.debian.debiandroid.utils.QRCodeEncoder;

import com.debian.debiandroid.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidStorageUtils.StorageUtils;

public class CIFFragment extends ItemDetailFragment {

	private ImageView qrcodeView;
	private Button qrScanButton;
	private ImageButton searchButton;
	private EditText mailInput;
	private String developerMail;
	private UDD udd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.cif_fragment, container, false);
    	
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.find_common_interests));
    	
    	udd = new UDD(getSherlockActivity());
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
    		developerMail = StorageUtils.getInstance(getSherlockActivity()).getPreference("ddemail", "empty");
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
			hideSoftKeyboard(mailInput);
			new CIFSearchTask().execute(scannedMail, developerMail);
		}
	}
	
	class CIFSearchTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog progressDialog;
		String title="", header=""; 
		ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		
		protected void onPreExecute() {
			   super.onPreExecute();
			   progressDialog = ProgressDialog.show(getSherlockActivity(), 
					   getString(R.string.searching), getString(R.string.searching_info) + ". " + getString(R.string.please_wait) + "...", true, false);  
			}
			
			protected Void doInBackground(String... params) {
				ArrayList<String> titles = new ArrayList<String>(Arrays.asList(
						params[0] + " " + getString(R.string.maintains_following) + " " + params[1] + ":", 
						params[1] + " " + getString(R.string.maintains_following) + " " + params[0] + ":"));
				
				ArrayList<String> packages = new ArrayList<String>(Arrays.asList(
						arrayToString(udd.getOverlappingInterests(params[0], params[1])), 
						arrayToString(udd.getOverlappingInterests(params[1], params[0]))
						));
				
				items.add(titles);
				items.add(packages);
				header =  getString(R.string.overlapping_interests);
				title = getString(R.string.overlapping_interests);
				
				return null;
			}
			
			protected void onPostExecute (Void result) {
				progressDialog.dismiss();
				ItemDetailFragment fragment = new ListDisplayFragment();
				Bundle arguments = new Bundle();
				arguments.putString(ListDisplayFragment.LIST_HEADER_ID, header);
				arguments.putString(ListDisplayFragment.LIST_TITLE_ID, title);
				arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
				
		        fragment.setArguments(arguments);
				
		    	getSherlockActivity().getSupportFragmentManager().beginTransaction()
		    	.replace(R.id.item_detail_container, fragment).addToBackStack(null).commit();
			}
			
			protected String arrayToString(String[] items) {
				StringBuilder builder = new StringBuilder();
				for(String s : items) {
				    builder.append(s);
				    builder.append("\n");
				}
				return builder.toString();
			}
	}
}
