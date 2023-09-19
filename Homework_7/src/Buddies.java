import tester.*;

// represents a Person with a user name and a list of buddies
class Person {

  String username;
  ILoBuddy buddies;

  Person(String username) {
    this.username = username;
    this.buddies = new MTLoBuddy();
  }

  /*
   * Template for Fields:
   * ... this.username ...  -- String
   * ... this.buddies ...   -- ILoBuddy
   */

  /*
   * Template for Methods:
   * ... this.addBuddy(Person buddy) ...          -- void
   * ... this.hasDirectBuddy(Person that) ...  -- boolean
   * ... this.countCommonBuddies(Person that) ... -- int
   * ... this.hasExtendedBuddy(Person that) ...    -- boolean
   * ... this.hasExtendedBuddyHelper(Person that, ILoBuddy other) ... -- boolean
   * ... this.partyCount() ...                  -- int
   */

  // Change this person's buddy list so that it includes the given person
  void addBuddy(Person buddy) {
    this.buddies = new ConsLoBuddy(buddy, this.buddies);
  }

  // returns true if this Person has that as a direct buddy
  boolean hasDirectBuddy(Person that) {
    return this.buddies.hasBuddy(that);
  }

  int countCommonBuddies(Person that) {
    return buddies.countCommonBuddies(that);
  }

  // will the given person be invited to a party
  // organized by this person?
  boolean hasExtendedBuddy(Person that) {
    return this.hasDirectBuddy(that) || this.buddies.hasExtendedBuddy2(that, new MTLoBuddy());
  }

  // Helper method for hasExtendedBuddy
  boolean hasExtendBuddyHelper(Person that, ILoBuddy other) {
    if (!other.hasBuddy(this) && this.buddies.hasBuddy(that)) {
      return true;
    }
    else {
      return this.buddies.hasExtendedBuddy2(that, new ConsLoBuddy(this, other));
    }
  }

  // returns the number of people who will show up at the party
  // given by this person
  int partyCount() {
    return 1 + this.buddies.countPartyHelper(new ConsLoBuddy(this, new MTLoBuddy()));
  }
}

// represents a list of Person's buddies
interface ILoBuddy {

  /* 
   * Template for Methods:
   * ... this.hasBuddy(Person that) ...  -- boolean
   * ... this.countPartyHelper(ILoBuddy that) ... -- int
   * ... this.countCommonBuddies(ILoBuddy that) ... -- int
   * ... this.hasExtendedBuddy(Person that) ...    -- boolean
   * ... this.hasExtendedBuddy2(Person that, ILoBuddy other) ... -- boolean
   */

  // returns true if this list of buddies contains that person
  boolean hasBuddy(Person that);

  // Helper for count party to count the number of people
  // who will show up at the party
  int countPartyHelper(ILoBuddy that);

  // returns the number of people that are direct buddies
  // of both this person and that person
  int countCommonBuddies(Person that);

  // will the given person be (directly or indirectly) invited to a party
  // organized by this person?
  boolean hasExtendedBuddy(Person that);

  // Helper method for hasExtendBuddies 
  // to account for the indirect buddies of this person
  boolean hasExtendedBuddy2(Person that, ILoBuddy other);

}

// represents an empty list of Person's buddies
class MTLoBuddy implements ILoBuddy {
  MTLoBuddy() {
  }

  /* 
   * Template for Methods:
   * ... this.hasBuddy(Person that) ...  -- boolean
   * ... this.countPartyHelper(ILoBuddy that) ... -- int
   * ... this.countCommonBuddies(ILoBuddy that) ... -- int
   * ... this.hasExtendedBuddy(Person that) ...    -- boolean
   * ... this.hasExtendedBuddy2(Person that, ILoBuddy other) ... -- boolean
   */

  // returns true if this list of buddies contains that person
  public boolean hasBuddy(Person that) {
    return false;
  }

  // Helper for count party to count the number of people
  // who will show up at the party
  public int countPartyHelper(ILoBuddy that) {
    return 0;
  }

  // returns the number of people that are direct buddies
  // of both this person and that person
  public int countCommonBuddies(Person that) {
    return 0;
  }

  // will the given person be (directly or indirectly) invited to a party
  // organized by this person?
  public boolean hasExtendedBuddy(Person that) {
    return false;
  }

  // Helper method for hasExtendBuddies
  // to account for the indirect buddies of this person
  public boolean hasExtendedBuddy2(Person that, ILoBuddy other) {
    return false;
  }
}

// represents a list of Person's buddies
class ConsLoBuddy implements ILoBuddy {

  Person first;
  ILoBuddy rest;

  ConsLoBuddy(Person first, ILoBuddy rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template for Fields:
   * ... this.first ...  -- Person
   * ... this.rest ...   -- ILoBuddy
   */

  /* 
   * Template for Methods:
   * ... this.hasBuddy(Person that) ...  -- boolean
   * ... this.countBuddiesHelper(ILoBuddy that) ... -- int
   * ... this.countBuddies() ...         -- int
   * ... this.countCommonBuddies(ILoBuddy that) ... -- int
   * ... this.hasExtendedBuddy(Person that) ...    -- boolean
   * ... this.hasExtendedBuddy2(Person that, ILoBuddy other) ...    -- boolean
   */

  // returns true if this list of buddies contains that person
  public boolean hasBuddy(Person that) {
    return this.first.username.equals(that.username) || this.rest.hasBuddy(that);
  }

  // Helper method for partyCount
  // count the buddies in the cons list
  public int countPartyHelper(ILoBuddy that) {
    if (that.hasBuddy(this.first)) {
      return this.rest.countPartyHelper(that);
    }
    else {
      that = new ConsLoBuddy(this.first, that);
      return 1 + this.first.buddies.countPartyHelper(that) + this.rest.countPartyHelper(that)
          - this.countCommonBuddies(this.first);
    }
  }

  // returns the number of people that are direct buddies
  // of both this and that person
  public int countCommonBuddies(Person that) {
    if (that.hasDirectBuddy(this.first)) {
      return 1 + this.rest.countCommonBuddies(that);
    }
    else {
      return this.rest.countCommonBuddies(that);
    }
  }

  // will the given person be invited to a party
  // organized by this person?
  public boolean hasExtendedBuddy(Person that) {
    return this.first.hasExtendedBuddy(that) || this.rest.hasExtendedBuddy(that);
  }

  // Helper method for hasExtendBuddies
  // to check whether the given person is indirectly invited
  public boolean hasExtendedBuddy2(Person that, ILoBuddy other) {
    if (!other.hasBuddy(this.first) && this.first.hasExtendBuddyHelper(that, other)) {
      return true;
    }
    else {
      return this.rest.hasExtendedBuddy2(that, new ConsLoBuddy(this.first, other));
    }
  }
}

// runs tests for the buddies problem
class ExamplesBuddies {
  Person ann = new Person("Ann");
  Person bob = new Person("Bob");
  Person cole = new Person("Cole");
  Person dan = new Person("Dan");
  Person ed = new Person("Ed");
  Person fay = new Person("Fay");
  Person gabi = new Person("Gabi");
  Person hank = new Person("Hank");
  Person jan = new Person("Jan");
  Person kim = new Person("Kim");
  Person len = new Person("Len");

  // Examples of buddy lists
  ILoBuddy mtb = new MTLoBuddy();
  ILoBuddy bl1 = new ConsLoBuddy(this.cole, new ConsLoBuddy(this.bob, new MTLoBuddy()));

  // initializes every person’s buddy lists
  void initBuddies() {
    this.ann.buddies = new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb));
    this.bob.buddies = new ConsLoBuddy(this.ann,
        new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb)));
    this.cole.buddies = new ConsLoBuddy(this.dan, this.mtb);
    this.dan.buddies = new ConsLoBuddy(this.cole, this.mtb);
    this.ed.buddies = new ConsLoBuddy(this.fay, this.mtb);
    this.fay.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb));
    this.gabi.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb));
    this.jan.buddies = new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, this.mtb));
    this.kim.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.len, this.mtb));
    this.len.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.kim, this.mtb));
  }

  // test the method addBuddy
  void testAddBuddy(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));
    t.checkExpect(this.gabi.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb)));
    t.checkExpect(this.jan.buddies, new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, this.mtb)));

    // check add buddy to anns list
    this.ann.addBuddy(this.dan);
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.dan, new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb))));
    t.checkExpect(this.ann.hasDirectBuddy(this.dan), true);

    // check add buddy to bobs list
    this.bob.addBuddy(this.fay);
    t.checkExpect(this.bob.buddies, new ConsLoBuddy(this.fay,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb)))));
    t.checkExpect(this.bob.hasExtendedBuddy(this.ed), true);

    // check add buddy to fays list
    this.fay.addBuddy(this.jan);
    t.checkExpect(this.fay.buddies,
        new ConsLoBuddy(this.jan, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb))));

    // check add buddy to gabis list
    this.gabi.addBuddy(this.kim);
    t.checkExpect(this.gabi.buddies,
        new ConsLoBuddy(this.kim, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb))));
  }

  // test the method hasDirectBuddy
  void testHasDirectBuddy(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test hasDirectBuddy
    t.checkExpect(this.ann.hasDirectBuddy(this.cole), true);
    t.checkExpect(this.ann.hasDirectBuddy(this.ed), false);
    t.checkExpect(this.ed.hasDirectBuddy(this.ed), false);
    t.checkExpect(this.dan.hasDirectBuddy(this.cole), true);
    t.checkExpect(this.cole.hasDirectBuddy(this.dan), true);
    t.checkExpect(this.fay.hasDirectBuddy(this.gabi), true);
    t.checkExpect(this.gabi.hasDirectBuddy(this.fay), true);
  }

  // test the method hasExtendedBuddyHelper
  void testHasExtendedBuddyHelper(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test hasExtendBuddyHelper
    t.checkExpect(this.ann.hasExtendBuddyHelper(this.bob, this.ann.buddies), true);
    t.checkExpect(this.ann.hasExtendBuddyHelper(this.ed, this.ann.buddies), false);
    t.checkExpect(this.fay.hasExtendBuddyHelper(this.ed, this.fay.buddies), true);
    t.checkExpect(this.fay.hasExtendBuddyHelper(this.gabi, this.fay.buddies), true);
    t.checkExpect(this.dan.hasExtendBuddyHelper(this.cole, this.dan.buddies), true);
    t.checkExpect(this.cole.hasExtendBuddyHelper(this.dan, this.cole.buddies), true);
  }

  // test the method countCommonBuddies
  void testCountCommonBuddies(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test countCommonBuddies
    t.checkExpect(this.ann.countCommonBuddies(this.bob), 0);
    t.checkExpect(this.ann.countCommonBuddies(this.cole), 0);
    t.checkExpect(this.ann.countCommonBuddies(this.ed), 0);
  }

  // test the method hasExtendedBuddy
  void testHasExtendedBuddy(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test hasExtendedBuddy
    t.checkExpect(this.ann.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.fay.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.fay.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.gabi.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.gabi.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.hank), false);
  }

  // test the method hasExtendedBuddy2
  void testHasExtendedBuddy2(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test hasExtendedBuddy2
    t.checkExpect(this.ann.buddies.hasExtendedBuddy2(this.ed, this.ed.buddies), true);
    t.checkExpect(this.ann.buddies.hasExtendedBuddy2(this.fay, this.fay.buddies), false);
    t.checkExpect(this.bob.buddies.hasExtendedBuddy2(this.fay, this.fay.buddies), false);
    t.checkExpect(this.fay.buddies.hasExtendedBuddy2(this.ann, this.ann.buddies), false);
  }

  // test the method countPartyHelper
  void testCountPartyHelper(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));

    // test countPartyHelper
    t.checkExpect(this.ann.buddies.countPartyHelper(this.ann.buddies), 0);
    t.checkExpect(this.ann.buddies.countPartyHelper(this.bob.buddies), 3);
    t.checkExpect(this.ann.buddies.countPartyHelper(this.cole.buddies), 8);
    t.checkExpect(this.bob.buddies.countPartyHelper(this.ed.buddies), 8);
  }

  // test the method partyCount
  void testPartyCount(Tester t) {
    // initialize the buddy lists
    initBuddies();

    // test to see if initial buddy lists are correct
    t.checkExpect(this.ann.buddies,
        new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
    t.checkExpect(this.bob.buddies,
        new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
    t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

    // test partyCount
    t.checkExpect(this.cole.partyCount(), 2);
    t.checkExpect(this.ann.partyCount(), 8);
    t.checkExpect(this.hank.partyCount(), 1);
    t.checkExpect(this.fay.partyCount(), 3);
    t.checkExpect(this.gabi.partyCount(), 3);
  }
}