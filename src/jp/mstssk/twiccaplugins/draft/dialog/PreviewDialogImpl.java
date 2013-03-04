
package jp.mstssk.twiccaplugins.draft.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.mstssk.twiccaplugins.draft.R;
import jp.mstssk.twiccaplugins.draft.Tweet;

/**
 * 下書きのプレビュー表示ダイアログ 実装
 * 
 * @author mstssk
 */
public class PreviewDialogImpl implements PreviewDialog {

    private final List<OnButtonClickListener> listeners = new ArrayList<OnButtonClickListener>();
    private final View view; // showが呼ばれるまでDialogのfindViewByIdはviewの中を走査してくれないのでフィールドに持つ
    private final Dialog dialog;
    private Tweet tweet;

    public PreviewDialogImpl(final Context context) {
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        this.dialog = new AlertDialog.Builder(context).setView(this.view).create();
        this.initButtonListener(R.id.button_load, PreviewDialogButtonType.LOAD);
        this.initButtonListener(R.id.button_overwrite, PreviewDialogButtonType.OVERWRITE);
        this.initButtonListener(R.id.button_delete, PreviewDialogButtonType.DELETE);
    }

    private void initButtonListener(final int buttonId, final PreviewDialogButtonType type) {
        this.view.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PreviewDialogImpl.this.fireButtonClickEvent(type);
            }
        });
    }

    private void fireButtonClickEvent(final PreviewDialogButtonType type) {
        for (final OnButtonClickListener listener : this.listeners) {
            listener.onClick(type, this.tweet);
        }
    }

    @Override
    public void init(final PreviewDialogMode mode, final Tweet tweet) {
        this.setMode(mode);
        this.setTweet(tweet);
    }

    /**
     * モードに応じてボタンの表示を切り替え
     * 
     * @param mode
     */
    private void setMode(final PreviewDialogMode mode) {
        final View buttonOverwrite = this.view.findViewById(R.id.button_overwrite);
        final View buttonLoad = this.view.findViewById(R.id.button_load);
        if (mode == PreviewDialogMode.LOADING) {
            buttonOverwrite.setVisibility(View.GONE);
            buttonLoad.setVisibility(View.VISIBLE);
        } else {
            buttonOverwrite.setVisibility(View.VISIBLE);
            buttonLoad.setVisibility(View.GONE);
        }
    }

    private void setTweet(final Tweet tweet) {
        ((TextView) this.view.findViewById(R.id.text_tweet_preview)).setText(tweet.getTweet());
    }

    @Override
    public void addOnButtonClickListener(final OnButtonClickListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void show() {
        this.dialog.show();

    }

    @Override
    public void close() {
        this.dialog.dismiss();
    }

}
