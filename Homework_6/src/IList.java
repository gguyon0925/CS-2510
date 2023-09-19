interface IList<T> {
  
 

}

class Empty<T> implements IList<T> {
  Empty(){}



}

class Cons<T> implements IList<T>{
  T first;
  IList<T> rest;

  Cons(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }




}