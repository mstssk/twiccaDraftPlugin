
package jp.mstssk.twiccaplugins.draft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB sqlite table: draft<br>
 * 1. id<br>
 * 2. tweet<br>
 * 3. in_reply_to_status_id<br>
 * 4. latitude<br>
 * 5. longitude<br>
 * 
 * @author mstssk
 */
public class DraftDB extends SQLiteOpenHelper {

    private static final String DB = "tweet_draft.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "draft";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_TWEET = "tweet";
    static final String COLUMN_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    static final String COLUMN_LATITUDE = "latitude";
    static final String COLUMN_LONGITUDE = "longitude";
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " + COLUMN_ID
            + " integer primary key, " + COLUMN_TWEET + " text, " + COLUMN_IN_REPLY_TO_STATUS_ID
            + " text, "
            + COLUMN_LATITUDE + " text, " + COLUMN_LONGITUDE + " text );";
    private static final String DROP_TABLE = "drop table " + TABLE_NAME + ";";

    public DraftDB(final Context context) {
        super(context, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO データ移行
        db.execSQL(DROP_TABLE);
        this.onCreate(db);
    }

    /* utility methods */

    /**
     * save tweet
     * 
     * @param tweet
     * @param in_reply_to_status_id
     * @param latitude
     * @param longitude
     * @return row id
     */
    public long saveTweet(final String tweet, final String in_reply_to_status_id,
            final String latitude,
            final String longitude) {

        final ContentValues value = new ContentValues();
        value.put(COLUMN_TWEET, tweet);
        value.put(COLUMN_IN_REPLY_TO_STATUS_ID, in_reply_to_status_id);
        value.put(COLUMN_LATITUDE, latitude);
        value.put(COLUMN_LONGITUDE, longitude);
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLE_NAME, null, value);
    }

    /**
     * save tweet(overwrite)
     * 
     * @param id
     * @param tweet
     * @param in_reply_to_status_id
     * @param latitude
     * @param longitude
     * @return row id
     */
    public long saveTweet(final long id, final String tweet, final String in_reply_to_status_id,
            final String latitude,
            final String longitude) {

        final ContentValues value = new ContentValues();
        value.put(COLUMN_TWEET, tweet);
        value.put(COLUMN_IN_REPLY_TO_STATUS_ID, in_reply_to_status_id);
        value.put(COLUMN_LATITUDE, latitude);
        value.put(COLUMN_LONGITUDE, longitude);
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_NAME, value, COLUMN_ID + "=?", new String[] {
            Long.toString(id)
        });
    }

    public long saveTweet(final Tweet tweet) {
        return this
                .saveTweet(tweet.getTweet(), tweet.getInReplyToStatusId(), tweet.getLatitude(),
                        tweet.getLongitude());
    }

    public long saveTweet(final long id, final Tweet tweet) {
        return this.saveTweet(id, tweet.getTweet(), tweet.getInReplyToStatusId(),
                tweet.getLatitude(),
                tweet.getLongitude());
    }

    public SQLiteCursor loadTweet(final Long id) {
        String selection = null;
        if (id != null) {
            selection = COLUMN_ID + " = " + id.toString();
        }

        final SQLiteDatabase db = this.getReadableDatabase();
        return (SQLiteCursor) db.query(TABLE_NAME, new String[] {
                COLUMN_ID, COLUMN_TWEET,
                COLUMN_IN_REPLY_TO_STATUS_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE
        }, selection, null, null, null, null);
    }

    public Tweet loadTweet(final long id) {
        final Cursor cursor = this.loadTweet(Long.valueOf(id));
        final int index_rowid = cursor.getColumnIndex(COLUMN_ID);
        final int index_tweet = cursor.getColumnIndex(COLUMN_TWEET);
        final int index_in_reply_to_status_id = cursor.getColumnIndex(COLUMN_IN_REPLY_TO_STATUS_ID);
        final int index_latitude = cursor.getColumnIndex(COLUMN_LATITUDE);
        final int index_longitude = cursor.getColumnIndex(COLUMN_LONGITUDE);
        cursor.moveToFirst();
        return new Tweet(cursor.getLong(index_rowid), cursor.getString(index_tweet),
                cursor.getString(index_in_reply_to_status_id), cursor.getString(index_latitude),
                cursor.getString(index_longitude));
    }

    public long deleteTweet(final long id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[] {
            Long.toString(id)
        });
    }

    public int getTweetCount() {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[] {
            COLUMN_ID
        }, null, null, null, null, null).getCount();
    }
}
