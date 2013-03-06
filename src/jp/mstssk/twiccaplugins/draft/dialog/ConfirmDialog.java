
package jp.mstssk.twiccaplugins.draft.dialog;

/**
 * 確認ダイアログ
 * 
 * @author mstssk
 */
public interface ConfirmDialog {

    public interface OnConfirmListener {
        void onConfirm();
    }

    void setMessage(String message);

    void setConfirmCallback(OnConfirmListener listener);

    void show();

}
