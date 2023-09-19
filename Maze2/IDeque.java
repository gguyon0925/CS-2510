import java.util.ArrayList;
import java.util.Comparator;
import tester.*;

// represents a Deque used for Maze game
 interface IDeque<T> {
  // returns the element at the head of the Deque
  T head();

  // returns and removes the element at the head of the Deque
  T removeHead();

  // adds the given element to the head of the Deque
  T addAtHead(T t);

  // returns true if the Deque is empty
  boolean isEmpty();
}

// an ArrayList representing a queue structure
class Queue<T> implements IDeque<T> {
  ArrayList<T> items;

  // Constructor
  Queue(ArrayList<T> items) {
    this.items = items;
  }

  // Empty Constructor
  Queue() {
    this.items = new ArrayList<T>();
  }

  // returns the first (front) item in a Queue
  public T head() {
    if (this.isEmpty()) {
      throw new NullPointerException("Queue is Empty");
    }
    else {
      return this.items.get(0);
    }
  }

  // returns the first (front) item in a Queue and removes it from the Queue
  public T removeHead() {
    if (this.isEmpty()) {
      throw new NullPointerException("Queue is Empty");
    }
    else {
      return this.items.remove(0);
    }
  }

  // adds the given item to the end of the queue
  public T addAtHead(T t) {
    this.items.add(0, t);
    return t;
  }

  // returns whether or not this Queue is empty
  public boolean isEmpty() {
    return this.items.size() == 0;
  }
}

// represents a Stack
class Stack<T> implements IDeque<T> {
  ArrayList<T> items;

  // constructor
  Stack(ArrayList<T> items) {
    this.items = items;
  }

  // empty constructor
  Stack() {
    this.items = new ArrayList<>();
  }

  // returns the first element, or throws an error if the stack is empty
  public T head() {
    if (this.isEmpty()) {
      throw new NullPointerException("No items in list.");
    }
    else {
      return items.get(0);
    }
  }

  // returns and removes the first element, or throws an error if the stack is empty
  public T removeHead() {
    if (this.isEmpty()) {
      throw new NullPointerException("No items in list.");
    }
    else {
      return items.remove(0);
    }
  }

  // adds the given element to the front of the list
  public T addAtHead(T t) {
    items.add(0, t);
    return t;
  }

  // determines if the stack is empty
  public boolean isEmpty() {
    return items.size() == 0;
  }
}

// represents a comparator for edges
class EdgeComparator implements Comparator<Edge> {
  // compares two edges
  public int compare(Edge e1, Edge e2) {
    return e1.compareWeights(e2);
  }
}


class TestDeque {
  Queue<Integer> queue;
  Stack<Integer> stack;
  Edge edge1;
  Edge edge2;
  EdgeComparator comparator;

  void initData() {
    queue = new Queue<>();
    stack = new Stack<>();
    edge1 = new Edge(new Vertex(0, 0), new Vertex(1, 1), 2);
    edge2 = new Edge(new Vertex(0, 0), new Vertex(1, 1), 3);
    comparator = new EdgeComparator();
  }

  // Test the head method of Queue
  void testQueueHead(Tester t) {
    initData();

    queue.addAtHead(1);
    queue.addAtHead(2);
    queue.addAtHead(3);

    t.checkExpect(queue.head(), 3);
  }

  // Test the removeHead method of Queue
  void testQueueRemoveHead(Tester t) {
    initData();

    queue.addAtHead(1);
    queue.addAtHead(2);
    queue.addAtHead(3);

    t.checkExpect(queue.removeHead(), 3);
    t.checkExpect(queue.removeHead(), 2);
    t.checkExpect(queue.removeHead(), 1);
    t.checkException(new NullPointerException("Queue is Empty"), queue, "removeHead");
  }

  // Test the isEmpty method of Queue
  void testQueueIsEmpty(Tester t) {
    initData();

    t.checkExpect(queue.isEmpty(), true);
    queue.addAtHead(1);
    t.checkExpect(queue.isEmpty(), false);
  }

  // Test the head method of Stack
  void testStackHead(Tester t) {
    initData();

    stack.addAtHead(1);
    stack.addAtHead(2);
    stack.addAtHead(3);

    t.checkExpect(stack.head(), 3);
  }

  // Test the removeHead method of Stack
  void testStackRemoveHead(Tester t) {
    initData();

    stack.addAtHead(1);
    stack.addAtHead(2);
    stack.addAtHead(3);

    t.checkExpect(stack.removeHead(), 3);
    t.checkExpect(stack.removeHead(), 2);
    t.checkExpect(stack.removeHead(), 1);
    t.checkException(new NullPointerException("No items in list."), stack, "removeHead");
  }

  // Test the isEmpty method of Stack
  void testStackIsEmpty(Tester t) {
    initData();

    t.checkExpect(stack.isEmpty(), true);
    stack.addAtHead(1);
    t.checkExpect(stack.isEmpty(), false);
  }

  // Test the compare method of EdgeComparator
  void testEdgeComparator(Tester t) {
    initData();

    t.checkExpect(comparator.compare(edge1, edge2), -1);
    t.checkExpect(comparator.compare(edge2, edge1), 1);
    t.checkExpect(comparator.compare(edge1, edge1), 0);
  }
}

