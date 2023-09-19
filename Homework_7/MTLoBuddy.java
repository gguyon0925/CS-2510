
// represents an empty list of Person's buddies
class MTLoBuddy implements ILoBuddy {
    MTLoBuddy() {}

    /* 
     * Template for Methods:
     * ... this.hasBuddy(Person that) ...  -- boolean
     * ... this.countBuddies() ...         -- int
     * ... this.countCommonBuddies(ILoBuddy that) ... -- int
     * ... this.hasExtendedBuddy(Person that) ...    -- boolean
     */

     
    // returns true if this list of buddies contains that person
    public boolean hasBuddy(Person that) {
        return false;
    }

    // returns the number of people who will show up at the party
    // given by this person
    public int countBuddies() {
        return 0;
    }

    // returns the number of people that are direct buddies
    // of both this and that person
    public int countCommonBuddies(ILoBuddy that) {
        return 0;
    }

    // will the given person be invited to a party
    // organized by this person?
    public boolean hasExtendedBuddy(Person that) {
        return false;
    }

}
