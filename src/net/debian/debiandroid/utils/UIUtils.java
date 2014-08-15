package net.debian.debiandroid.utils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.R;
import net.debian.debiandroid.R.drawable;
import net.debian.debiandroid.R.string;
import net.debian.debiandroid.contentfragments.ContentHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class UIUtils {

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void forwardToMailApp(Context context, String recipient, String subject, String body) {
        String uri = new StringBuilder("mailto:").append(Uri.encode(recipient))
                .append("?subject=").append(subject)
                .append("&body=").append(body).toString();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));

        /* Send it off to the Activity-Chooser */
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_mail)));
    }

    /** Returns the position of the value in the given array
     * @param values
     * @return an int which is the position of the language in the values or 0 if it is not found
     */
    public static int getValuePosition(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    public static void hideSoftKeyboard(Activity activity, EditText input) {
        if ((activity.getCurrentFocus() != null) && (activity.getCurrentFocus() instanceof EditText)) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    public static void loadFragment(FragmentManager fm, String fragID, Bundle arguments, boolean animateToLeft) {
        if (!fragID.equals(ItemFragment.currentFragID)) {
            ItemFragment fragment = ContentHelper.getDetailFragment(fragID);
            if (arguments != null) {
                fragment.setArguments(arguments);
            }
            FragmentTransaction ft = fm.beginTransaction();
            if (animateToLeft) {
                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
                        R.anim.push_right_out);
            } else {
                ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in,
                        R.anim.push_left_out);
            }
            ft.replace(R.id.item_detail_container, fragment).commit();
        }
    }

    public static void addSettingsMenuItem(Menu menu) {
        menu.add(0, ItemFragment.SETTINGS_ID, Menu.CATEGORY_CONTAINER, R.string.settings).setIcon(R.drawable.settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
