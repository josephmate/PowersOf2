import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public class Main {

  private static final String TOP_MATERIAL =
      """
          <html>
          <head>
            <link rel="stylesheet" href="css/styles.css">
            <link rel="script" href="js/simulator.js">
          </head>
          <body>
            <div>
              Use this table to figure out the max complexity your algorithm can have in order to achieve some deadline.
              <ol>
                <li>Look for your how much time your algorithm has available in the Time column.</li>
                <li>In that row, look for your approximate problem size, expressed as a power of 2 and power of 10.</li>
                <li>The column you picked is the complexity of the algorithm you need to achieve to finish fast enough.</li>
              </ol>
            </div>
              These are estimates.
              There are so many factors that can throw the runtimes off by factors of 1000s like:
              <ol>
                <li>Computer age</li>
                <li>IO</li>
                <li>Network</li>
                <li>Platform (running in a browser vs. desktop)</li>
                <li>Lower order terms of your algorithm's complexity (ex: N^2 vs 2*N^2)</li>
                <li>Concurrency</li>
              </ol>
            <div>
            </div>
            <div>
              For instance, In
              <a href="https://codingcompetitions.withgoogle.com/kickstart/round/0000000000201d29/0000000000201d2a">
                Google Kickstart 2017 Round F, Problem 1 "Cake"
              </a>,
              you're suppose find the minimum number of square cakes needed to cover a target area.
              The input size was 10^4 and the expected runtime limit was 30 seconds.
              I had a brute force algorithm that I was guessing was between O(N^3) and O(N^6).
              At the time, I had no long how that would take.
              I was hoping for a resource similar to
              <a href="https://people.eecs.berkeley.edu/~rcs/research/interactive_latency.html">
                Latency Numbers Every Programmer Should Know
              </a>
              to help me ballpark how long my algorithm would take before implementing it.
              I couldn't find anything so I wasted time thinking about how to make it more efficient.
              I eventually gave up and wrote the algorithm.
              I got lucky and it ended up working.
              Using this table, I would have been more confident in my brute force algorithm.
            </div>
            <br/>
            <div>
              Feel free to file an
              <a href="https://github.com/josephmate/PowersOf2/issues">issue</a>
              or a
              <a href="https://github.com/josephmate/PowersOf2/pulls">pull request</a>.
            </div>
            <table>
              <thead>
          """;
  private static final String AFTER_TABLE_HEADERS =
      """
                <th>Java Time*</th>
                <th>Javascript Time**</th>
                <th>Notable Usage</th>
                <th>Time it yourself***</th>
              </thead>
      """;

  private static final String BOTTOM_MATERIAL =
    """
      </table>
      <div>*- ran using a loop that counts from 1 to 2^N on an AMD-FX6300</div>
      <div>**- ran using a loop that counts from 1 to 2^N on an AMD-FX6300 in my Google Chrome 90.0.4430.93</div>
      <div>*** - Counts from 1 to 2^N in your browser.</div>
      <div>**** - I couldn't measure such a small time scale in the browser.</div>
      <div>***** - These are estimated because I do not want to leave my computer running that long.</div>
    </body>
    </html>
    """;

  private static final Map<Integer, String> NOTABLE_POWERS = new ImmutableMap.Builder<Integer,String>()
      .put(18,
          """
          <a href="https://en.wikipedia.org/wiki/MD5">
          Efficient MD5 Collision calculation: 2013 Xie Tao, Fanbao Liu, and Dengguo Feng (2^18 time)
          </a>
          """)
      .put(32, """
          <a href="https://www.youtube.com/watch?v=m4yVlPqeZwo&t=1380s">
          A linear solution to the sorting question Eric Schmidt asked President Obama
          </a>
          """)
      .put(56,
          """
          <a href="https://en.wikipedia.org/wiki/Data_Encryption_Standard">
          brute force attack of DES is 2^56 in the worst case
          </a>
          """)
      .put(58,
          """
          <a href="https://en.wikipedia.org/wiki/Chronology_of_computation_of_%CF%80">
          303 days to calculate 50,000,000,000,000 digits of pi
          </a>
          """)
      .put(61,
          """
          <a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem#Exact_algorithms">
          85,900 node Travelling Salesman problem was solved in 136 CPU Years.
          </a>
          """)
      .build();

  private static final Map<Integer, Long> JAVA_TIMED_POWERS = new ImmutableMap.Builder<Integer,Long>()
      .put(0, 700L)
      .put(1, 500L)
      .put(2, 500L)
      .put(3, 600L)
      .put(4, 900L)
      .put(5, 1500L)
      .put(6, 3300L)
      .put(7, 4700L)
      .put(8, 8800L)
      .put(9, 24700L)
      .put(10, 35800L)
      .put(11, 76700L)
      .put(12, 144500L)
      .put(13, 286100L)
      .put(14, 500200L)
      .put(15, 1365000L)
      .put(16, 1991100L)
      .put(17, 464200L)
      .put(18, 868900L)
      .put(19, 2306600L)
      .put(20, 3720000L)
      .put(21, 2352200L)
      .put(22, 4510000L)
      .put(23, 494600L)
      .put(24, 993200L)
      .put(25, 2061000L)
      .put(26, 3444800L)
      .put(27, 7180200L)
      .put(28, 15098600L)
      .put(29, 28359100L)
      .put(30, 59983700L)
      .put(31, 122634500L)
      .put(32, 260377800L)
      .put(33, 477866000L)
      .put(34, 944203900L)
      .put(35, 1646380100L)
      .put(36, 3413087700L)
      .put(37, 7260554300L)
      .put(38, 14355965800L)
      .put(39, 28853465300L)
      .put(40, 53058954000L)
      .build();

  private static final Map<Integer, Long> JAVASCRIPT_TIMED_POWERS = new ImmutableMap.Builder<Integer,Long>()
      .put(19, 1L * 1000 * 1000)
      .put(20, 1L * 1000 * 1000)
      .put(21, 3L * 1000 * 1000)
      .put(22, 6L * 1000 * 1000)
      .put(23, 11L * 1000 * 1000)
      .put(24, 24L * 1000 * 1000)
      .put(25, 51L * 1000 * 1000)
      .put(26, 95L * 1000 * 1000)
      .put(27, 197L * 1000 * 1000)
      .put(28, 392L * 1000 * 1000)
      .put(29, 776L * 1000 * 1000)
      .put(30, 1560L * 1000 * 1000)
      .put(31, 4723L * 1000 * 1000)
      .put(32, 6046L * 1000 * 1000)
      .put(33, 12095L * 1000 * 1000)
      .put(34, 24830L * 1000 * 1000)
      .put(35, 48955L * 1000 * 1000)
      .build();

  private static final int MAX_JAVASCRIPT_POWER;
  private static final int MIN_JAVASCRIPT_POWER;
  private static final long MAX_JAVASCRIPT_DURATION;
  private static final int MAX_JAVA_POWER;
  private static final long MAX_JAVA_DURATION;

  static {
    MIN_JAVASCRIPT_POWER = JAVASCRIPT_TIMED_POWERS.keySet().stream().mapToInt(l -> l).min().getAsInt();
    MAX_JAVASCRIPT_POWER = JAVASCRIPT_TIMED_POWERS.keySet().stream().mapToInt(l -> l).max().getAsInt();
    MAX_JAVASCRIPT_DURATION = JAVASCRIPT_TIMED_POWERS.get(MAX_JAVASCRIPT_POWER);
    MAX_JAVA_POWER = JAVA_TIMED_POWERS.keySet().stream().mapToInt(l -> l).max().getAsInt();
    MAX_JAVA_DURATION = JAVA_TIMED_POWERS.get(MAX_JAVA_POWER);
  }

  private static double factorial(double n) {
    double result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  private static double log2(double val) {
    return Math.log(val)/Math.log(2);
  }

  private static final List<ComplexityOrder> COMPLEXITY_ORDERS = Arrays.asList(
      new ComplexityOrder("O(lgN)", Main::log2),
      new ComplexityOrder("O(&#8730N)", Math::sqrt),
      new ComplexityOrder("O(N)", n -> n),
      new ComplexityOrder("O(NlgN)", n -> log2(n)*n),
      new ComplexityOrder("O(N<sup>2</sup>)", n -> Math.pow(n, 2)),
      new ComplexityOrder("O(N<sup>3</sup>)", n -> Math.pow(n, 3)),
      new ComplexityOrder("O(N<sup>4</sup>)", n -> Math.pow(n, 4)),
      new ComplexityOrder("O(2<sup>N</sup>)", n -> Math.pow(2, n)),
      new ComplexityOrder("O(N!)", Main::factorial),
      new ComplexityOrder("O(N<sup>N</sup>)", n -> Math.pow(n, n))
  );

  private record ComplexityOrder(
      String htmlDisplay,
      Function<Double, Double> linearToTargetRuntime
  ) { }

  /**
   * Closest X to solving:
   * <pre>
   *   lg( linearToTargetComplexity(inputPower^X) ) = linearBase2Power
   * </pre>
   *
   * @param linearBase2Power the target linear base 2 power.
   * @param inputPower the base power we're searching in.
   * @param linearToTargetComplexity the function that maps the input size to runtime complexity.
   * @return closest integer solution to
   * <pre>lg( linearToTargetComplexity(inputPower^X) ) = linearBase2Power</pre>
   */
  private static int findNearestBaseXPower(
      int linearBase2Power,
      int inputPower,
      Function<Double, Double> linearToTargetComplexity
  ) {
    int closest = 0;
    double closestDistance = Double.MAX_VALUE;
    double previousDistance = Double.MAX_VALUE;
    final double expected = Math.pow(2, linearBase2Power);
    int i = 0;
    while (true) {
      double inputSize = Math.pow(inputPower, i);
      double runtime = linearToTargetComplexity.apply(inputSize);
      double distance = Math.abs(runtime - expected);
      if(distance < closestDistance) {
        closestDistance = distance;
        closest = i;
      }
      if (distance > previousDistance) {
        break;
      }
      previousDistance = distance;
      i+=1;
    }
    return closest;
  }

  private static long pow(long base, long exp) {
    long result = 1;
    for(long i = 0; i < exp; i++) {
      result = result * base;
    }
    return result;
  }

  private static void printComplexity(
      BufferedWriter writer,
      int base2Power,
      ComplexityOrder order
  ) throws IOException {
    writer.write("      <td>");
    writer.write("2<sup>");
    if (order.htmlDisplay.equals("O(lgN)")) {
      if (base2Power <= 9) {
        writer.write(String.valueOf(pow(2, base2Power)));
      } else {
        writer.write("2<sup>");
        writer.write(String.valueOf(base2Power));
        writer.write("</sup>");
      }
    } else {
      writer.write(String.valueOf(findNearestBaseXPower(base2Power, 2, order.linearToTargetRuntime)));
    }
    writer.write("</sup>, ");
    writer.write("10<sup>");

    if (order.htmlDisplay.equals("O(lgN)") && base2Power > 9) {
      writer.write(String.valueOf(154 * pow(2, base2Power-9)));
    } else {
      writer.write(String.valueOf(findNearestBaseXPower(base2Power, 10, order.linearToTargetRuntime)));
    }
    writer.write("</sup>");
    writer.write("</td>\n");
  }

  private static String prettyPrintDuration(BigInteger durationNanos) {
    final BigInteger nanoSecondsTrimmed = durationNanos.mod(BigInteger.valueOf(1000L));
    final BigInteger microSeconds = durationNanos.divide(BigInteger.valueOf(1000L));
    final BigInteger microSecondsTrimmed = microSeconds.mod(BigInteger.valueOf(1000L));
    final BigInteger milliSeconds = microSeconds.divide(BigInteger.valueOf(1000L));
    final BigInteger millisecondsTrimmed = milliSeconds.mod(BigInteger.valueOf(1000L));
    final BigInteger seconds = milliSeconds.divide(BigInteger.valueOf(1000));
    final BigInteger secondsTrimmed = seconds.mod(BigInteger.valueOf(60));
    final BigInteger minutes = seconds.divide(BigInteger.valueOf(60));
    final BigInteger minutesTrimmed = minutes.mod(BigInteger.valueOf(60));
    final BigInteger hours = minutes.divide(BigInteger.valueOf(60));
    final BigInteger hoursTrimmed = hours.mod(BigInteger.valueOf(24));
    final BigInteger days = hours.divide(BigInteger.valueOf(24));
    final BigInteger daysTrimmed = days.mod(BigInteger.valueOf(365));
    final BigInteger years = days.divide(BigInteger.valueOf(365));

    if (years.compareTo(BigInteger.valueOf(0)) > 0) {
      return years + " years, " + daysTrimmed + " days";
    }
    if (days.compareTo(BigInteger.valueOf(0)) > 0) {
      return daysTrimmed + " days, " + hoursTrimmed + " hours";
    }
    if (hours.compareTo(BigInteger.valueOf(0)) > 0) {
      return hoursTrimmed + " hours, " + minutesTrimmed + " minutes";
    }
    if (minutes.compareTo(BigInteger.valueOf(0)) > 0) {
      return minutesTrimmed + " minutes, " + secondsTrimmed + " s";
    }
    if (seconds.compareTo(BigInteger.valueOf(0)) > 0) {
      return secondsTrimmed + " s, " + millisecondsTrimmed + " ms";
    }
    if (milliSeconds.compareTo(BigInteger.valueOf(0)) > 0) {
      return millisecondsTrimmed + " ms, " + microSecondsTrimmed + " &mu;s";
    }
    if (microSeconds.compareTo(BigInteger.valueOf(0)) > 0) {
      return microSecondsTrimmed + " &mu;s, " + nanoSecondsTrimmed + " ns";
    }

    return nanoSecondsTrimmed + " ns";
  }

  private static BigInteger extendPower(
      int linearBase2Power,
      int startBase,
      long startSize
  ) {
    BigInteger result = BigInteger.valueOf(startSize);
    for (int i = startBase; i <= linearBase2Power; i++) {
      result = result.multiply(BigInteger.valueOf(2));
    }
    return result;
  }

  private static void printTime(
      BufferedWriter writer,
      int linearBase2Power,
      int minPower,
      int maxPower,
      long maxDuration,
      Map<Integer, Long> timedPowers
  ) throws IOException {
    Long duration = timedPowers.get(linearBase2Power);
    writer.write("      <td>");
    if (duration != null) {
      writer.write(prettyPrintDuration(BigInteger.valueOf(duration)));
    } else if (linearBase2Power < minPower) {
      writer.write("****");
    } else {
      writer.write(prettyPrintDuration(extendPower(
          linearBase2Power,
          maxPower,
          maxDuration)));
      writer.write("*****");
    }
    writer.write("</td>\n");
  }

  public static void main(String[] args) throws Exception {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(("index.html")))) {
      writer.write(TOP_MATERIAL);
      for (ComplexityOrder complexityOrder : COMPLEXITY_ORDERS) {
        writer.write("      <th>");
        writer.write(complexityOrder.htmlDisplay);
        writer.write("\n");
      }
      writer.write(AFTER_TABLE_HEADERS);
      for(int linearBase2Power = 0; linearBase2Power <= 64; linearBase2Power++) {
        writer.write("    <tr>\n");
        for (ComplexityOrder complexityOrder : COMPLEXITY_ORDERS) {
          printComplexity(writer, linearBase2Power, complexityOrder);
        }

        printTime(writer, linearBase2Power, 0, MAX_JAVA_POWER, MAX_JAVA_DURATION, JAVA_TIMED_POWERS);
        printTime(writer, linearBase2Power, MIN_JAVASCRIPT_POWER, MAX_JAVASCRIPT_POWER, MAX_JAVASCRIPT_DURATION, JAVASCRIPT_TIMED_POWERS);

        String notable = NOTABLE_POWERS.get(linearBase2Power);
        writer.write("      <td>");
        if (notable != null) {
          writer.write(notable);
        }
        writer.write("</td>\n");

        // simulate
        String cellId = "linearBase2Power_" + linearBase2Power;
        writer.write("      <td id='");
        writer.write(cellId);
        writer.write("'>");
        writer.write("<input type='button' onclick='simulate(\"");
        writer.write(cellId);
        writer.write("\",");
        writer.write(String.valueOf(linearBase2Power));
        writer.write(")' value='Go'");
        writer.write("</td>\n");

        writer.write("    </tr>\n");
      }
      writer.write(BOTTOM_MATERIAL);
    }
  }

}
