import java.util.function.*;
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  int size() {
    return this.header.size();
  }

  void addAtHead(T value) {
    this.header.addAtHead(value);
  }

  void addAtTail(T value) {
    this.header.addAtTail(value);
  }

  // removes the first node from this Deque
  T removeFromHead() {
    return this.header.removeFirst();
  }
  
  // removes the last node from this Deque
  T removeFromTail() {
    return this.header.removeLast();
  }
  
  // takes a Predicate<T> and produces the first node in this Deque for which the 
  // given predicate returns true
  ANode<T> find(Predicate<T> pred) {
    return this.header.find(pred);
  }

    // append a given deque to the end of this deque
    void appendAtEnd(Deque<T> other) {
      int ind = other.size();
      while (ind > 0) {
        T values = other.removeFromHead();
        ind = other.size() - 1;
        this.addAtTail(values);
      }
    }

}

/*
 * Design the method addAtHead for the class Deque that consumes 
 * a value of type T and inserts it at the front of the list. 
 * Be sure to fix up all the links correctly!

Design the method addAtTail for the class 
Deque that consumes a value of type T 
and inserts it at the tail of this list. 
Again, be sure to fix up all the links correctly!

Design the method removeFromHead for the class Deque that 
removes the first node from this Deque. Throw a RuntimeException if an attempt is made 
to remove from an empty list. Be sure to fix up all the links correctly! 
As with ArrayList’s remove method, return the item that’s been removed from the list.

Design the method removeFromTail for the class Deque that removes the last node from this 
Deque, analogous to removeFromHead above. Again, be sure to fix up all the links correctly!
 */




abstract class ANode<T>{
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  boolean isSent() {
    return true;
  }

  int size() {
    int count = 0;
    ANode<T> current = this.next;
    while (!(current.isSent())) {
      count++;
      current = current.next;
    }
    return count;
  }

  int size1() {
    int count = 0;
    ANode<T> current = this.next;
    if (!current.isSent()) {
      count = count + 1;
      current = current.next;
      return current.size1() + count;
    }
    else {
      current = current.next;
      return count;
    }
  }

  // if (this instanceof Node) { remove this node } else { throw exception }
  abstract T removeNode();

  abstract ANode<T> findHelp(Predicate<T> pred);


}

class Sentinel<T> extends ANode<T> {

  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }

  void addAtHead(T value) {
    new Node<T>(value, this.next, this);
  }

  void addAtTail(T value) {
    new Node<T> (value, this, this.prev);
  }

  // removes the first node in the list, if there are no nodes throws exception
  T removeFirst() {
    return this.next.removeNode();
  }
  
  T removeLast() {
    return this.prev.removeNode();
  }

  // throws exception if there are no nodes in list
  T removeNode() {
    throw new RuntimeException();
  }

  // takes an Predicate<T> and returns the first node for which the given predicate returns true
  ANode<T> find(Predicate<T> pred) {
    return this.next.findHelp(pred);
  }
  
  // returns this (the sentinel) if the predicate never returned true
  ANode<T> findHelp(Predicate<T> pred) {
    return this;
  }
}





class Node<T> extends ANode<T> {
  T data;

  //constructors 
  Node(T data) {
    super(null, null);
    this.data = data;
  }
  
  Node(T data, ANode<T> next, ANode<T> prev) {
    // initialize data
    super(next, prev);
    this.data = data;
    
    // update the references of the next and previous items only if they are not null
    if (next != null) {
      this.next.prev = this;
    }
    if (prev != null) {
      this.prev.next = this;
    }
    
    // check if exception must be thrown and do so
    if (next == null || prev == null) {
      throw new IllegalArgumentException("next or prev is null");
    }
  }

  public boolean isSent() {
    return false;
  }

  T removeNode() {
    this.next = this.prev.next;
    this.prev = this.next.prev;
    return this.data;
  }
  ANode<T> findHelp(Predicate<T> pred) {
    if (pred.test(data)) {
      return this;
    }
    return this.next.findHelp(pred);
  }


}





