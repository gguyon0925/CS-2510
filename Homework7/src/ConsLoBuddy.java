// represents a list of Person's buddies
class ConsLoBuddy implements ILoBuddy {

    Person first;
    ILoBuddy rest;

    ConsLoBuddy(Person first, ILoBuddy rest) {
        this.first = first;
        this.rest = rest;
    }


    public boolean directBuddy(Person that) {
      return this.first.equals(that) || this.rest.directBuddy(that);
    }


    public int countCommons(Person that) {
      if (that.buddies.directBuddy(this.first)) {
        return 1 + this.rest.countCommons(that);
      } else {
        return this.rest.countCommons(that);
      }
    }

    public boolean hasExtendedBuddyHelper(Person that) {
      return this.first.hasDirectBuddy(that) || 
      this.rest.hasExtendedBuddyHelper(that);
    }

    // returns the number of people who will show up at the party
    // given by this person, and includes extended buddie

    public int countAll() {
      return 1 + this.first.partyCount() + this.rest.countAll();
    }
    }

