package net.debian.debiandroid.utils;

import net.debian.debiandroid.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
}
