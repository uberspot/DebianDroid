
package net.debian.debiandroid.view;

import net.debian.debiandroid.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchBarView extends LinearLayout {

    private EditText inputSearch;
    private ImageButton searchButton;
    private OnSearchActionListener searchActionListener = null;

    public SearchBarView(Context context) {
        super(context);
        init(context);
    }

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("NewApi")
    public SearchBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_bar_view, this, true);

        inputSearch = (EditText) getChildAt(0);
        searchButton = (ImageButton) getChildAt(1);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input = inputSearch.getText().toString().trim();
                if (searchActionListener != null) {
                    searchActionListener.onSearchAction(input);
                }
            }
        });

        inputSearch.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = inputSearch.getText().toString().trim();
                if ((actionId == EditorInfo.IME_ACTION_SEARCH) && (searchActionListener != null)) {
                    searchActionListener.onSearchAction(input);
                    return true;
                }
                return false;
            }
        });
    }


    public EditText getInputEditText() {
        return inputSearch;
    }

    public ImageButton getSearchButton() {
        return searchButton;
    }

    public void setHintAndType(int hintTextId, int inputType) {
        inputSearch.setHint(hintTextId);
        inputSearch.setInputType(hintTextId);
    }


    public interface OnSearchActionListener {
        /**
         * Called when a search action is being performed.
         *
         * @param searchInput the term to be searched
         */
        public void onSearchAction(String searchInput);
    }

    public void setOnSearchActionListener(OnSearchActionListener listener) {
        searchActionListener = listener;
    }
}
