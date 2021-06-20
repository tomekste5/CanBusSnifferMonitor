public interface BufferInterface {
    void push(CanBusPackage canBusPackage);

    CanBusPackage get();
    void clear();
}
