
package jp.mstssk.twiccaplugins.draft.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 確認ダイアログ 実装
 * 
 * @author mstssk
 */
public class ConfirmDialogImpl implements ConfirmDialog {

    private final AlertDialog dialog;
    private OnConfirmListener listener;

    public ConfirmDialogImpl(final Context context) {
        final DialogInterface.OnClickListener listener = this.getListenerWrapper();
        this.dialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, listener
                ).setNegativeButton(android.R.string.cancel, null).create();
    }

    private DialogInterface.OnClickListener getListenerWrapper() {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                ConfirmDialogImpl.this.listener.onConfirm();
            }
        };
        return listener;
    }

    @Override
    public void setMessage(final String message) {
        this.dialog.setMessage(message);
    }

    @Override
    public void setConfirmCallback(final OnConfirmListener listener) {
        this.listener = listener;

    }

    @Override
    public void show() {
        this.dialog.show();
    }

}
