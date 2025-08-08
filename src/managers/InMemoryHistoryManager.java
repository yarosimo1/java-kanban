package managers;

import nodes.Node;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static Map<Integer, Node<Task>> history = new HashMap<>();
    private static Node<Task> head;
    private static Node<Task> tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = history.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    public void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        history.remove(node.data.getId());
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;

        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }

        return tasks;
    }
}
