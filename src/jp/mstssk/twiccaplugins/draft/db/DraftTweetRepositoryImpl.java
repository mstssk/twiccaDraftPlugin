
package jp.mstssk.twiccaplugins.draft.db;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;

import java.util.List;

import jp.mstssk.twiccaplugins.draft.Tweet;

/**
 * 下書き保存先 実装
 * 
 * @author mstssk
 */
public class DraftTweetRepositoryImpl implements DraftTweetRepository {

    private final DraftDB db;

    public DraftTweetRepositoryImpl(final Context context) {
        this.db = new DraftDB(context);
    }

    @Override
    public long insert(final Tweet tweet) {
        return this.db.saveTweet(tweet);
    }

    @Override
    public long update(final Tweet tweet) {
        return this.db.saveTweet(tweet.getId(), tweet);
    }

    @Override
    public Tweet load(final long rowId) {
        return this.db.loadTweet(rowId);
    }

    @Override
    public long delete(final long rowId) {
        return this.db.deleteTweet(rowId);
    }

    @Override
    public List<Tweet> loadAll() {
        return this.db.loadAll();
    }

    @Override
    public long getCount() {
        final SQLiteCursor cursor = this.db.loadTweet(null);
        final int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            this.db.close();
        }
    }

}
