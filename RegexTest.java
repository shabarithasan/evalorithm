import java.util.regex.*;
import java.nio.file.*;
import java.util.*;
public class RegexTest {
    public static void main(String[] args) throws Exception {
        String text = new String(Files.readAllBytes(Paths.get("C:/Projects/college/college/college/evalorithm/DBMS_Syllabus.txt")));
        Pattern unitPattern = Pattern.compile("(?i)(unit\\s+[\\dIVXivx]+|module\\s+[\\dIVXivx]+|chapter\\s+[\\dIVXivx]+)[.:\\s]+(.+?)(?=unit\\s+[\\dIVXivx]+|module\\s+[\\dIVXivx]+|chapter\\s+[\\dIVXivx]+|$)", Pattern.DOTALL);
        Matcher unitMatcher = unitPattern.matcher(text);
        while (unitMatcher.find()) {
            String unitContent = unitMatcher.group(2).trim();
            List<String> topicList = new ArrayList<>();
            if (topicList.isEmpty()) {
                String[] lines = unitContent.split("\\n");
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && trimmed.length() > 3) {
                        topicList.add(trimmed);
                    }
                }
            }
            System.out.println("Unit " + unitMatcher.group(1) + " topics count: " + topicList.size());
            for(String t : topicList) { System.out.println("  " + t.substring(0, Math.min(10, t.length()))); }
        }
    }
}
