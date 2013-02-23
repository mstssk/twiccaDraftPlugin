
package jp.mstssk.twiccaplugins.draft;

/**
 * クイック保存
 * 
 * @author mstssk
 */
public class QuickSaveActivity extends DraftListActivity {

    @Override
    public void onStart() {
        super.onStart();
        if (!this.isFinishing()) {
            if (this.isIntentTweetEmpty()) {
                this.showEmptyTweetMsg();
            } else {
                this.saveTweet(this.getIntentTweet());
            }
            this.finish();
        }
    }

    @Override
    protected Mode getMode() {
        return Mode.SAVE;
    }

}
