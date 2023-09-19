import java.util.ArrayList;
import java.util.Arrays;
import tester.*;

// Class that defines a vigenere cipher
class Vigenere {
  ArrayList<Character> alphabet = new ArrayList<Character>(
      Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
  ArrayList<ArrayList<Character>> table = new ArrayList<ArrayList<Character>>();

  // Constcutor to create a vigenere square with the given alphabet
  Vigenere(ArrayList<Character> alphabet) {
    this.alphabet = alphabet;
    this.table = this.initTable();
  }

  // Constructor to create a vigenere square with the default alphabet
  Vigenere() {
    this.table = this.initTable();
  }

  // rewrite initTable using a while loop and a helper method and no j++ or i++
  ArrayList<ArrayList<Character>> initTable() {
    return this.initTableHelper(new ArrayList<ArrayList<Character>>(), 0);
  }

  // helper method for initTableWhile
  ArrayList<ArrayList<Character>> initTableHelper(ArrayList<ArrayList<Character>> table, int i) {
    if (i == this.alphabet.size()) {
      return table;
    }
    else {
      ArrayList<Character> row = new ArrayList<Character>();
      return this.initTableHelper(this.initTableHelper2(table, row, i, 0), i + 1);
    }
  }

  // helper method for initTableHelper
  ArrayList<ArrayList<Character>> initTableHelper2(ArrayList<ArrayList<Character>> table,
      ArrayList<Character> row, int i, int j) {
    if (j == this.alphabet.size()) {
      table.add(row);
      return table;
    }
    else {
      row.add(this.alphabet.get((i + j) % this.alphabet.size()));
      return this.initTableHelper2(table, row, i, j + 1);
    }
  }

  // rewrite the decode method using a while loop and a helper method
  String decode(String encodedMessage, String keyword) {
    String key = this.generateKey(encodedMessage, keyword);
    return this.decodeHelper(encodedMessage, key, "");
  }

  // helper method for the decodeWhile method
  String decodeHelper(String encodedMessage, String key, String decodedMessage) {
    if (encodedMessage.length() == 0) {
      return decodedMessage;
    }
    else {
      char eChar = encodedMessage.charAt(0);
      char kChar = key.charAt(0);
      int row = this.alphabet.indexOf(kChar);
      int col = this.table.get(row).indexOf(eChar);
      return this.decodeHelper(encodedMessage.substring(1), key.substring(1),
          decodedMessage + this.alphabet.get(col));
    }
  }

  // rewrite the encode method using a while loop and a helper method
  String encode(String message, String keyword) {
    String key = this.generateKey(message, keyword);
    return this.encodeHelper(message, key, "");
  }

  // helper method for the encodeWhile method
  String encodeHelper(String message, String key, String encodedMessage) {
    if (message.length() == 0) {
      return encodedMessage;
    }
    else {
      char mChar = message.charAt(0);
      char kChar = key.charAt(0);
      int row = this.alphabet.indexOf(kChar);
      int col = this.alphabet.indexOf(mChar);
      return this.encodeHelper(message.substring(1), key.substring(1),
          encodedMessage + this.table.get(row).get(col));
    }
  }

  // rewrite the generateKey method using a while loop and a helper method
  String generateKey(String message, String keyword) {
    return this.generateKeyHelper(message, keyword, "", 0, 0);
  }

  // helper method for the generateKeyWhile method
  String generateKeyHelper(String message, String keyword, String key, int i, int j) {
    if (i == message.length()) {
      return key;
    }
    else {
      if (j == keyword.length()) {
        j = 0;
      }
      return this.generateKeyHelper(message, keyword, key + keyword.charAt(j), i + 1, j + 1);
    }
  }
}

// Examples and test for the Vigenere squares
class ExamplesVigenere {
  Vigenere vig;

  void initData() {
    this.vig = new Vigenere(
        new ArrayList<Character>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')));
  }

  // test the encode method
  void testEncode(Tester t) {
    this.initData();
    t.checkExpect(vig.encode("thanksgiving", "happyhappyha"), "ahpcizgxkgug");
  }

  // test the decode method
  void testDecode(Tester t) {
    this.initData();
    t.checkExpect(vig.decode("ahpcizgxkgug", "happyhappyha"), "thanksgiving");
  }

  // test the generateKey method
  void testGenerateKey(Tester t) {
    this.initData();
    t.checkExpect(vig.generateKey("thanksgiving", "happyhappyha"), "happyhappyha");
  }
}
