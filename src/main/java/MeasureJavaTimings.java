import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MeasureJavaTimings {

  public static final String JAVA_TIMINGS_PATH = "data/java_timings.json";

  private static long pow(int exponent) {
    long result = 1;
    for(var i = 0; i < exponent; i++) {
      result *= 2;
    }
    return result;
  }

  private static void simulate(int linearBase2Power, Map<Integer, Long> timings) {
    long accumulator = 0;
    long iterations = pow(linearBase2Power);

    long t1 = System.nanoTime();
    for(long i = 0; i < iterations; i++) {
      accumulator += 1;
    }
    long t2 = System.nanoTime();
    var timeNanos = t2 - t1;
    timings.put(linearBase2Power, timeNanos);
    System.out.println(linearBase2Power + ", " + timeNanos + ", " + accumulator);
  }

  public static void main(String[] args) throws IOException {
    Map<Integer, Long> timings = new HashMap<>();
    for(int i = 0; i <= 40; i++) {
      simulate(i, timings);
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JAVA_TIMINGS_PATH), timings);
  }
}
