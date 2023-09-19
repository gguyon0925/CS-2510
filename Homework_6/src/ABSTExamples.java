import java.util.Comparator;
import tester.*;

class ABSTExamples {

  // Book examples
  Book b1 = new Book("HA1", "JA Rowling", 100);
  Book b2 = new Book("HB2", "JB Rowling", 110);
  Book b3 = new Book("HC3", "JC Rowling", 120);
  Book b4 = new Book("HD", "JD Rowling", 130);
  Book b5 = new Book("HE", "JE Rowling", 140);
  Book b6 = new Book("HF", "JF Rowling", 150);
  Book b7 = new Book("HG", "JG Rowling", 160);
  Book b8 = new Book("HH", "JH Rowling", 170);

  // comparator examples

  Comparator<Book> bytitle = new BooksByTitle();
  Comparator<Book> byauthor = new BooksByAuthor();
  Comparator<Book> byprice = new BooksByPrice();

  // examples of binary search trees 
  ABST<Book> mtauth = new Leaf<Book>(byauthor);
  ABST<Book> mttitle = new Leaf<Book>(bytitle);
  ABST<Book> mtprice = new Leaf<Book>(byprice);

  ABST<Book> binauth = new Node<Book>(byauthor, b4,
      new Node<Book>(byauthor, b3, new Node<Book>(byauthor, b2, mtauth, mtauth), mtauth),
      new Node<Book>(byauthor, b5, mtauth, new Node<Book>(byauthor, b6, mtauth, mtauth)));

  ABST<Book> bintitle = new Node<Book>(bytitle, b4,
      new Node<Book>(bytitle, b3, new Node<Book>(bytitle, b2, mttitle, mttitle), mttitle),
      new Node<Book>(bytitle, b5, mttitle, new Node<Book>(bytitle, b6, mttitle, mttitle)));

      
  ABST<Book> binprice = new Node<Book>(byprice, b4,
      new Node<Book>(byprice, b3, 
      new Node<Book>(byprice, b2, mtprice, mtprice), mtprice),
      new Node<Book>(byprice, b5, mtprice, 
      new Node<Book>(byprice, b6, mtprice, mtprice)));

      ABST<Book> binprice4 = new Node<Book>(byprice, b3,
      new Node<Book>(byprice, b2, mtprice, mtprice),
      new Node<Book>(byprice, b4, mtprice,
      new Node<Book>(byprice, b5, mtprice, 
      new Node<Book>(byprice, b6, mtprice, mtprice))));

  ABST<Book> binprice2 = new Node<Book>(byprice, b4,
      new Node<Book>(byprice, b3, new Node<Book>(byprice, b2, mtprice, mtprice), mtprice),
      new Node<Book>(byprice, b5, mtprice,
          new Node<Book>(byprice, b6, mtprice, new Node<Book>(byprice, b7, mtprice, mtprice))));

  
ABST<Book> binprice3 = new Node<Book>(byprice, b4,
new Node<Book>(byprice, b3, 
mtprice, mtprice),
 new Node<Book>(byprice, b5, mtprice,         
  new Node<Book>(byprice, b6, mtprice, mtprice)));


  void testSameData(Tester t) {
    t.checkExpect(binprice.sameData(binprice4), true);
    t.checkExpect(binprice.sameData(binprice2), false);
  }

  boolean testAuthor(Tester t) {
    return t.checkExpect(this.byauthor.compare(b4, b3), 1)
        && t.checkExpect(this.byauthor.compare(b4, b5), -1);
  }

  boolean testInsert(Tester t) {
    return t.checkExpect(this.binprice.insert(b7), binprice2) &&
     t.checkExpect(this.binprice.present(b8), false) &&
     t.checkExpect(this.binprice.present(b6), true) &&
     t.checkExpect(this.binprice.present(b4), true);
  }

  void testLength(Tester t) {
    t.checkExpect(this.binprice.treeLength(), 4);
    t.checkExpect(this.binprice.leftLength(), 2);
  }

  void testGetLeftMost(Tester t) {
    t.checkExpect(this.binprice.getLeftMost2(), b2);
  }

  void testGetRightMost(Tester t) {
    t.checkExpect(this.binprice.getRight(), binprice3);
  }

  void testSameTree(Tester t) {
    t.checkExpect(this.binauth.sameTree(binauth), true);
    t.checkExpect(this.binauth.sameTree(binprice), false);
  }

}
