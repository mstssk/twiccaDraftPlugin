package jp.mstssk.twiccaplugins.draft;

public class Tweet {

    private Long id;
    private String tweet;
    private String inReplyToStatusId;
    private String latitude;
    private String longitude;

    public Tweet(long id, String tweet, String in_reply_to_status_id, String latitude, String longitude) {
        this(tweet, in_reply_to_status_id, latitude, longitude);
        this.setId(id);
    }

    public Tweet(String tweet, String inReplyToStatusId, String latitude, String longitude) {
        this.setTweet(tweet);
        this.setInReplyToStatusId(inReplyToStatusId);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(" Tweet{");

        if (id != null) {
            buf.append("id:");
            buf.append(id);
            buf.append(", ");
        }
        if (tweet != null) {
            buf.append("tweet:");
            buf.append(tweet);
            buf.append(", ");
        }
        if (inReplyToStatusId != null) {
            buf.append("in_reply_to_status_id:");
            buf.append(inReplyToStatusId);
            buf.append(", ");
        }
        if (latitude != null) {
            buf.append("latitude:");
            buf.append(latitude);
            buf.append(", ");
        }
        if (longitude != null) {
            buf.append("longitude:");
            buf.append(longitude);
            buf.append(", ");
        }
        buf.append("}");

        return buf.toString();
    }

    public boolean isEmpty() {
        return isEmpty(tweet) && isEmpty(inReplyToStatusId) && isEmpty(latitude) && isEmpty(longitude);
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
