package brotherdetjr.gama;

public interface Homunculus {
    Object poll(Perception perception);
    <T> void handle(T command);
}
