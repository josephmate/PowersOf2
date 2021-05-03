public class Measure {

  public static long pow(int exponent) {
    long result = 1;
    for(var i = 0; i < exponent; i++) {
      result *= 2;
    }
    return result;
  }

  private static long simulate(int linearBase2Power) {
    long accumulator = 0;
    long iterations = pow(linearBase2Power);

    long t1 = System.nanoTime();
    for(long i = 0; i < iterations; i++) {
      accumulator += 1;
    }
    long t2 = System.nanoTime();
    var timeNanos = t2 - t1;
    System.out.println("      .put(" + linearBase2Power + ", " + timeNanos + "L)");
    return accumulator;
  }

  public static void main(String[] args) {
    for(int i = 0; i <= 40; i++) {
      simulate(i);
    }
  }
}
