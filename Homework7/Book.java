class Author {
  String first;
  String last;
  int yob;
  IList<Book> books;

  Author(String first, String last, int yob, Book book) {
    this.first = first;
    this.last = last;
    this.yob = yob;
    this.books = new ConsList<Book>(book, new MtList<Book>());
  }

  boolean sameAuthor(Author other) {
    return this.first.equals(other.first) &&
    this.last.equals(other.last) &&
    this.yob == other.yob;
  }

  void addBook(Book b) {
    if (! this.sameAuthor(b.author)) {
      throw new RuntimeException("Book was not written by this author");
    } else {
     this.books = new ConsList<Book>(b, this.books);
    }
  }
}



class Book {
  String title;
  int price;
  int quantity;
  Author author;

  Book(String title, int price, int quantity, Author auth) {
    this.title = title;
    this.price = price;
    this.quantity = quantity;
    this.author = auth;
    this.author.addBook(this);
  }
  
}
