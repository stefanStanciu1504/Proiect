package pro.xstore.api.sync.gui;

public interface Observer {
    public void update();

    public void setSubject(Subject sub);
}
