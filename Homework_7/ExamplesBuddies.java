// import tester.*;

// // runs tests for the buddies problem
//  class ExamplesBuddies {
//   Person ann = new Person("Ann");
//   Person bob = new Person("Bob");
//   Person cole = new Person("Cole");
//   Person dan = new Person("Dan");
//   Person ed = new Person("Ed");
//   Person fay = new Person("Fay");
//   Person gabi = new Person("Gabi");
//   Person hank = new Person("Hank");
//   Person jan = new Person("Jan");
//   Person kim = new Person("Kim");
//   Person len = new Person("Len");

//   // Examples of buddy lists
//   ILoBuddy mtb = new MTLoBuddy();
//   ILoBuddy bl1 = new ConsLoBuddy(this.cole, new ConsLoBuddy(this.bob, new MTLoBuddy()));

//   // initializes every person’s buddy lists
//   void initBuddies() {
//     this.ann.buddies = new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb));
//     this.bob.buddies = new ConsLoBuddy(this.ann,
//         new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb)));
//     this.cole.buddies = new ConsLoBuddy(this.dan, this.mtb);
//     this.dan.buddies = new ConsLoBuddy(this.cole, this.mtb);
//     this.ed.buddies = new ConsLoBuddy(this.fay, this.mtb);
//     this.fay.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb));
//     this.gabi.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb));
//     this.jan.buddies = new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, this.mtb));
//     this.kim.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.len, this.mtb));
//     this.len.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.kim, this.mtb));
//   }

//   // test the method addBuddy
//   void testAddBuddy(Tester t) {
//     // initialize the buddy lists
//     initBuddies();

//     // test to see if initial buddy lists are correct
//     t.checkExpect(this.ann.buddies,
//         new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
//     t.checkExpect(this.bob.buddies,
//         new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
//     t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));
//     t.checkExpect(this.gabi.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb)));
//     t.checkExpect(this.jan.buddies, new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, this.mtb)));

//     // check add buddy to anns list
//     this.ann.addBuddy(this.dan);
//     t.checkExpect(this.ann.buddies, new ConsLoBuddy(this.dan,
//         new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb))));
//     t.checkExpect(this.ann.hasDirectBuddy(this.dan), true);

//     // check add buddy to bobs list
//     this.bob.addBuddy(this.fay);
//     t.checkExpect(this.bob.buddies, new ConsLoBuddy(this.fay,
//         new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb)))));
//     t.checkExpect(this.bob.hasExtendedBuddy(this.ed), true);

//     // check add buddy to fays list
//     this.fay.addBuddy(this.jan);
//     t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.jan,
//         new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb))));

//     // check add buddy to gabis list
//     this.gabi.addBuddy(this.kim);
//     t.checkExpect(this.gabi.buddies, new ConsLoBuddy(this.kim,
//         new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, this.mtb))));
//   }

//   // test the method hasDirectBuddy
//   void testHasDirectBuddy(Tester t) {
//     // initialize the buddy lists
//     initBuddies();

//     // test to see if initial buddy lists are correct
//     t.checkExpect(this.ann.buddies,
//         new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
//     t.checkExpect(this.bob.buddies,
//         new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
//     t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

//     // test hasDirectBuddy
//     t.checkExpect(this.ann.hasDirectBuddy(this.cole), true);
//     t.checkExpect(this.ann.hasDirectBuddy(this.ed), false);
//     t.checkExpect(this.ed.hasDirectBuddy(this.ed), false);
//     t.checkExpect(this.dan.hasDirectBuddy(this.cole), true);
//     t.checkExpect(this.cole.hasDirectBuddy(this.dan), true);
//     t.checkExpect(this.fay.hasDirectBuddy(this.gabi), true);
//     t.checkExpect(this.gabi.hasDirectBuddy(this.fay), true);
//   }

//   // test the method hasExtendedBuddy
//    void testHasExtendedBuddy(Tester t) {
//     // initialize the buddy lists
//      initBuddies();

//     // test to see if initial buddy lists are correct
//       t.checkExpect(this.ann.buddies,
//           new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
//       t.checkExpect(this.bob.buddies,
//           new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
//       t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

//     // test hasExtendedBuddy
//      t.checkExpect(this.ann.hasExtendedBuddy(this.ed), true);
//      t.checkExpect(this.ed.hasExtendedBuddy(this.fay), true);
//      t.checkExpect(this.fay.hasExtendedBuddy(this.ed), true);
//      t.checkExpect(this.fay.hasExtendedBuddy(this.gabi), true);
//      t.checkExpect(this.gabi.hasExtendedBuddy(this.fay), true);
//      t.checkExpect(this.gabi.hasExtendedBuddy(this.ed), true);
//      t.checkExpect(this.ed.hasExtendedBuddy(this.gabi), true);
//  //  t.checkExpect(this.ed.hasExtendedBuddy(this.hank), true);
//    }

//   // test the method countCommonBuddies
//   void testPartyCount(Tester t) {
//     // initialize the buddy lists
//     initBuddies();

//     // test to see if initial buddy lists are correct
//     t.checkExpect(this.ann.buddies,
//         new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, this.mtb)));
//     t.checkExpect(this.bob.buddies,
//         new ConsLoBuddy(this.ann, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.hank, this.mtb))));
//     t.checkExpect(this.fay.buddies, new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, this.mtb)));

//     // test partyCount
//     t.checkExpect(this.cole.partyCount(), 2);
//     t.checkExpect(this.ann.partyCount(), 3);
//     t.checkExpect(this.hank.partyCount(), 1);
//     t.checkExpect(this.fay.partyCount(), 3);
//     t.checkExpect(this.gabi.partyCount(), 3);
//   }
// }