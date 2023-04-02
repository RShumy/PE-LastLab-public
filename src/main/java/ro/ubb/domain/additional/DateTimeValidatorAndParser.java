package ro.ubb.domain.additional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ro.ubb.domain.validators.ValidatorException;

public class DateTimeValidatorAndParser {

    private static final String EX_LUNA_INTRE_1_12 = "Luna trebuie sa fie mai mare sau egala cu 1 si mai mica sau egala cu 12\n";
    private static final String EX_FEB_BISECT = "Februarie nu poate avea mai mult de 28 de zile\n" +
            "Anul introdus nu este bisect pentru luna Februarie\n";

    private static final String PUNCTUATION = "[\\.\\\\,/\\-:\\s_;+`]";
    private static String localDateRegex = "^\\d{4}"+PUNCTUATION+"\\d{1,2}"+PUNCTUATION+"\\d{1,2}";
    private static String dateValidatorRegEx = "^\\d{1,2}" + PUNCTUATION + "\\w{3}" + PUNCTUATION + "(?:\\d{2}|\\d{4})";
    private static String dateValidatorRegEx2 = "^\\d{1,2}" + PUNCTUATION + "\\d{1,2}" + PUNCTUATION + "(?:\\d{2}|\\d{4})";
    private static String timeValidatorRegEx = "^\\d{1,2}" + PUNCTUATION + "\\d{1,2}";
    private static String timeValidatorRegEx2 = "^\\d{1,2}" + PUNCTUATION + "\\d{1,2}" + PUNCTUATION + "\\d{1,2}";
    private static String months = "ian Ian IAN jan Jan JAN feb Feb FEB mar Mar MAR apr Apr APR mai Mai MAI may May MAY iun Iun IUN jun Jun JUN" +
            "iul Iul IUL jul Jul JUL aug Aug AUG sep Sep SEP oct Oct OCT noi Noi NOI nov Nov NOV dec Dec DEC";
    private static String monthsWith30days = "apr iun jun sept sep nov";
    private static Tuple datePattern_Matcher;
    private static LocalTime timeActual = LocalTime.of(1, 1, 0);
    private static LocalDate dateActual = LocalDate.of(1,1,1);

    private static Tuple<Pattern, Matcher> inputDatePattern(String date, Integer monthIndexForPatterns,
                                                            String monthsToMatch){
        String[] dateTokens = date.split(PUNCTUATION);
        Pattern monthPattern = Pattern.compile(dateTokens[monthIndexForPatterns]);
        Matcher monthMatcher = monthPattern.matcher(monthsToMatch);
        return new Tuple<>(monthPattern, monthMatcher);
    }

    public static void validate(String date, String time){
        validateDate(date);
        validateTime(time);
    }

    private static boolean boolFeb_bisect(String day, String year) {
        if ((Integer.parseInt(day) > 28)
            && (!(Integer.parseInt(year) % 4 == 0) && !(Integer.parseInt(year) % 400 == 0)
            || (Integer.parseInt(year) % 100 == 0) && !(Integer.parseInt(year) % 400 == 0)))
        return true;
        else return false;
    }

    private static void validateDate(String date) {
        String[] dateTokens = date.split(PUNCTUATION);
        List<Integer> monthsDigits = new ArrayList<>();
        for (int i = 1; i < 13; i++) monthsDigits.add(i);
        List<Integer> monthsDigitsWith30days = new ArrayList<>(Arrays.asList(4, 6, 9, 11));
        if (!(!date.matches(dateValidatorRegEx) ^ !date.matches(dateValidatorRegEx2))) {
            if (!date.matches(localDateRegex)) {
            throw new RuntimeException("==Inputul este gol sau formatul introdus este gresit===\n" +
                    "Formatul datei trebuie sa respecte:\n" +
                    "Ziua Luna Anul despartit de oricare dintre semnele de punctuatie : \\ / . - ,\n" +
                    "Formatarile permise propriuzis sunt\n" +
                    "\"dd.lll.yy\" Ziua Luna(format scurt - 3 litere) Anul(format scurt 2 cifre)\n" +
                    "\"dd.lll.yyyy\" Ziua Luna(format scurt 3 litere) Anul(format intreg)\n" +
                    "\"dd.mm.yy\" Ziua Luna(1 sau 2 cifre) Anul(format scurt 2 cifre)\n" +
                    "\"dd.mm.yyyy\" Ziua Luna(1 sau 2 cifre) Anul(format intreg)\n");
            }
        }
        if (date.matches(localDateRegex)) {
            if (Integer.parseInt(dateTokens[2]) > 31) throw new RuntimeException("Nici o zi din an nu poate avea mai mult de 31 de zile!\n");
            if ((Integer.parseInt(dateTokens[1]) < 1) || (Integer.parseInt(dateTokens[1]) > 12)) {
                throw new RuntimeException(EX_LUNA_INTRE_1_12);
            }
            if (monthsDigitsWith30days.contains(Integer.parseInt(dateTokens[1])) && Integer.parseInt(dateTokens[2]) > 30){
                throw new RuntimeException("Ziua Lunii nu poate sa fie mai mare de 30\n" +
                        "pentru lunile " + monthsWith30days + "\n");
            }
            if ((Integer.parseInt(dateTokens[2]) < 1) || (Integer.parseInt(dateTokens[2]) > 31)) {
                throw new RuntimeException("Nici o zi din an nu poate depasi 31 de zile\n");
            }
            if ((Integer.parseInt(dateTokens[1]) == 2) && boolFeb_bisect(dateTokens[2],dateTokens[0])) {
                throw new RuntimeException(EX_FEB_BISECT);
            }
        }
        if (date.matches(dateValidatorRegEx)) {
            if (Integer.parseInt(dateTokens[0]) > 31) throw new RuntimeException("Nici o zi din an nu poate avea mai mult de 31 de zile!\n");
            if (!inputDatePattern(date,1,months).getSecond().find()) {
                throw new RuntimeException("Luna trebuie sa respecte formatul LLL (scurt al lunii) cele trei litere definitorii ale lunii\n" +
                        "Exemplu Ianuarie: IAN, Ian, ian (Romana)\n" +
                        "Exemplu January: JAN, Jan, jan (Engleza) \n");
            }
            if (inputDatePattern(date,1,monthsWith30days).getSecond().find() && Integer.parseInt(dateTokens[0]) > 30) {
                throw new RuntimeException("Ziua Lunii nu poate sa fie mai mare de 30\n" +
                        "pentru lunile " + monthsWith30days + "\n");
            }
            if (dateTokens[1].matches("Feb") && boolFeb_bisect(dateTokens[0], dateTokens[2])) {
                throw new RuntimeException(EX_FEB_BISECT);
            }
        }
        if (date.matches(dateValidatorRegEx2)) {
            if ((Integer.parseInt(dateTokens[1]) < 1) || (Integer.parseInt(dateTokens[1]) > 12)) {
                throw new RuntimeException(EX_LUNA_INTRE_1_12);
            }
            if ((Integer.parseInt(dateTokens[0]) < 1) || (Integer.parseInt(dateTokens[0]) > 31)) {
                throw new RuntimeException("Nici o zi din an nu poate depasi 31 de zile\n");
            }
            if (monthsDigitsWith30days.contains(Integer.parseInt(dateTokens[1])) && Integer.parseInt(dateTokens[0]) > 30) {
                throw new RuntimeException("Ziua Lunii nu poate sa fie mai mare de 30\n" +
                        "pentru lunile " + monthsWith30days + "\n");
            }
            if ((Integer.parseInt(dateTokens[1]) == 2) && boolFeb_bisect(dateTokens[0],dateTokens[2])) {
                throw new RuntimeException(EX_FEB_BISECT);
            }
        }
    }


    public static void validateTime(String time){
        String timeValidatorRegEx = "^\\d{1,2}" + PUNCTUATION + "\\d{1,2}";
        String timeValidatorRegEx2 = "^\\d{1,2}" + PUNCTUATION + "\\d{1,2}" + PUNCTUATION + "\\d{1,2}";
        if (!(!time.matches(timeValidatorRegEx) ^ !time.matches(timeValidatorRegEx2))) {
            throw new RuntimeException("Stringul Orei trebuie sa respecte formatul:\n" +
                    "Ora Minute Secunde despartit de oricare dintre semnele de puctuatie : \\ / . - ,\n" +
                    "Formatarile permise propriuzis sunt\n" +
                    "\"HH.mm\" Ora Minute\n" +
                    "\"HH.mm.ss\" Ora Minute Secunde");
        }
        if (time.matches(timeValidatorRegEx)) {
            String[] timeTokens = time.split(PUNCTUATION);
            if (Integer.parseInt(timeTokens[0]) > 23 || Integer.parseInt(timeTokens[1]) > 59) {
                throw new RuntimeException("Ora nu poate depasi valoarea 23\n" +
                        "Minutele nu pot depasi valoarea 59\n");
            }
        }
        if (time.matches(timeValidatorRegEx2)) {
            String[] timeTokens = time.split(PUNCTUATION);
            if (Integer.parseInt(timeTokens[0]) > 23 || Integer.parseInt(timeTokens[1]) > 59 || Integer.parseInt(timeTokens[2]) > 59) {
                throw new RuntimeException("Ora nu poate depasi valoarea 23\n" +
                        "Minutele nu pot depasi valoarea 59\n" +
                        "Secundele nu pot depasi valoarea 59\n");
            }
        }
    }

    public static LocalDateTime parseDateTime(String date, String time) throws ValidatorException{
        validate(date,time);
        dateActual = parseDate(date);
        timeActual = parseTime(time);
        LocalDateTime dateTime = LocalDateTime.of(dateActual, timeActual);
        return dateTime;
    }

    public static LocalDate parseDate(String date){
        validateDate(date);
        String[] dateTokens = date.split(PUNCTUATION);
        DateTimeFormatter dateTimeFormatter;
        LocalDate dateActual = LocalDate.of(1,1,1);
        /**
         *  Matches per Month letter formatting cases
         */
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\w{3}") && dateTokens[2].matches("\\d{2}")){
            String dateString = "0"+dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd LLL yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\w{3}") && dateTokens[2].matches("\\d{4}")){
            String dateString = "0"+dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd LLL yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\w{3}") && dateTokens[2].matches("\\d{4}")){
            String dateString = dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd LLL yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\w{3}") && dateTokens[2].matches("\\d{2}")){
            String dateString = dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd LLL yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        /**
         * Matching cases and coorrections for Day digit String input, Month digit String input and Year digit String input
         * For cases of entering single digit, but valid input for Day and Month (Example 1-5: Day 1 Month 5)
         * Also for determining year format from the input String
         */
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\d{1}") && dateTokens[2].matches("\\d{2}")){
            String dateString = "0"+dateTokens[0] + " 0" + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\d{2}") && dateTokens[2].matches("\\d{2}")){
            String dateString = "0" + dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\d{1}") && dateTokens[2].matches("\\d{4}")){
            String dateString = "0" + dateTokens[0] + " 0" + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{1}") && dateTokens[1].matches("\\d{2}") && dateTokens[2].matches("\\d{4}")) {
            String dateString = "0" + dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\d{1}") && dateTokens[2].matches("\\d{2}")){
            String dateString = dateTokens[0] + " 0" + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\d{1}") && dateTokens[2].matches("\\d{4}")){
            String dateString = dateTokens[0] + " 0" + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\d{2}") && dateTokens[2].matches("\\d{2}")){
            String dateString = dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        if (dateTokens[0].matches("\\d{2}") && dateTokens[1].matches("\\d{2}") && dateTokens[2].matches("\\d{4}")){
            String dateString = dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        /////////////// Actual DateTIME FORMAT
        if (dateTokens[0].matches("\\d{4}") && dateTokens[1].matches("\\d{1,2}") && dateTokens[2].matches("\\d{1,2}")){
            String dateString = dateTokens[0] + " " + dateTokens[1] + " " + dateTokens[2];
            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd");
            dateActual = LocalDate.parse(dateString, dateTimeFormatter);
        }
        return dateActual;
    }

    public static LocalTime parseTime(String time) throws ValidatorException {
        validateTime(time);
        String[] timeTokens = time.split(PUNCTUATION);
        if (time.matches(timeValidatorRegEx)) {
            int hour = Integer.parseInt(timeTokens[0]);
            int minutes = Integer.parseInt(timeTokens[1]);
            timeActual = LocalTime.of(hour, minutes);
        }
        if (time.matches(timeValidatorRegEx2)) {
            int hour = Integer.parseInt(timeTokens[0]);
            int minutes = Integer.parseInt(timeTokens[1]);
            int seconds = Integer.parseInt(timeTokens[2]);
            timeActual = LocalTime.of(hour, minutes, seconds);
        }
        return timeActual;
    }


}
