import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class Main {

  private static final String JAVASCRIPT_TIMINGS_PATH = "data/javascript_timings.json";

  private static final String AFTER_TABLE_HEADERS =
      """
                <th>Java Time*</th>
                <th>Javascript Time**</th>
                <th>Notable Usage</th>
                <th>Time it yourself***</th>
              </thead>
      """;

  private static final Map<Integer, String> NOTABLE_POWERS = new ImmutableMap.Builder<Integer,String>()
      .put(18,
          """
          <a href="https://en.wikipedia.org/wiki/MD5">
          Efficient MD5 Collision calculation: 2013 Xie Tao, Fanbao Liu, and Dengguo Feng (2^18 time)
          </a>
          """)
      .put(20, """
          <a href="https://www.youtube.com/watch?v=m4yVlPqeZwo&t=1380s">
          A linear solution to the sorting question Eric Schmidt asked President Obama
          </a>
          """)
      .put(44, """
          <a href="https://www.youtube.com/watch?v=V4V2bpZlqx8">
          20 minutes to solve the Enigma code every morning.
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
      .put(64,
          """
          <a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem#Exact_algorithms">
          85,900 node Travelling Salesman problem was solved in 136 CPU Years.
          </a>
          """)
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

  private static record ComplexityOrder(
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

  private static final String HTML_PATH = "index.html";

  public static void main(String[] args) throws Exception {
    HtmlParser.ParsedHtml parsedHtml = HtmlParser.parse(HTML_PATH);

    ObjectMapper objectMapper = new ObjectMapper();
    Map<Integer, Long> javascriptTimedPowers = objectMapper.readValue(
        new File(JAVASCRIPT_TIMINGS_PATH),
        new TypeReference<>(){});
    Map<Integer, Long> javaTimedPowers = objectMapper.readValue(
        new File(MeasureJavaTimings.JAVA_TIMINGS_PATH),
        new TypeReference<>(){});
    int minJavascriptPower = javascriptTimedPowers.keySet().stream().mapToInt(l -> l).min().getAsInt();
    int maxJavascriptPower = javascriptTimedPowers.keySet().stream().mapToInt(l -> l).max().getAsInt();
    long maxJavascriptDuration = javascriptTimedPowers.get(maxJavascriptPower);
    int maxJavaPower = javaTimedPowers.keySet().stream().mapToInt(l -> l).max().getAsInt();
    long maxJavaDuration = javaTimedPowers.get(maxJavaPower);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter((HTML_PATH)))) {
      writer.write(parsedHtml.topMaterial());
      writer.write("  <table id=\"runtimeTable\">");
      writer.write("\n");
      writer.write("    <thead>");
      writer.write("\n");
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

        printTime(writer, linearBase2Power, 0, maxJavaPower, maxJavaDuration, javaTimedPowers);
        printTime(writer, linearBase2Power, minJavascriptPower, maxJavascriptPower, maxJavascriptDuration, javascriptTimedPowers);

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
        writer.write(")' value='Go'>");
        writer.write("</td>\n");

        writer.write("    </tr>\n");
      }
      writer.write("  </table>");
      writer.write("\n");
      writer.write(parsedHtml.bottomMaterial());
    }
  }

}
