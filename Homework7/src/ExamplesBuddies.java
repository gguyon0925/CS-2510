import tester.*;


// runs tests for the buddies problem
public class ExamplesBuddies{

  Person anna;
  Person bob;
  Person cole;
  Person dan;
  Person ed;
  Person fay;
  Person gabi;
  Person hank;
  Person jan;
  Person kim;
  Person len;

  void initData() {
    this.anna = new Person("Anna");
    this.bob = new Person("Bob");
    this.cole = new Person("Cole");
    this.dan = new Person("Dan");
    this.ed = new Person("Ed");
    this.fay = new Person("Fay");
    this.gabi = new Person("Gabi");
    this.hank = new Person("Hank");
    this.jan = new Person("Jan");
    this.kim = new Person("Kim");
    this.len = new Person("Len");

    // annas buddys
    // this.anna.addBuddy(bob);
    // this.anna.addBuddy(cole);

    // this.bob.addBuddy(anna);
    // this.bob.addBuddy(ed);
    // this.bob.addBuddy(hank);

    // this.cole.addBuddy(dan);

    // this.dan.addBuddy(cole);

    // this.ed.addBuddy(fay);

    // this.fay.addBuddy(ed);
    // this.fay.addBuddy(gabi);

    this.anna.buddies = new ConsLoBuddy(cole, new ConsLoBuddy(bob, new MTLoBuddy()));
    this.bob.buddies = new ConsLoBuddy(hank, new ConsLoBuddy(ed, new ConsLoBuddy(anna, new MTLoBuddy())));
    this.dan.buddies = new ConsLoBuddy(cole, new MTLoBuddy());
    this.cole.buddies = new ConsLoBuddy(this.dan, new MTLoBuddy());
    this.ed.buddies = new ConsLoBuddy(this.fay, new MTLoBuddy());
    this.fay.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, new MTLoBuddy()));
    this.gabi.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, new MTLoBuddy()));
    this.hank.buddies = new MTLoBuddy();
    this.jan.buddies = new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, new MTLoBuddy()));
    this.kim.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.len, new MTLoBuddy()));
    this.len.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.kim, new MTLoBuddy()));

  }

  void testInitData(Tester t) {
    this.initData();

    t.checkExpect(this.anna.buddies, new ConsLoBuddy(cole, new ConsLoBuddy(bob, new MTLoBuddy())));
    t.checkExpect(this.bob.buddies, new ConsLoBuddy(hank, new ConsLoBuddy(ed, new ConsLoBuddy(anna, new MTLoBuddy()))));
  }

  void testAddBuddy(Tester t) {
    this.initData();

    this.anna.addBuddy(len);

     t.checkExpect(this.anna.buddies, new ConsLoBuddy(len, new ConsLoBuddy(cole, new ConsLoBuddy(bob, new MTLoBuddy()))));
  }

  void testHasDirectBuddy(Tester t) {
    this.initData();

    t.checkExpect(this.anna.hasDirectBuddy(len), false);

    t.checkExpect(this.anna.hasDirectBuddy(bob), true);
  }

  void testCountCommon(Tester t) {
    this.initData();

    t.checkExpect(this.anna.countCommonBuddies(dan), 1);
    t.checkExpect(this.anna.countCommonBuddies(len), 0);
    t.checkExpect(this.len.countCommonBuddies(kim), 1);
  }

  void testHasExtendedBuddy(Tester t) {
    this.initData();

    t.checkExpect(this.anna.hasExtendedBuddy(dan), true);
    t.checkExpect(this.anna.hasExtendedBuddy(ed), true);
    t.checkExpect(this.anna.hasExtendedBuddy(jan), false);

    t.checkExpect(this.anna.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.fay.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.fay.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.gabi.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.gabi.hasExtendedBuddy(this.ed), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.ed.hasExtendedBuddy(this.hank), false);
  }

  void testPartyCount(Tester t) {
    this.initData();

    t.checkExpect(this.cole.partyCount(), 2);
    t.checkExpect(this.anna.partyCount(), 8);
    t.checkExpect(this.hank.partyCount(), 1);
    t.checkExpect(this.fay.partyCount(), 3);
    t.checkExpect(this.gabi.partyCount(), 3);
  }
}