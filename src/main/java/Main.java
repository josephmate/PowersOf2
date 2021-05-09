import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

  private static final String HTML_PATH = "index.html";

  private static final List<String> AFTER_TABLE_HEADERS = Arrays.asList(
      "Java Time*",
      "Javascript Time**",
      "Notable Usage",
      "Time it yourself***"
  );

  public static void main(String[] args) throws Exception {
    HtmlParser.ParsedHtml parsedHtml = HtmlParser.parse(HTML_PATH);
    PowersOf2 powersOf2 = PowersOf2.load();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter((HTML_PATH)))) {
      writer.write(parsedHtml.topMaterial());
      writer.write("  <table id=\"runtimeTable\">");
      writer.write("\n");

      // Print the header using the complexities and the non-complexity headers
      writer.write("    <thead>");
      writer.write("\n");
      List<String> headers = new ArrayList<>(powersOf2.complexities().size() + AFTER_TABLE_HEADERS.size());
      headers.addAll(powersOf2.complexities());
      headers.addAll(AFTER_TABLE_HEADERS);
      for (String header : headers) {
        writer.write("      <th>");
        writer.write(header);
        writer.write("</th>\n");
      }
      writer.write("    </thead>\n");

      for(Map.Entry<Integer, PowersOf2.PowerOf2> entry : powersOf2.powers().entrySet()) {
        int linearBase2Power = entry.getKey();
        PowersOf2.PowerOf2 powerOf2 = entry.getValue();

        writer.write("    <tr>\n");
        for(String complexity : powersOf2.complexities()) {
          PowersOf2.PowerPerformance powerPerformance = powerOf2.powerPerformances().get(complexity);
          writer.write("      <td>");
          writer.write(powerPerformance.base2());
          writer.write(", ");
          writer.write(powerPerformance.base10());
          writer.write("</td>\n");
        }

        writer.write("      <td>");
        writer.write(powerOf2.javaTimeDisplay());
        writer.write("</td>\n");

        writer.write("      <td>");
        writer.write(powerOf2.javascriptTimeDisplay());
        writer.write("</td>\n");

        writer.write("      <td>");
        if (powerOf2.notableUsage() != null) {
          writer.write(powerOf2.notableUsage());
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
