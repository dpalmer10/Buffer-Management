public class Frame {
    private int pinCount = 0;
    private boolean dirty = false;
    private String content;

    public Frame(String content) {
        this.content = content;
    }

    public int getPin() {
        return pinCount;
    }

    public void incPin() {
        pinCount++;
    }

    public void decPin() {
        pinCount--;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void displayPage() {
        System.out.println("\n"+ content + "\n");
    }

    public void updatePage(String toAppend) {
        content += "\n" + toAppend;
        dirty = true;
        System.out.println("Text appended\n");
    }

    public String getContent() {
        return content;
    }
}
