import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverter
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        try
        {
            // User input prompt
            System.out.println("Enter month (e.g., 'Jan' or 'January'), day, week, quarter, or 'current': ");
            String input = scanner.nextLine().trim();

            // Get current date
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = null, endDate = null;

            // Default end date for previous cases (except previous day)
            LocalDate defaultEndDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1).minusDays(1);

            // Regex pattern to match month input
            String monthRegex = "(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)";
            Pattern monthPattern = Pattern.compile(monthRegex);
            Matcher monthMatcher = monthPattern.matcher(input);

            // Check if input matches a month abbreviation or name
            if (monthMatcher.find())
            {
                String monthStr = monthMatcher.group(1);
                Month month = null;
                // Match month abbreviation or name with Java Month enum
                for (Month m : Month.values())
                {
                    if (m.name().toLowerCase().startsWith(monthStr))
                    {
                        month = m;
                        break;
                    }
                }
                // If month found, calculate start and end date of the month
                if (month != null)
                {
                    startDate = LocalDate.of(currentDate.getYear(), month, 1);
                    endDate = startDate.plusMonths(1).minusDays(1);
                    // Print start and end date of the month
                    System.out.println("Start Date of " + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " = " + startDate);
                    System.out.println("End Date of " + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " = " + endDate);
                }
                else
                {
                    // If invalid month input
                    System.out.println("Invalid month input. Please enter a valid month name or abbreviation.");
                }
            }
            else
            {
                // Handling other input options with numeric values
                Pattern pattern = Pattern.compile("(?i)(previous|next)?\\s*(\\d+)?\\s*(year|day|week|month|quarter)?");
                Matcher matcher = pattern.matcher(input);

                if (matcher.find())
                {
                    String direction = matcher.group(1);
                    String numberStr = matcher.group(2);
                    String unit = matcher.group(3);

                    int number = (numberStr != null) ? Integer.parseInt(numberStr) : 1;

                    if (direction != null && unit != null)
                    {
                        switch (direction.toLowerCase())
                        {
                            case "previous":
                                switch (unit.toLowerCase())
                                {
                                    case "year":
                                        int previousYear = currentDate.getYear() - number;
                                        Month startMonthOfPreviousYear = currentDate.getMonth();
                                        startDate = LocalDate.of(previousYear, startMonthOfPreviousYear, 1);
                                        endDate= currentDate.minusDays(1).minusMonths(1);
                                        break;
                                    case "day":
                                        startDate = currentDate.minusDays(number);
                                        endDate = currentDate.minusDays(1);
                                        break;
                                    case "week":
                                        startDate = currentDate.minusWeeks(number).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                                        endDate = startDate.plusWeeks(number).minusDays(1);
                                        break;
                                    case "month":
                                        startDate = currentDate.minusMonths(number).with(TemporalAdjusters.firstDayOfMonth());
                                        endDate = defaultEndDate;
                                        break;
                                    case "quarter":
                                        int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;
                                        int startQuarter = currentQuarter - number;
                                        int startYear = currentDate.getYear();

                                        // Adjust the year and quarter if the subtraction results in a previous year
                                        while (startQuarter < 1)
                                        {
                                            startQuarter += 4;
                                            startYear--;
                                        }
                                        startDate = LocalDate.of(startYear, (startQuarter - 1) * 3 + 1, 1);
                                        endDate = defaultEndDate;
                                        break;
                                }
                                break;
                            case "next":
                                switch (unit.toLowerCase())
                                {
                                    case "year":
                                        startDate = currentDate.plusYears(1).with(TemporalAdjusters.firstDayOfYear());
                                        endDate = startDate.plusYears(number);
                                        break;
                                    case "day":
                                        startDate = currentDate.plusDays(1);
                                        endDate = currentDate.plusDays(number);
                                        break;
                                    case "week":
                                        startDate = currentDate.plusDays(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY) );
                                        endDate = startDate.plusWeeks(number).minusDays(1);
                                        break;
                                    case "month":
                                        startDate = currentDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                                        endDate = startDate.plusMonths(number).minusDays(1);
                                        break;
                                    case "quarter":
                                        int currentQuarter = (currentDate.getMonthValue() + 1) / 3 + 1;
                                        startDate = LocalDate.of(currentDate.getYear(), (currentQuarter - 1) * 3 + 1, 1);
                                        endDate = startDate.plusMonths(number * 3).minusDays(1);
                                        break;
                                }
                                break;
                        }
                    }
                    else
                    {
                        // Default current time period cases
                        switch (input)
                        {
                            case "current year":
                                startDate = LocalDate.of(currentDate.getYear(), 1, 1);
                                endDate = LocalDate.of(currentDate.getYear(), 12, 31);
                                break;
                            case "current day":
                                startDate = endDate = currentDate;
                                break;
                            case "current week":
                                startDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                                endDate = startDate.plusDays(6);
                                break;
                            case "current quarter":
                                int currentMonth = currentDate.getMonthValue();
                                int currentQuarter = (currentMonth - 1) / 3 + 1;
                                startDate = LocalDate.of(currentDate.getYear(), (currentQuarter - 1) * 3 + 1, 1);
                                endDate = startDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                                break;
                        }
                    }
                }

                // Print start and end date if valid input
                if (startDate != null && endDate != null)
                {
                    System.out.println("Start Date = " + startDate);
                    System.out.println("End Date = " + endDate);
                }
                else
                {
                    System.out.println("Invalid input. Please enter a valid month, day, week, quarter, or 'current'.");
                }
            }
        }
        catch (Exception e)
        {
            // Catch any exceptions
            System.out.println("Error occurred: " + e.getMessage());
        } finally
        {
            // Close scanner
            scanner.close();
        }
    }
}

