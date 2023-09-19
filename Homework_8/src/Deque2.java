import tester.Tester;

class Deque<T> {
  Sentinel<T> header;
  
  // constructors 
  Deque() {
    this.header = new Sentinel<T>();
  }
  Deque(Sentinel<T> header){
    this.header = header;
  }
  
  // counts the number of nodes in a list Deque
  int size() {
    return this.header.size();
  }
  
  // consumes a value of type T and inserts it at the front of the list
  void addAtHead(T value) {
    this.header.addAtHead(value);
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  //constructor
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  //counts the number of nodes in a list starting from this node
  int size() {
    int count = 0;
    ANode<T> current = this.next;
    while (!(current instanceof Sentinel)) {
      count++;
      current = current.next;
    }
    return count;
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
    // update the references of the next and previous items
    this.next.prev = this;
    this.prev.next = this;
    
    // check if exception must be thrown and do so
    if (next == null || prev == null) {
      throw new IllegalArgumentException("next or prev is null");
    }
  }
}

class Sentinel<T> extends ANode<T> {

  //constructors
  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }
  
  // consumes a value of type T and inserts it at the front of the list
  void addAtHead(T value) {
    new Node<T>(value, this, this.next);
  }

}

class ExamplesDeque {
  Sentinel<String> sen1;
  Sentinel<String> sen2;
  ANode<String> node1;
  ANode<String> node2;
  ANode<String> node3;
  ANode<String> node4;
  ANode<String> node5;
  ANode<String> node6;
  ANode<String> node7;
  ANode<String> node8;

  // examples
  Deque<String> deque1;
  Deque<String> deque2;
  Deque<String> deque3;
  
  void reset() {
    //for example 2
    sen1 = new Sentinel<String>();
    node1 = new Node<String>("ABC", sen1, sen1);
    node2 = new Node<String>("BCD", sen1, node1);
    node3 = new Node<String>("CDE", sen1, node2);
    node4 = new Node<String>("DEF", sen1, node3);

    // for example 3
    sen2 = new Sentinel<String>();
    node5 = new Node<String>("Hello", sen2, sen2);
    node6 = new Node<String>("World", sen2, node5);
    node7 = new Node<String>("Oreos", sen2, node6);
    node8 = new Node<String>("Yerba Mate", sen2, node7);

    // examples
    deque1 = new Deque<String>();
    deque2 = new Deque<String>(sen1);
    deque3 = new Deque<String>(sen2);
  }
  
  // tests the size() method
  void testSize(Tester t) {
    reset();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 4);
  }
  
  //for testing addAtHead
  // example 1
  Sentinel<String> sen3 = new Sentinel<String>();
  ANode<String> nodeA0 = new Node<String>("MARIO KART WII", sen3, sen3);
  Deque<String> deque1WithMario = new Deque<String>(sen3);
  // example 2
  Sentinel<String> sen4 = new Sentinel<String>();
  ANode<String> nodeB0 = new Node<String>("ZAB", sen4, sen4);
  ANode<String> nodeB1 = new Node<String>("ABC", sen4, nodeB0);
  ANode<String> nodeB2 = new Node<String>("BCD", sen4, nodeB1);
  ANode<String> nodeB3 = new Node<String>("CDE", sen4, nodeB2);
  ANode<String> nodeB4 = new Node<String>("DEF", sen4, nodeB3);
  Deque<String> deque2WithZAB = new Deque<String>(sen4);
  
  // tests the addAtHead() method
  void testAddAtHead(Tester t) {
    reset();
    
    t.checkExpect(this.deque1.size(), 0);
    this.deque1.addAtHead("MARIO KART WII");
    t.checkExpect(this.deque1, deque1WithMario);
    t.checkExpect(this.deque1.size(), 1);
    /*
    t.checkExpect(this.deque2.size(), 4);
    this.deque2.addAtHead("ZAB");
    t.checkExpect(this.deque2, this.deque2WithZAB);
    t.checkExpect(this.deque2.size(), 5);
    */
    reset();
    
    t.checkExpect(this.deque1.size(), 0);
    this.deque1.addAtHead("DEF");
    this.deque1.addAtHead("CDE");
    this.deque1.addAtHead("BCD");
    this.deque1.addAtHead("ABC");
    t.checkExpect(this.deque1, this.deque2);
  }
}