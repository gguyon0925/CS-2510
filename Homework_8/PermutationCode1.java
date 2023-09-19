import java.util.*;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
class PermutationCode {
    // The original list of characters to be encoded
    ArrayList<Character> alphabet = 
        new ArrayList<Character>(Arrays.asList(
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
                    't', 'u', 'v', 'w', 'x', 'y', 'z'));

    ArrayList<Character> code = new ArrayList<Character>(26);

    // A random number generator
    Random rand = new Random();

    // Create a new instance of the encoder/decoder with a new permutation code 
    PermutationCode() {
        this.code = this.initEncoder();
    }

    // Create a new instance of the encoder/decoder with the given code 
    PermutationCode(ArrayList<Character> code) {
        this.code = code;
    }

    // Initialize the encoding permutation of the characters
    ArrayList<Character> initEncoder() {
        return this.alphabet; //you should complete this method
    }

    // produce an encoded String from the given String
    String encode(String source) {
        return ""; //you should complete this method
    }

    // produce a decoded String from the given String
    String decode(String code) {
        return ""; //you should complete this method
    }
}

// give the examples to the tests
class Examples {
  ArrayList<Character> alphabet = new ArrayList<Character>(Arrays.asList(
          'h', 'w', 'z', 'a', 'm', 'q', 'k', 'l', 'e', 'p', 'v',
          'b', 'o', 'g', 'y', 'x', 'c', 'd', 'r', 'i', 'f', 'j',
          'n', 't', 's', 'u'));
  ArrayList<Character> code2 = new ArrayList<Character>(Arrays.asList(
          'h', 'm', 'q', 'k', 'l', 'e', 'p', 'v',
          'b', 'o', 'g', 'x', 'd', 'r', 'i', 'f', 'j',
          'n', 's', 'u'));
  ArrayList<Character> code3 = new ArrayList<Character>(Arrays.asList(
          'h', 'm', 'p', 'v', 'b', 'o', 'g', 'x', 'd', 'r', 'i', 'f', 'j',
          'n', 's', 'u'));
  
  PermutationCode pc1 = new PermutationCode();
  String decoded1 = "olehl ban";
  String encoded1 = "ybmlb whg";
  
  String decoded2 = "worldabw!!";
  String encoded2 = "nydbahwn!!";
  
  PermutationCode p = new PermutationCode();
  PermutationCode p1 = new PermutationCode(this.alphabet);
  PermutationCode p2 = new PermutationCode(this.code2);
  
  // test encode method
  void testEncode(Tester t) {
      t.checkExpect(p1.encode(this.decoded1), this.encoded1);
      t.checkExpect(p1.encode(this.decoded2), this.encoded2);
      t.checkExpect(p1.encode(""), "");
      t.checkExpect(p1.encode("ad"), "ha");
      t.checkExpect(p1.encode(""), "");
  }
  
  // test decode method
  void testDecode(Tester t) {
      t.checkExpect(p1.decode(this.encoded1), this.decoded1);
      t.checkExpect(p1.decode(this.encoded2), this.decoded2);
      t.checkExpect(p1.decode(""), "");
      t.checkExpect(p1.decode("ad"), "dr");
      t.checkExpect(p1.decode(""), "");
  }
  
  // test initEncoder method       
  boolean testInitEncoder(Tester t) {
      return t.checkExpect(p1.initEncoder().size(), 52) &&
              t.checkExpect(p2.initEncoder().size(), 46) &&
              t.checkExpect(p2.initEncoder().size(), 72);
  }
  
  // test the DecodeHelper method
  boolean testDecodeHelper(Tester t) {
      return t.checkExpect(p1.decodeHelper(this.encoded1, "htc"), "htcolehl ban") &&
              t.checkExpect(p1.decodeHelper("", "dgh"), "dgh") &&
              t.checkExpect(p1.decodeHelper("j !!!ij", " k ! j"), " k ! jv !!!tv") ;
  }
  
  // test the EncodeHelper method
  boolean testEncodeHelper(Tester t) {
      return t.checkExpect(p1.encodeHelper(this.encoded1, "htc"), "htcswobw nlk") &&
              t.checkExpect(p1.encodeHelper("", "dgh"), "dgh") &&
              t.checkExpect(p1.encodeHelper("j !!!ij", " k ! j"), " k ! jp !!!ep");
  }
  
  // test Permutation
  void testPermutation(Tester t) {
      ArrayList<Character> alphabet = 
          new ArrayList<Character>(Arrays.asList(
                  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
                  'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
                  't', 'u', 'v', 'w', 'x', 'y', 'z'));

      ArrayList<Character> code = new ArrayList<Character>(26);

      Random rand = new Random();
      t.checkExpect(new PermutationCode(alphabet).code, 
              new PermutationCode(alphabet).initEncoder());
  }
}
