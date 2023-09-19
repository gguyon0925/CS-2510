import tester.*;
import java.util.function.*;
class ExamplesDeque2 {
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
    t.checkExpect(this.deque1.size(), 4);
    t.checkExpect(this.deque2.size(), 5);
    
  }

  void testAddAtHead(Tester t) {
    this.initData();

    this.deque1.addAtHead("bde");
    t.checkExpect(this.deque1, this.deque2);
    t.checkExpect(this.deque1.header.next, addedNode);


  }



  void testAddAtHead2(Tester t) {
    this.initData();
   this.deque1.addAtHead("bbb");
   t.checkExpect(this.deque1.size(), 5);



    
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

  //for testing testAddAtTail
  Sentinel<String> sen5 = new Sentinel<String>();
  ANode<String> node5C = new Node<String>("Hello", sen5, sen5);
  ANode<String> node6C = new Node<String>("World", sen5, node5C);
  ANode<String> node7C = new Node<String>("Oreos", sen5, node6C);
  ANode<String> node8C = new Node<String>("Yerba Mate", sen5, node7C);
  ANode<String> node9C = new Node<String>("Goldfish", sen5, node8C);
  Deque<String> deque3WithGold = new Deque<String>(sen5);
  
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
    
    //for testing addAtHead
    // example 1
    sen3 = new Sentinel<String>();
    nodeA0 = new Node<String>("MARIO KART WII", sen3, sen3);
    deque1WithMario = new Deque<String>(sen3);
    // example 2
    sen4 = new Sentinel<String>();
    nodeB0 = new Node<String>("ZAB", sen4, sen4);
    nodeB1 = new Node<String>("ABC", sen4, nodeB0);
    nodeB2 = new Node<String>("BCD", sen4, nodeB1);
    nodeB3 = new Node<String>("CDE", sen4, nodeB2);
    nodeB4 = new Node<String>("DEF", sen4, nodeB3);
    deque2WithZAB = new Deque<String>(sen4);
    
    // for testing testAddAtTail
    sen5 = new Sentinel<String>();
    node5C = new Node<String>("Hello", sen5, sen5);
    node6C = new Node<String>("World", sen5, node5C);
    node7C = new Node<String>("Oreos", sen5, node6C);
    node8C = new Node<String>("Yerba Mate", sen5, node7C);
    node9C = new Node<String>("Goldfish", sen5, node8C);
    deque3WithGold = new Deque<String>(sen5);
  }

  void testAppend(Tester t) {
    this.reset();
    Sentinel<String> expSen = new Sentinel<String>();
    Deque<String> exp = new Deque<String>(expSen);
    sen1 = new Sentinel<String>();
    node1 = new Node<String>("ABC", expSen, expSen);
    node2 = new Node<String>("BCD", expSen, node1);
    node3 = new Node<String>("CDE", expSen, node2);
    node4 = new Node<String>("DEF", expSen, node3);
    sen2 = new Sentinel<String>();
    node5 = new Node<String>("Hello", expSen, expSen);
    node6 = new Node<String>("World", expSen, node5);
    node7 = new Node<String>("Oreos", expSen, node6);
    node8 = new Node<String>("Yerba Mate", expSen, node7);

    this.deque2.appendAtEnd(deque3);

    t.checkExpect(deque2, exp);


  }
  
  // tests the size() method
  void testSize(Tester t) {
    reset();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 4);
  }
  
  // tests the addAtHead() method
  void testAddAtHead(Tester t) {
    reset();
    
    t.checkExpect(this.deque1.size(), 0);
    this.deque1.addAtHead("MARIO KART WII");
    t.checkExpect(this.deque1, deque1WithMario);
    t.checkExpect(this.deque1.size(), 1);
    
    t.checkExpect(this.deque2.size(), 4);
    this.deque2.addAtHead("ZAB");
    t.checkExpect(this.deque2, this.deque2WithZAB);
    t.checkExpect(this.deque2.size(), 5);
    
    reset();
    
    t.checkExpect(this.deque1.size(), 0);
    this.deque1.addAtHead("DEF");
    this.deque1.addAtHead("CDE");
    this.deque1.addAtHead("BCD");
    this.deque1.addAtHead("ABC");
    t.checkExpect(this.deque1, this.deque2);
    
    reset();
  }

  // tests the addAtTail() method
  void testAddAtTail(Tester t) {
    reset();
    
    Deque<String> deque4 = new Deque<String>();
    t.checkExpect(this.deque1, deque4);
    
    this.deque1.addAtHead("any");
    deque4.addAtTail("any");
    
    t.checkExpect(this.deque1, deque4);
    
    reset();
    
    this.deque1.addAtTail("ABC");
    this.deque1.addAtTail("BCD");
    this.deque1.addAtTail("CDE");
    this.deque1.addAtTail("DEF");
    t.checkExpect(this.deque1, this.deque2);
    
    this.deque3.addAtTail("Goldfish");
    t.checkExpect(this.deque3, deque3WithGold);
    
    reset();
  }
  
  // tests removeFromHead method
  void testRMFromHead(Tester t) {
    reset();
    
    this.deque1.addAtHead("BCD");
    this.deque1.addAtTail("CDE");
    this.deque1.addAtTail("DEF");
    
    t.checkExpect(this.deque2.removeFromHead(), "ABC");
    
    reset();
    
    t.checkExpect(this.deque1WithMario.removeFromHead(), "MARIO KART WII");
    
    t.checkExpect(this.deque2WithZAB.removeFromHead(), "ZAB");
    
    try {
      this.deque1.removeFromHead();
      t.checkExpect(false, true, "Expected this method to throw an exception, and it didn't");
    } catch (Exception e) {
      t.checkExpect(e, new RuntimeException());
    }
    
    reset();
  }
  
  // tests removeFromTail method
  void testRMFromTail(Tester t) {
    reset();
    
    this.deque1.addAtTail("ABC");
    this.deque1.addAtTail("BCD");
    this.deque1.addAtTail("CDE");
    
    t.checkExpect(this.deque2.removeFromTail(), "DEF");
    
    reset();
    
    t.checkExpect(this.deque3WithGold.removeFromTail(), "Goldfish");
    
    try {
      this.deque1.removeFromTail();
      t.checkExpect(false, true, "Expected this method to throw an exception, and it didn't");
    } catch (Exception e) {
      t.checkExpect(e, new RuntimeException());
    }
    
    reset();
  }
  
  // tests find method
  void testFind(Tester t) {
    Predicate<String> pred1 = (String str) -> str.equals("BCD");
    Predicate<String> pred2 = (String str) -> str.equals("ABC");
    Predicate<String> pred3 = (String str) -> str.equals("Yerba Mate");
    
  //  t.checkExpect(this.deque2.find(pred1), this.node2);
    t.checkExpect(this.deque2.find(pred2), this.node1);
    t.checkExpect(this.deque3.find(pred3), this.node8);
    t.checkExpect(this.deque1.find(pred1), new Sentinel<String>());
  }
}