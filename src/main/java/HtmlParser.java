import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HtmlParser {


  public static record ParsedHtml(
      String topMaterial,
      String bottomMaterial
  ) { }

  public static ParsedHtml parse(String path) throws IOException {
    StringBuilder topMaterial = new StringBuilder();
    StringBuilder bottomMaterial = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;

      // Top material is described by everything until <table id="runtimeTable">
      while ((line = reader.readLine()) != null && !line.contains("<table id=\"runtimeTable\">")) {
        topMaterial.append(line);
        topMaterial.append("\n");
      }
      // Ignore the table. It will be regenerated.
      while ((line = reader.readLine()) != null && !line.contains("</table>")) {
        // NO-OP
      }
      // Bottom material is everything after the table.
      while ((line = reader.readLine()) != null) {
        bottomMaterial.append(line);
        bottomMaterial.append("\n");
      }
    }

    return new ParsedHtml(topMaterial.toString(), bottomMaterial.toString());
  }

}
