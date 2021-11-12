package saravia.apps.freefirediamonds;

public class Messages {

    String message;
    String senderID;
    long timesTamp;
    String currentTime;

    public Messages() {
    }

    public Messages(String message, String senderID, long timesTamp, String currentTime) {
        this.message = message;
        this.senderID = senderID;
        this.timesTamp = timesTamp;
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderID() {
        return senderID;
    }

    public long getTimesTamp() {
        return timesTamp;
    }

    public String getCurrentTime() {
        return currentTime;
    }
}
