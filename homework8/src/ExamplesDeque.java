import tester.*;

class ExamplesDeque {
  ANode<String> node1;
  ANode<String> node2;
  ANode<String> node3;
  ANode<String> node4;

  ANode<String> node12;
  ANode<String> node23;
  ANode<String> node34;
  ANode<String> node45;

  ANode<String> addedNode;


  Sentinel<String> string1;
  Deque<String> deque1;

  Sentinel<String> string2;
  Deque<String> deque2;

  void initData() {

    this.string1 = new Sentinel<String>();
    this.node1 = new Node<String>("abc", string1, string1);
    this.node2 = new Node<String>("bcd", string1, node1);
    this.node3 = new Node<String>("cde", string1, node2);
    this.node4 = new Node<String>("def", string1, node3);
    this.deque1 = new Deque<String>(string1);


    this.string2 = new Sentinel<String>();
    this.addedNode = new Node<String>("bde", string2, string2);
    this.node12 = new Node<String>("abc", string2, addedNode);
    this.node23 = new Node<String>("bcd", string2, node12);
    this.node34 = new Node<String>("cde", string2, node23);
    this.node45 = new Node<String>("def", string2, node34);
    this.deque2 = new Deque<String>(string2);

   


  }

  void testSize(Tester t) {
    this.initData();

    t.checkExpect(this.string1.prev, node4);
    t.checkExpect(this.deque1.header.prev, node4);
    t.checkExpect(this.deque1.size1(), 4);
    t.checkExpect(this.deque2.size1(), 5);
    
  }

  void testAddAtHead(Tester t) {
    this.initData();

    this.deque1.addAtHead("bde");
    t.checkExpect(this.deque1, this.deque2);
    t.checkExpect(this.deque1.header.next, addedNode);


  }

  void tesRemoveFromHead(Tester t) {
    this.initData();

    this.deque1.removeFromHead();

    t.checkExpect(this.deque1.size1(), 3);
    t.checkExpect(this.deque1.header.next, node2);

  }



  // void testAddAtTail(Tester t) {
  //   this.initData();
  //  // ANode<String> exp = new Node<String>("bde", string1, node4);

  //   this.deque1.addAtTail("bde");
  //  t.checkExpect(this.deque1.header.next, node1);

    
  // }

}
