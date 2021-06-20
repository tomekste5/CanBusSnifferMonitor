public class CanBusPackage {
    private int mode;
    private long id;
    private int size;
    private byte[] data;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }


    CanBusPackage(int mode, long id, int size, byte[] data, long timestamp) {
        this.mode = mode;
        this.id = id;
        this.size = size;
        this.data = data;
        this.timestamp = timestamp;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
