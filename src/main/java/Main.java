import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
              These are based on estimates.
              There are so many factors that can throw the runtimes off by factors of 10-100 like:
              <ol>
                <li>Computer age</li>
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
              you're suppose find the minimum square cakes needed for a target input area.
              The input size was 10^4 and the runtime limit was 30 seconds.
              I had an algorithm that I was guessing was  less between O(N^3) and O(N^6).
              At O(N^3), with 10^3 we were looking at seconds.
              As a result, I decided it would not be a waste of time to try out this solution.
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
            <th>Time(msec)</th>
            <th>Notable Usage</th>
            <th>Time it yourself*</th>
          </thead>
      """;

  private static final String BOTTOM_MATERIAL =
    """
      </table>
      <div>* - Counts from 1 to 2^N in your browser.</div>
      <div>** - these are estimated because I do not want to leave my computer running that long.</div>
    </body>
    </html>
    """;

  private static final Map<Integer, String> NOTABLE_POWERS = new ImmutableMap.Builder<Integer,String>()
      .put(18, "Efficient MD5 Collision calculation: 2013 Xie Tao, Fanbao Liu, and Dengguo Feng (2^18 time)")
      .put(34, "Sorting all 10 digit phone numbers with Obama's algorithm (10^10 or about 2^34) or counting all 32 bit integers (2^32)")
      .put(46, "303 days to calculate 50,000,000,000,000 digits of pi (about 2^46)")
      .put(56, "brute force attack of DES is 2^56 in the worst case")
      .build();

  private static final Map<Integer, Long> TIMED_POWERS = new ImmutableMap.Builder<Integer,Long>()
      .put(0, 0L)
      .put(1, 0L)
      .put(2, 0L)
      .put(3, 0L)
      .put(4, 0L)
      .put(5, 0L)
      .put(6, 0L)
      .put(7, 0L)
      .put(8, 0L)
      .put(9, 0L)
      .put(10, 0L)
      .put(11, 0L)
      .put(12, 0L)
      .put(13, 0L)
      .put(14, 0L)
      .put(15, 0L)
      .put(16, 0L)
      .put(17, 0L)
      .put(18, 0L)
      .put(19, 1L)
      .put(20, 1L)
      .put(21, 3L)
      .put(22, 6L)
      .put(23, 11L)
      .put(24, 24L)
      .put(25, 51L)
      .put(26, 95L)
      .put(27, 197L)
      .put(28, 392L)
      .put(29, 776L)
      .put(30, 1560L)
      .put(31, 4723L)
      .put(32, 6046L)
      .put(33, 12095L)
      .put(34, 24830L)
      .put(35, 48955L)
      .build();

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

  private static void printComplexity(
      BufferedWriter writer,
      int base2Power,
      ComplexityOrder order
  ) throws IOException {
    writer.write("      <td>");
    writer.write("2<sup>");
    writer.write(String.valueOf(findNearestBaseXPower(base2Power, 2, order.linearToTargetRuntime)));
    writer.write("</sup>, ");
    writer.write("10<sup>");
    writer.write(String.valueOf(findNearestBaseXPower(base2Power, 10, order.linearToTargetRuntime)));
    writer.write("</sup>");
    writer.write("</td>\n");
  }

  private static String prettyPrintDuration(final long durationMilliseconds) {
    final long millisecondsTrimmed = durationMilliseconds % 1000;
    final long seconds = durationMilliseconds/1000;
    final long secondsTrimmed = seconds % 60;
    final long minutes = seconds / 60;
    final long minutesTrimmed = minutes % 60;
    final long hours = minutes / 60;
    final long hoursTrimmed = hours % 24;
    final long days = hours / 24;
    final long daysTrimmed = days % 365;
    final long years = days / 365;

    if (years > 0) {
      return years + " years, " + daysTrimmed + " days";
    }
    if (days > 0) {
      return daysTrimmed + " days, " + hoursTrimmed + " hours";
    }
    if(hours > 0) {
      return hoursTrimmed + " hours, " + minutesTrimmed + " minutes";
    }
    if(minutes > 0) {
      return minutesTrimmed + " minutes, " + secondsTrimmed + " s";
    }
    if(seconds > 0) {
      return secondsTrimmed + " s, " + millisecondsTrimmed + " ms";
    }

    return millisecondsTrimmed + " ms";
  }

  private static long extendPower(int linearBase2Power) {
    long result = 48955L;
    for (int i = 35; i <= linearBase2Power; i++) {
      result = result << 2;
    }
    return result;
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

        Long duration = TIMED_POWERS.get(linearBase2Power);
        writer.write("      <td>");
        if (duration != null) {
          writer.write(prettyPrintDuration(duration));
        } else {
          writer.write(prettyPrintDuration(extendPower(linearBase2Power)));
          writer.write("**");
        }
        writer.write("</td>\n");

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
