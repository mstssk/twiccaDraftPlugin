package jp.mstssk.twiccaplugins.draft;

public class Tweet {

	public Long id;
	public String tweet;
	public String in_reply_to_status_id;
	public String latitude;
	public String longitude;

	public Tweet(String tweet, String in_reply_to_status_id, String latitude,
			String longitude) {
		init(tweet, in_reply_to_status_id, latitude, longitude);

	}

	public Tweet(long id, String tweet, String in_reply_to_status_id,
			String latitude, String longitude) {
		init(tweet, in_reply_to_status_id, latitude, longitude);
		this.id = id;
	}

	private void init(String tweet, String in_reply_to_status_id,
			String latitude, String longitude) {
		this.tweet = tweet;
		this.in_reply_to_status_id = in_reply_to_status_id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append(" Tweet{");

		if (tweet != null) {
			buf.append("tweet:");
			buf.append(tweet);
			buf.append(", ");
		}
		if (in_reply_to_status_id != null) {
			buf.append("in_reply_to_status_id:");
			buf.append(in_reply_to_status_id);
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
		if (_isEmpty(tweet) && _isEmpty(in_reply_to_status_id)
				&& _isEmpty(latitude) && _isEmpty(longitude)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean _isEmpty(String target) {
		if (target == null || target.length() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
