package jp.mstssk.twiccaplugins.draft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCursor;
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

public class DraftListActivity extends Activity implements OnClickListener,
		OnLongClickListener {

	// 画面モード
	enum Mode {
		SAVE, // 保存モード
		LIST
		// 読み込みモード
	}

	static int TAG_ID = R.id.tweet_id;

	private Mode mode = Mode.SAVE;

	// private Tweet tweet;

	public DraftDB db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			// configuration変更前の状態を引き継がないようにする
			savedInstanceState.clear();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Log.i("mstssk", getComponentName().getShortClassName()); //
		// manifestでのname

		// 画面モード切り替え
		mode = getMode();

		// ツイートの空確認
		if (mode.equals(Mode.SAVE) && isIntentTweetEmpty()) {
			showEmptyTweetMsg();
			finish();
			return;
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.list_menus);
		View buttonSaveNew = layout.findViewById(R.id.button_save_as_new);

		// 読み込みモードでは新規登録ボタンを隠す
		if (mode.equals(Mode.LIST)) {
			buttonSaveNew.setVisibility(View.GONE);
		} else {
			buttonSaveNew.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					saveTweet(getIntentTweet());
				}
			});
		}

		db = new DraftDB(this);

		// 1件も保存されていない場合
		if (mode.equals(Mode.LIST) && db.getTweetCount() == 0) {
			Toast.makeText(this, getString(R.string.msg_no_draft),
					Toast.LENGTH_SHORT).show();
			finish();
		}

		drawList();
	}

	@Override
	public void onStop() {
		super.onStop();

		// DB接続解除
		if (db != null) {
			db.close();
			db = null;
		}
	}

	/**
	 * 選択時処理
	 */
	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.list_button_submenu) {
			// サブメニューボタンなら押し時処理へ
			onLongClick(view);
		} else {
			long id = (Long) view.getTag(TAG_ID);
			if (mode.equals(Mode.LIST)) {
				// ツイートをセット
				loadTweet(id);
			} else {
				// ツイートを保存
				overwriteTweet(null, id, getIntentTweet());
			}

		}
	}

	/**
	 * 長押し時処理<br>
	 * プレビューメニュー表示
	 */
	@Override
	public boolean onLongClick(View view) {

		// final LinearLayout item = (LinearLayout) view.getParent();
		final long id = (Long) view.getTag(TAG_ID);
		View layout = LayoutInflater.from(this).inflate(R.layout.dialog, null);

		// 読み込みモードでは「上書き保存」ボタンの代わりに「読み込み」ボタン表示
		View buttonOverwrite = layout.findViewById(R.id.button_overwrite);
		View buttonLoad = layout.findViewById(R.id.button_load);
		if (Mode.LIST.equals(mode)) {
			buttonOverwrite.setVisibility(View.GONE);
			buttonLoad.setVisibility(View.VISIBLE);
		} else {
			buttonOverwrite.setVisibility(View.VISIBLE);
			buttonLoad.setVisibility(View.GONE);
		}

		TextView text = (TextView) layout.findViewById(R.id.text_tweet_preview);
		text.setText(db.loadTweet(id).tweet);
		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setView(layout).create();

		// クリックリスナ
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.button_delete:
						deleteTweet(dialog, id);
						break;
					case R.id.button_overwrite:
						overwriteTweet(dialog, id, getIntentTweet());
						break;
					case R.id.button_load:
						loadTweet(id);
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
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) this.findViewById(R.id.list_menus);

		// Log.i("mstssk", "count:" +layout.getChildCount());
		layout.removeViews(1, layout.getChildCount() - 1);

		SQLiteCursor cursor = db.loadTweet(null);
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			LinearLayout listitem = (LinearLayout) inflater.inflate(
					R.layout.listitem, null);
			String text = cursor.getString(cursor
					.getColumnIndex(DraftDB.COLUMN_TWEET));
			int index = cursor.getColumnIndex(DraftDB.COLUMN_ID);
			Long id = cursor.getLong(index);

			Button button = (Button) listitem
					.findViewById(R.id.list_button_main);
			button.setText(text);
			button.setTag(TAG_ID, id);
			button.setOnClickListener(this);
			button.setOnLongClickListener(this);

			ImageButton ib = (ImageButton) listitem
					.findViewById(R.id.list_button_submenu);
			ib.setTag(TAG_ID, id);
			ib.setOnClickListener(this);

			// TODO 新規保存ボタンの位置を先頭か末尾か選べるようにする
			layout.addView(listitem);

			cursor.moveToNext();
		}
		cursor.close();

	}

	/**
	 * 渡されたツイートの空確認
	 * 
	 * @return
	 */
	boolean isIntentTweetEmpty() {
		Tweet tweet = getIntentTweet();
		return (tweet == null || tweet.isEmpty());
	}

	/**
	 * 読み込み
	 * 
	 * @param id
	 */
	void loadTweet(final long id) {
		boolean isConfLoad = isPrefBool(R.string.prefkey_confirm_load);
		boolean isConfEditing = isPrefBool(R.string.prefkey_confirm_load_only_editing);
		boolean isEmpty = isIntentTweetEmpty();
		if (isConfLoad && (!isConfEditing || (isConfEditing && !isEmpty))) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int which) {
					loadTweetInternal(id);
				}
			};
			showConfirmDialog(R.string.label_load, listener);
		} else {
			loadTweetInternal(id);
		}

	}

	/**
	 * ツイートをtwiccaへ読み込み
	 * 
	 * @param id ツイートのID
	 */
	void loadTweetInternal(long id) {
		setResult(RESULT_OK, buildResultIntent(db.loadTweet(id)));
		finish();
	}

	/**
	 * 上書き保存
	 * 
	 * @param dialog
	 * @param id
	 * @param tweet
	 */
	void overwriteTweet(final Dialog dialog, final long id, final Tweet tweet) {
		if (isPrefBool(R.string.prefkey_confirm_overwrite)) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int which) {
					overwriteTweetInternal(dialog, id, tweet);
				}
			};
			showConfirmDialog(R.string.label_overwrite, listener);
		} else {
			overwriteTweetInternal(dialog, id, tweet);
		}
	}

	/**
	 * ツイート上書き保存
	 * 
	 * @param id 上書かれるツイートのID
	 * @param tweet 上書くツイート
	 */
	void overwriteTweetInternal(Dialog dialog, long id, Tweet tweet) {
		db.saveTweet(id, tweet);
		drawList();

		if (dialog != null) {
			dialog.hide();
		}

		finish();

		Toast.makeText(this, R.string.msg_save_succesfully, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 削除
	 * 
	 * @param dialog
	 * @param id
	 */
	void deleteTweet(final Dialog dialog, final long id) {
		if (isPrefBool(R.string.prefkey_confirm_delete)) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int which) {
					deleteTweetInternal(dialog, id);
				}
			};
			showConfirmDialog(R.string.label_delete, listener);
		} else {
			deleteTweetInternal(dialog, id);
		}
	}

	/**
	 * ツイート削除
	 * 
	 * @param id ツイートのID
	 */
	void deleteTweetInternal(Dialog dialog, long id) {
		db.deleteTweet(id);
		if (mode.equals(Mode.LIST) && db.getTweetCount() == 0) {
			finish();
		} else {
			drawList();
		}
		dialog.hide();
	}

	/**
	 * 新規保存
	 * 
	 * @param tweet
	 */
	void saveTweet(Tweet tweet) {
		db.saveTweet(tweet);
		Toast.makeText(this, R.string.msg_save_succesfully, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	/**
	 * Intentで渡されたツイートを取得
	 * 
	 * @return ツイート
	 */
	protected Tweet getIntentTweet() {
		Intent intent = getIntent();
		String tweetText = intent.getStringExtra(Intent.EXTRA_TEXT);
		String in_reply_to_status_id = intent
				.getStringExtra(DraftDB.COLUMN_IN_REPLY_TO_STATUS_ID);
		String latitude = intent.getStringExtra(DraftDB.COLUMN_LATITUDE);
		String longitude = intent.getStringExtra(DraftDB.COLUMN_LONGITUDE);
		Tweet tweet = new Tweet(tweetText, in_reply_to_status_id, latitude,
				longitude);
		return tweet;
	}

	/**
	 * twiccaに返すIntentを生成
	 * 
	 * @param tweet 返却するツイート
	 * @return Intent result
	 */
	Intent buildResultIntent(Tweet tweet) {

		Intent result = new Intent();

		result.putExtra(Intent.EXTRA_TEXT, tweet.tweet);
		if (tweet.in_reply_to_status_id != null) {
			result.putExtra(DraftDB.COLUMN_IN_REPLY_TO_STATUS_ID,
					tweet.in_reply_to_status_id);
		}
		if (tweet.latitude != null) {
			result.putExtra(DraftDB.COLUMN_LATITUDE, tweet.latitude);
		}
		if (tweet.longitude != null) {
			result.putExtra(DraftDB.COLUMN_LONGITUDE, tweet.longitude);
		}

		return result;
	}

	/**
	 * ツイートが渡されていないメッセージ表示
	 */
	void showEmptyTweetMsg() {
		Toast.makeText(this, R.string.msg_empty_tweet, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 確認ダイアログ表示
	 * 
	 * @param purpose 確認対象の文字列リソースID
	 * @param onYes コールバック
	 */
	void showConfirmDialog(int purpose, DialogInterface.OnClickListener onYes) {
		String msg = getString(R.string.text_confirm, getString(purpose));
		new AlertDialog.Builder(this).setMessage(msg)
				.setPositiveButton(android.R.string.ok, onYes)
				.setNegativeButton(android.R.string.cancel, null).show();
	}

	/**
	 * 真偽値の設定をチェック
	 * 
	 * @param id 設定キーの文字列リソースID
	 * @return 設定 true / false
	 */
	boolean isPrefBool(int id) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean defValue = getResources().getBoolean(R.bool.pref_default);
		boolean result = pref.getBoolean(getString(id), defValue);
		return result;

	}

	/**
	 * 画面モードを取得
	 * 
	 * @return Mode
	 */
	protected Mode getMode() {
		// 呼び出し時のintent-filterによって、画面モード切り替え
		if (getComponentName().getShortClassName().equals(
				getString(R.string.activity_save))) {
			return Mode.SAVE;
		} else {
			return Mode.LIST;
		}
	}
}
