import java.util.List;

/**
 * The type Random sentence.
 */
public class RandomSentence {

  /**
   * The constant S1.
   */
  public static final String S1 = "May a swarm of sloppy monkeys find your genitals suddenly delectable.";
  /**
   * The constant S2.
   */
  public static final String S2 = "With the power of an irate manticore, may the hosts of Hades throw a party in your mother's anal cavity.";
  /**
   * The constant S3.
   */
  public static final String S3 = "You rotting vat of spittoon spittle.";
  /**
   * The constant S4.
   */
  public static final String S4 = "You are so creepy that even a barnacle would not want to fondle you.";
  /**
   * The constant S5.
   */
  public static final String S5 = "May a rabid Rush Limbaugh and a grisly and wormy group of manic weasels seek a battleground in your mother's bed.";
  /**
   * The constant S6.
   */
  public static final String S6 = "You are so unkempt that even a simpleton would not want to cuddle you.";


  /**
   * Instantiates a new Random sentence.
   */
  public RandomSentence() {
  }

  /**
   * Generate insult string.
   *
   * @return the string
   */
  public static String generateInsult() {
    List<String> insult = List.of(S1, S2, S3, S4, S5, S6);
    int ans = (int) (Math.random() * insult.size());
    return insult.get(ans);
  }

  @Override
  public String toString() {
    return "RandomSentence{}";
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
