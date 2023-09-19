import java.util.Comparator;

class Book {
  String title;
  String author;
  int price;

  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }
}

class BooksByTitle implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.title.compareTo(b2.title);
  }
}

class BooksByAuthor implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.author.compareTo(b2.author);
  }
}

class BooksByPrice implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.price - b2.price;
  }
}

abstract class ABST<T> {
  Comparator<T> order;
  ABST(Comparator<T> order) {
    this.order = order;
  }

  public abstract ABST<T> insert(T t);
  public abstract boolean present(T t);
  public abstract int treeLength();
  public abstract int lengthHelp();
  public abstract int leftLength();
  public abstract int leftLengthHelp();
  public abstract T getLeftMost();
  public abstract T getHelp(int i);
  public abstract T getLeftMost2();
  public abstract T getLeftMostHelper(T t);
  public abstract ABST<T> getRight();
  public abstract ABST<T> getRightHelper(T t);
  public abstract boolean sameTree(ABST<T> tree2);
  public abstract boolean sameTreeHelper(Node<T> tree);
  public abstract boolean sameData(ABST<T> tree);
  public abstract boolean sameDataHelper(ABST<T> treeData);

  public abstract IList<T> buildList();


}

class Leaf<T> extends ABST<T> {
  Leaf(Comparator<T> order) {
    super(order);
  }

  public ABST<T> insert(T t) {
    return new Node<T>(order, t, new Leaf<T>(this.order), new Leaf<T>(this.order));
  }

  public boolean present(T t) {
    return false;
  }

  public int treeLength() {
    return 0;
  }

  public int lengthHelp() {
    return 0;
  }

  public int leftLength() {
    return 0;
  }

  public int leftLengthHelp() {
    return 0;
  }

  public T getLeftMost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  public T getHelp(int i) {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  public T getLeftMost2() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  public T getLeftMostHelper(T t) {
    return t;
  }

  public ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  public ABST<T> getRightHelper(T t) {
    return this;
  }

  public boolean sameTree(ABST<T> tree2) {
    return true;
  }

  public boolean sameTreeHelper(Node<T> tree) {
    return true;
  }

  public boolean sameData(ABST<T> tree) {
    return true;
  }


  public boolean sameDataHelper(ABST<T> treeData) {
    return true;
  }

  public IList<T> buildList() {
    return new Empty<T>();
  }
}

class Node<T> extends ABST<T>{
  T data;
  ABST<T> left;
  ABST<T> right;

  Node(Comparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right; 
  }

  public ABST<T> insert(T t) {
    if (this.order.compare(t, data) >= 0) {
      return new Node<T>(this.order, this.data, this.left, this.right.insert(t));
    } else {
      return new Node<T> (this.order, this.data, this.left.insert(t), this.right);
    }
  }

  public boolean present(T t) {
    return this.order.compare(data, t) == 0 ||
    this.left.present(t) || this.right.present(t);
  }

  public int treeLength() {
    return this.left.lengthHelp() + this.right.lengthHelp();
  }

  public int lengthHelp() {
    return 1 + this.left.lengthHelp() + this.right.lengthHelp();
  }

  public int leftLength() {
    return this.left.leftLengthHelp();
  }

  public int leftLengthHelp() {
    return 1 + this.left.leftLengthHelp();
  }

  public T getLeftMost() {
    int length = this.left.leftLength(); 
    return this.left.getHelp(length - 1);
  }

  public T getHelp(int i) {
    if (i >= 0) {
      return this.left.getHelp(i - 1);
    } else {
      return this.data;
    }
  }

  public T getLeftMost2() {
    return this.getLeftMostHelper(this.data);
  }

  public T getLeftMostHelper(T t) {
    return this.left.getLeftMostHelper(this.data);
  }

  public ABST<T> getRight() {
    return this.getRightHelper(this.getLeftMost2());
  }

  public ABST<T> getRightHelper(T t) {
     if (order.compare(t, this.data) == 0) {
      return this.right;
     } else {
      return new Node<T>(this.order, this.data, this.left.getRightHelper(t), this.right.getRightHelper(t));
     }
  } 

  public boolean sameTree(ABST<T> tree2) {
    return tree2.sameTreeHelper(this);
  }

  public boolean sameTreeHelper(Node<T> tree) {
    return this.order == tree.order && this.order.compare(this.data, tree.data) == 0 &&
    this.left.sameTree(tree.left) && this.right.sameTree(tree.right);
  }


  /*
   * bstA:       bstB:       bstC:       bstD:
     b3          b3          b2          b3
    /  \        /  \        /  \        /  \
   b2  b4      b2  b4      b1   b4     b1   b4
  /           /                /             \
b1           b1               b3              b5

the following should hold:
bstA is the sameTree as bstB

bstA is not the sameTree as bstC

bstA is not the sameTree as bstD

bstA has the sameData as bstB

bstA has the sameData as bstC

bstA does not have the sameData as bstD
   */


  public  boolean sameData(ABST<T> given) {
    return this.sameDataHelper(given) && given.sameDataHelper(this);
  }

  public boolean sameDataHelper(ABST<T> given) {
    return given.present(this.data)
        && this.left.sameDataHelper(given)
        && this.right.sameDataHelper(given);
  }

  public IList<T> buildList() {
    return new Cons<T>(this.getLeftMost(), this.getRight().buildList());
  }



}

