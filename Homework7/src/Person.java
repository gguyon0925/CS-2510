
// represents a Person with a user name and a list of buddies
class Person {

    String username;
    ILoBuddy buddies;

    Person(String username) {
        this.username = username;
        this.buddies = new MTLoBuddy();
    }


    void addBuddy(Person buddy) {
      if (buddy.username.equals(this.username)){
       throw new RuntimeException("Already has this buddy in buddy list");
      } else {
      this.buddies = new ConsLoBuddy(buddy, this.buddies);
      
    }
  }

    // returns true if this Person has that as a direct buddy
    boolean hasDirectBuddy(Person that) {
        return this.buddies.directBuddy(that);
    }

    // returns the number of people that are direct buddies 
    // of both this and that person
    int countCommonBuddies(Person that) {
      return this.buddies.countCommons(that);
    }

    // will the given person be invited to a party 
    // organized by this person?
    boolean hasExtendedBuddy(Person that) {
      return this.buddies.hasExtendedBuddyHelper(that) 
          || this.hasDirectBuddy(that);
    }

     // returns the number of people who will show up at the party 
    // given by this person

    int partyCount() {
      return 1 + this.buddies.countAll();
    }

}
