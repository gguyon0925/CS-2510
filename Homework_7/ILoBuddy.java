
// represents a list of Person's buddies
interface ILoBuddy {

  /* 
   * Template for Methods:
   * ... this.hasBuddy(Person that) ...  -- boolean
   * ... this.countBuddies() ...         -- int
   * ... this.countCommonBuddies(ILoBuddy that) ... -- int
   * ... this.hasExtendedBuddy(Person that) ...    -- boolean
   */

  // returns true if this list of buddies contains that person
  boolean hasBuddy(Person that);

  // returns the number of people who will show up at the party
  // given by this person
  int countBuddies();

  // returns the number of people that are direct buddies
  // of both this and that person
  int countCommonBuddies(ILoBuddy that);

  // will the given person be invited to a party
  // organized by this person?
  boolean hasExtendedBuddy(Person that);

}


