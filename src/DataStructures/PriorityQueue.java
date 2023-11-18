package DataStructures;

import java.util.ArrayList;
import java.util.List;

public class PriorityQueue<T extends Comparable<T>> {

    private List<T> heap;

    public PriorityQueue() {
        this.heap = new ArrayList<>();
    }

    public void enqueue(T item) {
        heap.add(item);
        heapifyUp();
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }

        T root = heap.get(0);

        // Replace the root with the last element
        int lastIndex = heap.size() - 1;
        heap.set(0, heap.get(lastIndex));
        heap.remove(lastIndex);

        // Heapify down to maintain the heap property
        heapifyDown();

        return root;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }
        return heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    private void heapifyUp() {
        int currentIndex = heap.size() - 1;

        while (hasParent(currentIndex) && getParent(currentIndex).compareTo(heap.get(currentIndex)) > 0) {
            swap(currentIndex, getParentIndex(currentIndex));
            currentIndex = getParentIndex(currentIndex);
        }
    }

    private void heapifyDown() {
        int currentIndex = 0;

        while (hasLeftChild(currentIndex)) {
            int smallerChildIndex = getLeftChildIndex(currentIndex);

            // Check if the right child is smaller (if it exists)
            if (hasRightChild(currentIndex) &&
                    getRightChild(currentIndex).compareTo(getLeftChild(currentIndex)) < 0) {
                smallerChildIndex = getRightChildIndex(currentIndex);
            }

            // Check if the current element is smaller than both children
            if (heap.get(currentIndex).compareTo(heap.get(smallerChildIndex)) < 0) {
                break;
            }

            swap(currentIndex, smallerChildIndex);
            currentIndex = smallerChildIndex;
        }
    }

    private boolean hasParent(int index) {
        return index > 0;
    }

    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    private T getParent(int index) {
        return heap.get(getParentIndex(index));
    }

    private boolean hasLeftChild(int index) {
        return getLeftChildIndex(index) < heap.size();
    }

    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    private T getLeftChild(int index) {
        return heap.get(getLeftChildIndex(index));
    }

    private boolean hasRightChild(int index) {
        return getRightChildIndex(index) < heap.size();
    }

    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    private T getRightChild(int index) {
        return heap.get(getRightChildIndex(index));
    }

    private void swap(int index1, int index2) {
        T temp = heap.get(index1);
        heap.set(index1, heap.get(index2));
        heap.set(index2, temp);
    }


}
