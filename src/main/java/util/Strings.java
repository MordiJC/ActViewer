package util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Strings {

    public static String glueBrokenText(List<String> lines) {
        if (lines.isEmpty()) {
            return "";
        } else if (lines.size() == 1) {
            return lines.get(0);
        }

        List<String> glued = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();

        for(String line: lines) {
            if(line.matches(".*-$")) {
                stringBuilder.append(line.replaceAll("-$", ""));
            } else {
                stringBuilder.append(line).append(' ');
            }
        }

        return stringBuilder.toString();
    }
}
