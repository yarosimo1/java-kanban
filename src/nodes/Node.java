package nodes;

public class Node<T> {
    public T data;
    public Node<T> prev;
    public Node<T> next;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }
}
