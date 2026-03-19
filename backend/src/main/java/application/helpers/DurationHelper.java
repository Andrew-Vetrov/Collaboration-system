package application.helpers;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationHelper {

    // Для преобразования из установленного формата длительности (число-слово) в Duration
    // Возможно, стоит всё же поменять этот формат
    public Duration parseDuration(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Duration string cannot be empty");
        }

        // Убираем лишние пробелы, приводим к нижнему регистру
        String s = input.trim().toLowerCase();

        // Ожидаемый формат: число + пробел + единица (week, day, hour, minute, etc.)
        Pattern pattern = Pattern.compile("^(\\d+)\\s*([a-z]+)$");
        Matcher matcher = pattern.matcher(s);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + input);
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "week", "weeks"     -> Duration.ofDays(value*7);
            case "day", "days"       -> Duration.ofDays(value);
            case "hour", "hours"     -> Duration.ofHours(value);
            case "minute", "minutes" -> Duration.ofMinutes(value);
            default -> throw new IllegalArgumentException("Unsupported unit: " + unit);
        };
    }

    public String formatDuration(Duration duration) {
        if (duration == null) {
            return null;
        }

        long totalMinutes = duration.toMinutes();
        if (totalMinutes % 60 != 0) {
            return totalMinutes + " minute" + (totalMinutes > 1 ? "s" : "");
        }

        long totalHours = duration.toHours();
        if (totalHours % 24 != 0){
            return totalHours + " hour" + (totalHours > 1 ? "s" : "");
        }

        long totalDays = duration.toDays();
        if (totalDays % 7 != 0) {
            return totalDays + " day" + (totalDays > 1 ? "s" : "");
        }

        long weeks = totalDays / 7;
        return weeks + " week" + (weeks > 1 ? "s" : "");
    }
}
