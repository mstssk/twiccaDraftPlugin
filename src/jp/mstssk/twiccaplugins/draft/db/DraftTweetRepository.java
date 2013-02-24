
package jp.mstssk.twiccaplugins.draft.db;

import java.util.List;

import jp.mstssk.twiccaplugins.draft.Tweet;

/**
 * 下書き保存先
 * 
 * @author mstssk
 */
public interface DraftTweetRepository {

    /**
     * 新規保存
     * 
     * @param tweet
     * @return 保存されたツイートのrowId
     */
    long insert(Tweet tweet);

    /**
     * 更新
     * 
     * @param tweet
     * @return 更新された件数。常に1が返る。
     */
    long update(Tweet tweet);

    /**
     * 読み込み
     * 
     * @param rowId
     * @return 読み込んだ下書きツイート
     */
    Tweet load(long rowId);

    /**
     * 削除
     * 
     * @param rowId
     * @return 削除された件数。常に1が返る。
     */
    long delete(long rowId);

    /**
     * 全件取得
     * 
     * @return
     */
    List<Tweet> loadAll();

    /**
     * 全件数取得
     * 
     * @return
     */
    long getCount();
}
