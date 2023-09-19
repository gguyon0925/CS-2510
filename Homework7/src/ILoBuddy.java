
// represents a list of Person's buddies
interface ILoBuddy {

  boolean directBuddy(Person that);

  int countCommons(Person that);

  boolean hasExtendedBuddyHelper(Person that);

  int countAll();


}
