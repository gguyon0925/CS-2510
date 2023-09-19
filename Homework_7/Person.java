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
    * ... this.hasDirectBuddy(Person that) ...  -- boolean
    * ... this.countCommonBuddies(Person that) ... -- int
    * ... this.hasExtendedBuddy(Person that) ...    -- boolean
    * ... this.addBuddy(Person buddy) ...          -- void
    * ... this.countBuddies() ...                  -- int
    */


 // Change this person's buddy list so that it includes the given person
  void addBuddy(Person buddy) {
    this.buddies = new ConsLoBuddy(buddy, this.buddies);
  }
  
  // returns true if this Person has that as a direct buddy
  boolean hasDirectBuddy(Person that) {
    return this.buddies.hasBuddy(that);
  }

  // returns the number of people that are direct buddies 
  // of both this and that person
  int countCommonBuddies(Person that) {
    return this.buddies.countCommonBuddies(that.buddies);
  }

  // will the given person be invited to a party 
  // organized by this person?
  boolean hasExtendedBuddy(Person that) {
    return this.hasDirectBuddy(that) || this.buddies.hasExtendedBuddy(that);
  }

    // returns the number of people who will show up at the party 
  // given by this person
  int partyCount() {
    return this.buddies.countBuddies() + 1;
  }
}
