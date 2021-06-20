import java.util.ArrayList;

public class Buffer implements BufferInterface {
    private ArrayList<CanBusPackage> buffer = new ArrayList();

    @Override
    public synchronized void push(CanBusPackage canBusPackage) {
        buffer.add(canBusPackage);
    }

    @Override
    public synchronized CanBusPackage get() {
        if (buffer.size() > 0 && buffer.get(0) != null) {
            CanBusPackage canBusPackage = buffer.get(0);
            shiftBuffer();
            return canBusPackage;
        } else return null;
    }

    @Override
    public void clear() {
        buffer.clear();
    }

    private void shiftBuffer() {
        for (int idx = 1; idx < buffer.size(); idx++) {
            buffer.set(idx - 1, buffer.get(idx));
        }
        buffer.remove(buffer.size() - 1);
    }
}
