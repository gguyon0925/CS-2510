class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  int size1() {
    return this.header.size();
  }

  void addAtHead(T value) {
    this.header.addAtHead(value);
  }

  void addAtTail(T value) {
    this.header.addAtTail(value);
  }

  void removeFromHead() {
    this.header.removeFromHead();
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

  void removeFromHead() {
    this.next = this.prev.next;
    this.prev = this.next.prev;
    
  }



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


}







