
package jp.mstssk.twiccaplugins.draft.dialog;

import jp.mstssk.twiccaplugins.draft.Tweet;

/**
 * 下書きのプレビュー表示ダイアログ<br>
 * サブメニューを兼ねる。
 * 
 * @author mstssk
 */
public interface PreviewDialog {

    public enum PreviewDialogMode {
        /** 保存時表示 */
        SAVING,
        /** 取得時表示 */
        LOADING;
    }

    public enum PreviewDialogButtonType {
        LOAD, OVERWRITE, DELETE;
    }

    public interface OnButtonClickListener {
        void onClick(PreviewDialogButtonType which, Tweet tweet);
    }

    void init(PreviewDialogMode mode, Tweet tweet);

    void addOnButtonClickListener(OnButtonClickListener listener);

    void show();

    void close();
}
