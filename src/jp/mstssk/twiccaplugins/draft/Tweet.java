
package jp.mstssk.twiccaplugins.draft;

/**
 * 下書きツイート
 * 
 * @author mstssk
 */
public class Tweet {

    private Long id;
    private String tweet;
    private String inReplyToStatusId;
    private String latitude;
    private String longitude;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTweet() {
        return this.tweet;
    }

    public void setTweet(final String tweet) {
        this.tweet = tweet;
    }

    public String getInReplyToStatusId() {
        return this.inReplyToStatusId;
    }

    public void setInReplyToStatusId(final String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" Tweet{");

        if (this.id != null) {
            buf.append("id:");
            buf.append(this.id);
            buf.append(", ");
        }
        if (this.tweet != null) {
            buf.append("tweet:");
            buf.append(this.tweet);
            buf.append(", ");
        }
        if (this.inReplyToStatusId != null) {
            buf.append("in_reply_to_status_id:");
            buf.append(this.inReplyToStatusId);
            buf.append(", ");
        }
        if (this.latitude != null) {
            buf.append("latitude:");
            buf.append(this.latitude);
            buf.append(", ");
        }
        if (this.longitude != null) {
            buf.append("longitude:");
            buf.append(this.longitude);
            buf.append(", ");
        }
        buf.append("}");

        return buf.toString();
    }

    public boolean isEmpty() {
        return isEmpty(this.tweet) && isEmpty(this.inReplyToStatusId) && isEmpty(this.latitude)
                && isEmpty(this.longitude);
    }

    private static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

}
