package philoats.celerysplit.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import rx.functions.Action1;

public class ListSelectDialog {

    public static void show(Context context, String title, ArrayList<String> names, Action1<Integer> itemSelect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        CharSequence[] titles = new CharSequence[names.size()];
        names.toArray(titles);
        builder.setItems(titles, (dialog, item) -> {
            itemSelect.call(item);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
