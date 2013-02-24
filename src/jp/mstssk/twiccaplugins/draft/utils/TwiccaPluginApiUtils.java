
package jp.mstssk.twiccaplugins.draft.utils;

import android.content.Intent;

import jp.mstssk.twiccaplugins.draft.Tweet;

/**
 * twiccaのプラグインAPIのためのユーティリティ
 * 
 * @author mstssk
 */
public class TwiccaPluginApiUtils {

    /** 返信先情報(オプション) */
    private static final String COLUMN_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";

    /** 緯度(オプション) */
    private static final String COLUMN_LATITUDE = "latitude";

    /** 経度(オプション) */
    private static final String COLUMN_LONGITUDE = "longitude";

    /**
     * Intentで渡されたツイートを取得
     * 
     * @return ツイート
     */
    public static Tweet getTweet(final Intent intent) {
        final Tweet tweet = new Tweet();
        tweet.setTweet(intent.getStringExtra(Intent.EXTRA_TEXT));
        tweet.setInReplyToStatusId(intent
                .getStringExtra(COLUMN_IN_REPLY_TO_STATUS_ID));
        tweet.setLatitude(intent.getStringExtra(COLUMN_LATITUDE));
        tweet.setLongitude(intent.getStringExtra(COLUMN_LONGITUDE));
        return tweet;
    }

    /**
     * twiccaに返すIntentを生成
     * 
     * @param tweet 返却するツイート
     * @return Intent result
     */
    public static Intent createIntent(final Tweet tweet) {
        final Intent result = new Intent();
        result.putExtra(Intent.EXTRA_TEXT, tweet.getTweet());
        if (tweet.getInReplyToStatusId() != null) {
            result.putExtra(COLUMN_IN_REPLY_TO_STATUS_ID, tweet.getInReplyToStatusId());
        }
        if (tweet.getLatitude() != null) {
            result.putExtra(COLUMN_LATITUDE, tweet.getLatitude());
        }
        if (tweet.getLongitude() != null) {
            result.putExtra(COLUMN_LONGITUDE, tweet.getLongitude());
        }
        return result;
    }
}
