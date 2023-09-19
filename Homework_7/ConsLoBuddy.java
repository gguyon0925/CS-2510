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
      * ... this.countBuddies() ...         -- int
      * ... this.countCommonBuddies(ILoBuddy that) ... -- int
      * ... this.hasExtendedBuddy(Person that) ...    -- boolean
      */

    // returns true if this list of buddies contains that person
    public boolean hasBuddy(Person that) {
        return this.first.username.equals(that.username) || this.rest.hasBuddy(that);
    }

    // returns the number of people who will show up at the party
    // given by this person
    public int countBuddies() {
        return 1 + this.rest.countBuddies();
    }

    // returns the number of people that are direct buddies
    // of both this and that person
    public int countCommonBuddies(ILoBuddy that) {
        if (that.hasBuddy(this.first)) {
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
}
