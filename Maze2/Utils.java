import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.HashSet;
import tester.*;

// Represents utility functions for ArrayLists and HashMaps
class Utils {
  // Concatenates two ArrayLists
  <T> ArrayList<T> concatenate(ArrayList<T> a1, ArrayList<T> a2) {
    for (T t : a2) {
      a1.add(t);
    }
    return a1;
  }

  // Sorts an ArrayList using a quicksort algorithm based on the given comparator
  <T> void quicksort(ArrayList<T> arr, Comparator<T> comp) {
    quicksort(arr, comp, 0, arr.size() - 1);
  }

  // Overloaded quicksort method with specified range
  <T> void quicksort(ArrayList<T> arr, Comparator<T> comp, int low, int high) {
    if (low < high) {
      int pivotIndex = partition(arr, comp, low, high);
      quicksort(arr, comp, low, pivotIndex - 1);
      quicksort(arr, comp, pivotIndex + 1, high);
    }
  }

  // Partitions the ArrayList based on a pivot value and comparator, and returns the pivot index
  <T> int partition(ArrayList<T> arr, Comparator<T> comp, int low, int high) {
    T pivot = arr.get(high);
    int i = low - 1;
    for (int j = low; j < high; j++) {
      if (comp.compare(arr.get(j), pivot) <= 0) {
        i++;
        swap(arr, i, j);
      }
    }
    swap(arr, i + 1, high);
    return i + 1;
  }

  // Swaps two elements in an ArrayList
  <T> void swap(ArrayList<T> arr, int i, int j) {
    T temp = arr.get(i);
    arr.set(i, arr.get(j));
    arr.set(j, temp);
  }

  // Adds an element to an ArrayList if the element does not already exist in the list
<T> ArrayList<T> addWithoutDuplicates(ArrayList<T> arr, T t) {
  if (!arr.contains(t)) {
    arr.add(t);
  }
  return arr;
}

// Collects all of the Vertices from a list of Edges and adds them to an ArrayList
ArrayList<Vertex> collectVertices(ArrayList<Edge> arr) {
  HashSet<Vertex> verticesSet = new HashSet<>();
  for (Edge e : arr) {
    e.addUniqueVertices(new ArrayList<>(verticesSet));
    verticesSet.add(e.start);
    verticesSet.add(e.end);
  }
  return new ArrayList<>(verticesSet);
}


// Returns whether or not the given items cause a cycle in a graph
// if not, updates the HashMap accordingly
<T> boolean hasCycle(HashMap<T, T> hash, T t1, T t2) {
  T rootOne = findRoot(hash, t1);
  T rootTwo = findRoot(hash, t2);
  if (rootOne.equals(rootTwo)) {
    // they are already connected
    return true;
  } else {
    // they are separate
    hash.replace(rootTwo, rootOne);
    return false;
  }
}

// Helper method for hasCycle; finds the root connector of the given item in a HashMap
<T> T findRoot(HashMap<T, T> hash, T t) {
  if (!hash.containsKey(t)) {
    throw new NullPointerException("Invalid key");
  }
  while (!hash.get(t).equals(t)) {
    t = hash.get(t);
  }
  return t;
}

// Returns the neighbors of the given Vertex as directed by the given ArrayList of Edges
ArrayList<Vertex> getNeighbors(Vertex v, ArrayList<Edge> edges) {
  ArrayList<Vertex> neighbors = new ArrayList<>();
  for (Edge e : edges) {
    if (e.includesVertex(v)) {
      neighbors.add(e.start.equals(v) ? e.end : e.start);
    }
  }
  return neighbors;
}

// Reverses an ArrayList
<T> ArrayList<T> reverse(ArrayList<T> arr) {
  ArrayList<T> reversed = new ArrayList<>(arr.size());
  for (int i = arr.size() - 1; i >= 0; i--) {
    reversed.add(arr.get(i));
  }
  return reversed;
}

// Gets the values of a HashMap given the keys and returns them in an ArrayList
<T, U> ArrayList<U> getValues(HashMap<T, U> hash, ArrayList<T> keys) {
  ArrayList<U> values = new ArrayList<>(keys.size());
  for (T t : keys) {
    U value = hash.get(t);
    if (value != null) {
      values.add(value);
    }
  }
  return values;
}
}


 class UtilsTest {
  Vertex v1, v2, v3, v4, vertex1, vertex2, vertex3;
  Edge e1, e2, e3, e4, e5;
  Utils utils;

  Comparator<Edge> comp;

  public void initData() {
    utils = new Utils();
    comp = new EdgeComparator();

    v1 = new Vertex(0, 0);
    v2 = new Vertex(1, 0);
    v3 = new Vertex(1, 1);
    v4 = new Vertex(0, 1);

    e1 = new Edge(v1, v2, 1);
    e2 = new Edge(v2, v3, 2);
    e3 = new Edge(v1, v3, 3);
    e4 = new Edge(v3, v4, 4);
    e5 = new Edge(v1, v4, 5);

    vertex1 = new Vertex(0, 0);
    vertex2 = new Vertex(1, 1);
    vertex3 = new Vertex(2, 2);

  }

  public void testConcatenate(Tester t) {
    initData();
    ArrayList<Vertex> list1 = new ArrayList<>();
    list1.add(v1);
    list1.add(v2);
    ArrayList<Vertex> list2 = new ArrayList<>();
    list2.add(v3);
    list2.add(v4);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(v1);
    expected.add(v2);
    expected.add(v3);
    expected.add(v4);
    t.checkExpect(utils.concatenate(list1, list2), expected);
  }

  // Test the quicksort method
  void testQuicksort(Tester t) {
    initData();

    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(e3);
    edges.add(e2);
    edges.add(e1);
    edges.add(e4);

    utils.quicksort(edges, comp);

    t.checkExpect(edges.get(0), e1);
    t.checkExpect(edges.get(1), e2);
    t.checkExpect(edges.get(2), e3);
    t.checkExpect(edges.get(3), e4);
  }

  public void testSwap(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    list.add(vertex3);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex3);
    expected.add(vertex2);
    expected.add(vertex1);
    utils.swap(list, 0, 2);
    t.checkExpect(list, expected);
  }

  public void testAddWithoutDuplicates(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex1);
    expected.add(vertex2);
    t.checkExpect(utils.addWithoutDuplicates(list, vertex1), expected);
    expected.add(vertex3);
    t.checkExpect(utils.addWithoutDuplicates(list, vertex3), expected);
  }

  public void testCollectVertices(Tester t) {
    initData();
    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(new Edge(v1, v2, 1));
    edges.add(new Edge(v2, v3, 1));
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(v1);
    expected.add(v2);
    expected.add(v3);
    t.checkExpect(utils.collectVertices(edges), expected);
  }

  public void testHasCycle(Tester t) {
    initData();
    HashMap<Vertex, Vertex> hash = new HashMap<>();
    hash.put(v1, v1);
    hash.put(v2, v2);
    t.checkExpect(utils.hasCycle(hash, v1, v2), false);
  }

  public void testFindRoot(Tester t) {
    initData();
    HashMap<Vertex, Vertex> hash = new HashMap<>();
    hash.put(v1, v1);
    hash.put(v2, v2);
    hash.put(v3, v1);
    t.checkExpect(utils.findRoot(hash, v3), v1);
  }
// Test the getNeighbors method
void testGetNeighbors(Tester t) {
  initData();
  
  ArrayList<Edge> edges = new ArrayList<>();
  edges.add(new Edge(v1, v2, 1));
  edges.add(new Edge(v2, v3, 1));
  edges.add(new Edge(v3, v4, 1));
  
  ArrayList<Vertex> v1Neighbors = utils.getNeighbors(v1, edges);
  ArrayList<Vertex> v2Neighbors = utils.getNeighbors(v2, edges);
  ArrayList<Vertex> v3Neighbors = utils.getNeighbors(v3, edges);
  
  t.checkExpect(v1Neighbors.size(), 1);
  t.checkExpect(v1Neighbors.contains(v2), true);
  
  t.checkExpect(v2Neighbors.size(), 2);
  t.checkExpect(v2Neighbors.contains(v1), true);
  t.checkExpect(v2Neighbors.contains(v3), true);
  
  t.checkExpect(v3Neighbors.size(), 2);
  t.checkExpect(v3Neighbors.contains(v2), true);
  t.checkExpect(v3Neighbors.contains(v4), true);
  }


  public void testReverse(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    list.add(vertex3);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex3);
    expected.add(vertex2);
    expected.add(vertex1);
    t.checkExpect(utils.reverse(list), expected);
  }

  public void testGetValues(Tester t) {
    initData();
    HashMap<Vertex, Integer> hash = new HashMap<>();
    hash.put(vertex1, 1);
    hash.put(vertex2, 2);
    hash.put(vertex3, 3);
    ArrayList<Vertex> keys = new ArrayList<>();
    keys.add(vertex1);
    keys.add(vertex2);
    ArrayList<Integer> expected = new ArrayList<>();
    expected.add(1);
    expected.add(2);
    t.checkExpect(utils.getValues(hash, keys), expected);
  }
}







