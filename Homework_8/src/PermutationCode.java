import java.util.*;
import tester.*;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
class PermutationCode {
  // The original list of characters to be encoded 
  ArrayList<Character> alphabet = new ArrayList<Character>(
      Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

  ArrayList<Character> code = new ArrayList<Character>(26);

  /** A random number generator */
  Random rand = new Random();

  // Create a new instance of the encoder/decoder with a new permutation code 
  PermutationCode() {
    this.code = this.initEncoder();
  }

  //Create a new instance of the encoder/decoder with a new code 
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }

  // Initialize the encoding permutation of the characters
  ArrayList<Character> initEncoder() {
    ArrayList<Character> copy = new ArrayList<Character>(
        Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
    return this.initEncoderHelper(new ArrayList<Character>(26), copy);
  }

  // Helper for initEncoder
  ArrayList<Character> initEncoderHelper(ArrayList<Character> start, ArrayList<Character> end) {
    while (end.size() > 0) {
      int i = this.rand.nextInt(end.size());
      code.add(end.get(i));
      end.remove(i);
    }
    return this.code;
  }

  // produce an encoded String from the given String
  String encode(String source) {
    return this.encodeHelper(source, "");
  }

  // helper function for encode
  String encodeHelper(String source, String encoded) {
    while (!source.equals("")) {
      Character first = source.charAt(0);
      String rest = source.substring(1);

      if (this.alphabet.contains(first)) {
        encoded = encoded.concat(this.code.get(this.alphabet.indexOf(first)).toString());
      }
      else {
        encoded = encoded.concat(first.toString());
      }
      source = rest;
    }
    return encoded;
  }

  // produce a decoded String from the given String
  String decode(String code) {
    return this.decodeHelper(code, "");
  }

  // helper function for decode
  String decodeHelper(String code, String message) {
    while (!code.equals("")) {
      Character first = code.charAt(0);
      String rest = code.substring(1);

      if (this.alphabet.contains(first)) {
        message = message.concat(this.alphabet.get(this.code.indexOf(first)).toString());
      }
      else {
        message = message.concat(first.toString());
      }
      code = rest;
    }
    return message;
  }
}

// give the examples to the tests
class ExamplesPermutation {
  ArrayList<Character> alphabet;
  ArrayList<Character> code;
  ArrayList<Character> code2;
  String decoded1;
  String encoded1;
  String decoded2;
  String encoded2;
  PermutationCode pc1;
  PermutationCode p1;
  PermutationCode p2;

  // represent initial data conditions for tests
  void initData() {
    this.alphabet = new ArrayList<Character>(Arrays.asList('o', 'w', 'u', 'a', 'm', 'q', 'k', 'l',
        'f', 'j', 'v', 'n', 'h', 'g', 's', 'x', 'c', 'd', 'r', 'i', 'e', 'p', 'b', 't', 'y', 'z'));
    this.code = new ArrayList<Character>(Arrays.asList('c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w'));
    this.code2 = new ArrayList<Character>(Arrays.asList('h', 'm', 'p', 'v', 'b', 'o', 'g', 'x', 'd',
        'r', 'i', 'f', 'j', 'n', 's', 'u'));
    this.decoded1 = "hello world";
    this.encoded1 = "lmnns bsdna";
    this.decoded2 = "goodbye world";
    this.encoded2 = "kssawym bsdna";
    this.pc1 = new PermutationCode();
    this.p1 = new PermutationCode(this.alphabet);
    this.p2 = new PermutationCode(this.code2);
  }

  // test encode method
  void testEncode(Tester t) {
    this.initData();
    t.checkExpect(p1.encode(this.decoded1), this.encoded1);
    t.checkExpect(p1.encode(this.decoded2), this.encoded2);
    t.checkExpect(p1.encode("hello"), "lmnns");
    t.checkExpect(p1.encode("world"), "bsdna");
    t.checkExpect(p1.encode("this is!!"), "ilfr fr!!");
    t.checkExpect(p1.encode(""), "");
  }

  // test decode method
  void testDecode(Tester t) {
    this.initData();
    t.checkExpect(p1.decode(this.encoded1), this.decoded1);
    t.checkExpect(p1.decode(this.encoded2), this.decoded2);
    t.checkExpect(p1.decode("lmnns"), "hello");
    t.checkExpect(p1.decode("bsdna"), "world");
    t.checkExpect(p1.decode("ilfr fr!!"), "this is!!");
    t.checkExpect(p1.decode(""), "");
  }

  // test initEncoder method       
  void testInitEncoder(Tester t) {
    this.initData();
    t.checkExpect(p1.initEncoder().size(), 52);
    t.checkExpect(p2.initEncoder().size(), 42);
    t.checkExpect(p2.initEncoder().size(), 68);
  }

  // test initEncoderHelper method
  void testInitEncoderHelper(Tester t) {
    this.initData();
    t.checkExpect(p2.initEncoderHelper(new ArrayList<Character>(26), new ArrayList<Character>(26)),
        this.code2);
    t.checkExpect(p2.initEncoderHelper(new ArrayList<Character>(26), new ArrayList<Character>(26)),
        new ArrayList<Character>(Arrays.asList('h', 'm', 'p', 'v', 'b', 'o', 'g', 'x', 'd', 'r',
            'i', 'f', 'j', 'n', 's', 'u')));
  }

  // test the EncodeHelper method
  void testEncodeHelper(Tester t) {
    this.initData();
    t.checkExpect(p1.encodeHelper(this.encoded1, "htc"), "htcnhggr wrago");
    t.checkExpect(p1.encodeHelper("", "abc"), "abc");
    t.checkExpect(p1.encodeHelper("l !! ed", "k ! f"), "k ! fn !! ma");
  }

  // test the DecodeHelper method
  void testDecodeHelper(Tester t) {
    this.initData();
    t.checkExpect(p1.decodeHelper(this.decoded1, "htc"), "htcmuhha bashr");
    t.checkExpect(p1.decodeHelper("", "abc"), "abc");
    t.checkExpect(p1.decodeHelper("l !! ed", "k ! f "), "k ! f h !! ur");
  }

  // test Permutation
  void testPermutation(Tester t) {
    this.initData();
    t.checkExpect(new PermutationCode(alphabet).code, new PermutationCode(alphabet).initEncoder());
  }
}
