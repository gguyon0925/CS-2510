
// represents an empty list of Person's buddies
class MTLoBuddy implements ILoBuddy {
    MTLoBuddy() {}


    public boolean directBuddy(Person that) {
      return false;
    }

    public int countCommons(Person that) {
      return 0;
    }

    public boolean hasBuddies(Person that) {
      return false;
    }

    public boolean hasExtendedBuddyHelper(Person that) {
      return false;
    }

    public int countAll() {
      return 0;
    }

}
