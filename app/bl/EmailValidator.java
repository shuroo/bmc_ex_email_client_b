package bl;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.*;
/**
 * Basic client email validation class.
 */
public class EmailValidator {


    private static final String EMAIL_REGEX = Constants.EMAIL_REGEX;
    private static Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private static Boolean validateEmailAddress(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * Basic email validation for the sent email addresses
     *
     * @param emailFrom - The email source
     * @param emailTo   - The email Destination
     * @return Boolean - True for success, False for Failure
     */
    public static Boolean validateEmailAddresses(String emailFrom, String emailTo) {
        return validateEmailAddress(emailFrom) && validateEmailAddress(emailTo);
    }
}