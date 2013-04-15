 import java.util.Locale;

/**
 * Demonstrating proper usage of Java's {@linkplain Locale} class
 * in respect to accessing user's regional settings, specifically
 * the locale configured for date/time/number formatting.
 *
 * Prior to Java 7 the call to {@linkplain Locale#getDefault()}
 * returned the display/format settings the user configured
 * for him/her. For example, on Windows XP there is a Contol Panel
 * section called "Regional Settings" where one could select and/or
 * customize the location, language (encoding/code page) and
 * date/time/number/currency formatting settings to his/her liking.
 *
 * This changed with recent versions of Windows and Java had to
 * follow suite - a change was introduced in Java 7 release that
 * broke existing behavior of {@linkplain Locale}.
 *
 * @see Locale#getDefault(java.util.Locale.Category)
 * @see Locale#getDefault()
 * @since Java 7
 */
public class DefaultLocaleChangesJava7Demo {
  private static final String NO_VALUE = "<- empty ->";
  private static final String[] ENVVAR_NAMES = {
      "java.runtime.name", "java.runtime.version", "java.vm.name", "java.vm.version",
      "os.name", "os.version", "os.arch",
      "user.language", "user.country", "user.script", "user.variant",
      "user.language.format", "user.country.format", "user.script.format" };

  public static void main(String[] args) {
    // Print out select environment variables.
    System.out.println("System Information (environment):");
    for (String name : ENVVAR_NAMES) {
      String value = System.getProperty(name);
      if (value == null || value.length() == 0) {
        value = NO_VALUE;
      }
      System.out.println("  " + name + " = " + value);
    }

    // Print out default display locale information.
    System.out.println("\nDefault Display Locale:");
    final Locale defaultLocale = Locale.getDefault();
    System.out.println(localeToString(defaultLocale, "  default."));

    // Print out default format locale information.
    System.out.println("\nDefault Format Locale:");
    final Locale formatLocale = Locale.getDefault(Locale.Category.FORMAT);
    System.out.println(localeToString(formatLocale, "  format."));
  }

  public static String localeToString(Locale locale, String prefix) {
    final StringBuffer result = new StringBuffer();

    result.append(prefix);
    result.append("locale.language = ");
    result.append(locale.getLanguage());
    result.append(" (");
    result.append(locale.getDisplayLanguage());
    result.append(")\n");

    result.append(prefix);
    result.append("locale.country = ");
    result.append(locale.getCountry());
    result.append(" (");
    result.append(locale.getDisplayCountry());
    result.append(")\n");

    result.append(prefix);
    result.append("locale.script = ");
    result.append(locale.getScript());
    result.append(" (");
    result.append(locale.getDisplayScript());
    result.append(")\n");

    result.append(prefix);
    result.append("locale.variant = ");
    result.append(locale.getVariant());
    result.append(" (");
    result.append(locale.getDisplayVariant());
    result.append(")\n");

    return result.toString();
  }
}
