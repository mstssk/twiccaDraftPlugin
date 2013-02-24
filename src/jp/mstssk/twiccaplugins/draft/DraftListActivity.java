
package jp.mstssk.twiccaplugins.draft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jp.mstssk.twiccaplugins.draft.db.DraftTweetRepository;
import jp.mstssk.twiccaplugins.draft.db.DraftTweetRepositoryImpl;
import jp.mstssk.twiccaplugins.draft.utils.TwiccaPluginApiUtils;

/**
 * 下書き一覧画面
 * 
 * @author mstssk
 */
public class DraftListActivity extends Activity implements OnClickListener, OnLongClickListener {

    // 画面モード
    protected enum Mode {
        SAVE, // 保存モード
        // 読み込みモード
        LIST;
    }

    private static int TAG_ID = R.id.tweet_id;

    private Mode mode = Mode.SAVE;

    private DraftTweetRepository repository;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // configuration変更前の状態を引き継がないようにする
            savedInstanceState.clear();
        }
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Log.i("mstssk", getComponentName().getShortClassName()); //
        // manifestでのname

        // 画面モード切り替え
        this.mode = this.getMode();

        // ツイートの空確認
        if (this.mode.equals(Mode.SAVE) && this.isIntentTweetEmpty()) {
            this.showEmptyTweetMsg();
            this.finish();
            return;
        }

        final LinearLayout layout = (LinearLayout) this.findViewById(R.id.list_menus);
        final View buttonSaveNew = layout.findViewById(R.id.button_save_as_new);

        // 読み込みモードでは新規登録ボタンを隠す
        if (this.mode.equals(Mode.LIST)) {
            buttonSaveNew.setVisibility(View.GONE);
        } else {
            buttonSaveNew.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    DraftListActivity.this.saveTweet(TwiccaPluginApiUtils
                            .getTweet(DraftListActivity.this.getIntent()));
                }
            });
        }

        if (this.repository == null) {
            // TODO repositoryはDIするようにしたい
            this.repository = new DraftTweetRepositoryImpl(this);
        }

        // 1件も保存されていない場合
        if (this.mode.equals(Mode.LIST) && this.repository.getCount() == 0) {
            Toast.makeText(this, this.getString(R.string.msg_no_draft), Toast.LENGTH_SHORT).show();
            this.finish();
        }

        this.drawList();
    }

    @Override
    public void onStop() {
        super.onStop();

        // DB接続解除
        if (this.repository != null) {
            this.repository = null;
        }
    }

    /**
     * 選択時処理
     */
    @Override
    public void onClick(final View view) {

        if (view.getId() == R.id.list_button_submenu) {
            // サブメニューボタンなら押し時処理へ
            this.onLongClick(view);
        } else {
            final long id = (Long) view.getTag(TAG_ID);
            if (this.mode.equals(Mode.LIST)) {
                // ツイートをセット
                this.loadTweet(id);
            } else {
                // ツイートを保存
                this.overwriteTweet(null, id,
                        TwiccaPluginApiUtils.getTweet(this.getIntent()));
            }
        }
    }

    /**
     * 長押し時処理<br>
     * プレビューメニュー表示
     */
    @Override
    public boolean onLongClick(final View view) {

        // final LinearLayout item = (LinearLayout) view.getParent();
        final long id = (Long) view.getTag(TAG_ID);
        final View layout = LayoutInflater.from(this).inflate(R.layout.dialog, null);

        // 読み込みモードでは「上書き保存」ボタンの代わりに「読み込み」ボタン表示
        final View buttonOverwrite = layout.findViewById(R.id.button_overwrite);
        final View buttonLoad = layout.findViewById(R.id.button_load);
        if (Mode.LIST.equals(this.mode)) {
            buttonOverwrite.setVisibility(View.GONE);
            buttonLoad.setVisibility(View.VISIBLE);
        } else {
            buttonOverwrite.setVisibility(View.VISIBLE);
            buttonLoad.setVisibility(View.GONE);
        }

        final TextView text = (TextView) layout.findViewById(R.id.text_tweet_preview);
        text.setText(this.repository.load(id).getTweet());
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(layout).create();

        // クリックリスナ
        final OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.button_delete:
                        DraftListActivity.this.deleteTweet(dialog, id);
                        break;
                    case R.id.button_overwrite:
                        DraftListActivity.this.overwriteTweet(dialog, id,
                                TwiccaPluginApiUtils.getTweet(DraftListActivity.this
                                        .getIntent()));
                        break;
                    case R.id.button_load:
                        DraftListActivity.this.loadTweet(id);
                        break;
                    default:
                        break;
                }
            }
        };
        // 削除ボタン ハンドラ
        layout.findViewById(R.id.button_delete).setOnClickListener(listener);
        // 上書きボタン ハンドラ
        buttonOverwrite.setOnClickListener(listener);
        // 読み込みボタン ハンドラ
        buttonLoad.setOnClickListener(listener);

        dialog.show();

        return true;
    }

    /**
     * リスト描き出し
     */
    private void drawList() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final LinearLayout layout = (LinearLayout) this.findViewById(R.id.list_menus);

        // Log.i("mstssk", "count:" +layout.getChildCount());
        layout.removeViews(1, layout.getChildCount() - 1);

        final List<Tweet> list = this.repository.loadAll();
        for (final Tweet tweet : list) {

            final LinearLayout listitem = (LinearLayout) inflater.inflate(R.layout.listitem, null);
            final String text = tweet.getTweet();
            final Long id = tweet.getId();

            final Button button = (Button) listitem.findViewById(R.id.list_button_main);
            button.setText(text);
            button.setTag(TAG_ID, id);
            button.setOnClickListener(this);
            button.setOnLongClickListener(this);

            final ImageButton ib = (ImageButton) listitem.findViewById(R.id.list_button_submenu);
            ib.setTag(TAG_ID, id);
            ib.setOnClickListener(this);

            // TODO 新規保存ボタンの位置を先頭か末尾か選べるようにする
            layout.addView(listitem);

        }
    }

    /**
     * 渡されたツイートの空確認
     * 
     * @return
     */
    protected boolean isIntentTweetEmpty() {
        final Tweet tweet = TwiccaPluginApiUtils.getTweet(this.getIntent());
        return (tweet == null || tweet.isEmpty());
    }

    /**
     * 読み込み
     * 
     * @param id
     */
    private void loadTweet(final long id) {
        final boolean isConfLoad = this.isPrefBool(R.string.prefkey_confirm_load);
        final boolean isConfEditing = this.isPrefBool(R.string.prefkey_confirm_load_only_editing);
        final boolean isEmpty = this.isIntentTweetEmpty();
        if (isConfLoad && (!isConfEditing || (isConfEditing && !isEmpty))) {
            final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface d, final int which) {
                    DraftListActivity.this.loadTweetInternal(id);
                }
            };
            this.showConfirmDialog(R.string.label_load, listener);
        } else {
            this.loadTweetInternal(id);
        }

    }

    /**
     * ツイートをtwiccaへ読み込み
     * 
     * @param id ツイートのID
     */
    private void loadTweetInternal(final long id) {
        this.setResult(RESULT_OK, TwiccaPluginApiUtils.createIntent(this.repository.load(id)));
        this.finish();
    }

    /**
     * 上書き保存
     * 
     * @param dialog
     * @param id
     * @param tweet
     */
    private void overwriteTweet(final Dialog dialog, final long id, final Tweet tweet) {
        if (this.isPrefBool(R.string.prefkey_confirm_overwrite)) {
            final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface d, final int which) {
                    DraftListActivity.this.overwriteTweetInternal(dialog, id, tweet);
                }
            };
            this.showConfirmDialog(R.string.label_overwrite, listener);
        } else {
            this.overwriteTweetInternal(dialog, id, tweet);
        }
    }

    /**
     * ツイート上書き保存
     * 
     * @param id 上書かれるツイートのID
     * @param tweet 上書くツイート
     */
    private void overwriteTweetInternal(final Dialog dialog, final long id, final Tweet tweet) {
        tweet.setId(id);
        this.repository.update(tweet);
        this.drawList();

        if (dialog != null) {
            dialog.hide();
        }

        this.finish();

        Toast.makeText(this, R.string.msg_save_succesfully, Toast.LENGTH_SHORT).show();
    }

    /**
     * 削除
     * 
     * @param dialog
     * @param id
     */
    private void deleteTweet(final Dialog dialog, final long id) {
        if (this.isPrefBool(R.string.prefkey_confirm_delete)) {
            final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface d, final int which) {
                    DraftListActivity.this.deleteTweetInternal(dialog, id);
                }
            };
            this.showConfirmDialog(R.string.label_delete, listener);
        } else {
            this.deleteTweetInternal(dialog, id);
        }
    }

    /**
     * ツイート削除
     * 
     * @param id ツイートのID
     */
    private void deleteTweetInternal(final Dialog dialog, final long id) {
        this.repository.delete(id);
        if (this.mode.equals(Mode.LIST) && this.repository.getCount() == 0) {
            this.finish();
        } else {
            this.drawList();
        }
        dialog.hide();
    }

    /**
     * 新規保存
     * 
     * @param tweet
     */
    protected void saveTweet(final Tweet tweet) {
        this.repository.insert(tweet);
        Toast.makeText(this, R.string.msg_save_succesfully, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * ツイートが渡されていないメッセージ表示
     */
    protected void showEmptyTweetMsg() {
        Toast.makeText(this, R.string.msg_empty_tweet, Toast.LENGTH_SHORT).show();
    }

    /**
     * 確認ダイアログ表示
     * 
     * @param purpose 確認対象の文字列リソースID
     * @param onYes コールバック
     */
    private void showConfirmDialog(final int purpose, final DialogInterface.OnClickListener onYes) {
        final String msg = this.getString(R.string.text_confirm, this.getString(purpose));
        new AlertDialog.Builder(this).setMessage(msg).setPositiveButton(android.R.string.ok, onYes)
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 真偽値の設定をチェック
     * 
     * @param id 設定キーの文字列リソースID
     * @return 設定 true / false
     */
    private boolean isPrefBool(final int id) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean defValue = this.getResources().getBoolean(R.bool.pref_default);
        final boolean result = pref.getBoolean(this.getString(id), defValue);
        return result;
    }

    /**
     * 画面モードを取得
     * 
     * @return Mode
     */
    protected Mode getMode() {
        // 呼び出し時のintent-filterによって、画面モード切り替え
        if (this.getComponentName().getShortClassName()
                .equals(this.getString(R.string.activity_save))) {
            return Mode.SAVE;
        } else {
            return Mode.LIST;
        }
    }
}
