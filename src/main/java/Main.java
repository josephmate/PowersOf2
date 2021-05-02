import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.time.StopWatch;

public class Main {

  private static final String TOP_MATERIAL =
      """
      <html>
      <head>
        <link rel="stylesheet" href="css/styles.css">
      </head> 
      <body>
        <table>
          <thead>
      """;
  private static final String AFTER_TABLE_HEADERS =
      """
            <!--<th>Time(msec)</th>-->
            <th>Not able Usage</th>
            <!--<th>Time it yourself</th>-->
          </thead>
      """;

  private static final String BOTTOM_MATERIAL =
    """
      </table>
      <div>* - these are estimated because I do not want to leave my computer running that long.</div>
    </body>      
    </html>
    """;

  private static final Map<Integer, String> NOTABLE_POWERS = new ImmutableMap.Builder()
      .put(18, "Efficient MD5 Collision calculation: 2013 Xie Tao, Fanbao Liu, and Dengguo Feng (2^18 time)")
      .put(34, "Sorting all 10 digit phone numbers with Obama's algorithm (10^10 or about 2^34) or counting all 32 bit integers (2^32)")
      .put(46, "303 days to calculate 50,000,000,000,000 digits of pi (about 2^46)")
      .put(56, "brute force attack of DES is 2^56 in the worst case")
      .build();

  private static double factorial(double n) {
    double result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }

  private static double log(double base, double val) {
    return Math.log(val)/Math.log(base);
  }

  /**
   *             <th>O(lgN) Power</th>
   *             <th>O(sqrt(N)) Power</th>
   *             <th>O(N) Power</th>
   *             <th>O(NlgN) Power</th>
   *             <th>O(N^2) Power</th>
   *             <th>O(N^3) Power</th>
   *             <th>O(2^N) Power</th>
   *             <th>O(N!) Power</th>
   *             <th>O(N^N) Power</th>
   */
  private static final List<ComplexityOrder> COMPLEXITY_ORDERS = Arrays.asList(
      //new ComplexityOrder("O(lgN)", Function.identity()),
      //new ComplexityOrder("O(&#8730N)", Function.identity()),
      new ComplexityOrder("O(N)", n -> n),
      new ComplexityOrder("O(NlgN)", n -> log(2, n)*n),
      new ComplexityOrder("O(N<sup>2</sup>)", n -> Math.pow(n, 2)),
      new ComplexityOrder("O(N<sup>3</sup>)", n -> Math.pow(n, 3)),
      new ComplexityOrder("O(2<sup>N</sup>)", n -> Math.pow(2, n)),
      new ComplexityOrder("O(N!)", n -> factorial(n)),
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
    for (int i = 0; i <= linearBase2Power; i++) {
      double inputSize = Math.pow(inputPower, i);
      double runtime = linearToTargetComplexity.apply(inputSize);
      double distance = Math.abs(runtime - expected);
      if(distance < closestDistance) {
        closestDistance = distance;
        closest = i;
      }
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
    writer.write("      </td>\n");
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
          StopWatch sw = StopWatch.createStarted();
          printComplexity(writer, linearBase2Power, complexityOrder);
          sw.stop();
          System.out.println(linearBase2Power + " " + complexityOrder.htmlDisplay + " " + sw.formatTime());
        }
        String notable = NOTABLE_POWERS.get(linearBase2Power);
        writer.write("      <td>");
        if (notable != null) {
          writer.write(notable);
        }
        writer.write("</td>\n");
        writer.write("    </tr>\n");
      }
      writer.write(BOTTOM_MATERIAL);
    }
  }

}
