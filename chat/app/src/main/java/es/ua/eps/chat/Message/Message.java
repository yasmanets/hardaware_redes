package es.ua.eps.chat.Message;

public class Message {
    private String nickname;
    private String content;
    private int color;

    public Message(String nick, String message, int color) {
        this.nickname = nick;
        this.content = message;
        this.color = color;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public int getColor() {
        return this.color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
