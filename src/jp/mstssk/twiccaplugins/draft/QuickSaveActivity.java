package jp.mstssk.twiccaplugins.draft;

/**
 * クイック保存
 * 
 * @author mstssk
 * 
 */
public class QuickSaveActivity extends DraftListActivity {

    @Override
    public void onStart() {
        super.onStart();
        if (!isFinishing()) {
            if (isIntentTweetEmpty()) {
                showEmptyTweetMsg();
            } else {
                saveTweet(getIntentTweet());
            }
            finish();
        }
    }

    @Override
    protected Mode getMode() {
        return Mode.SAVE;
    }

}
