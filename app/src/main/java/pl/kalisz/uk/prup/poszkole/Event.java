package pl.kalisz.uk.prup.poszkole;

public class Event {

    private String from;
    private String name;
    private String subject;
    private String to;
    private boolean cancelled;

    public Event() {
    }

    public Event(String from, String name, String subject, String to, boolean cancelled) {
        this.from = from;
        this.name = name;
        this.subject = subject;
        this.to = to;
        this.cancelled = cancelled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
