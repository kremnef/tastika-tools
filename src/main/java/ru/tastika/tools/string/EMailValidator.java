package ru.tastika.tools.string;


import java.util.regex.Pattern;


/**
 * Taken from http://www.leshazlewood.com/?p=5
 * <p/>
 * Date: 05.02.2008
 * Time: 17:27:20
 */
public class EMailValidator {


    private static final boolean ALLOW_DOMAIN_LITERALS = true;
    private static final boolean ALLOW_QUOTED_IDENTIFIERS = true;
    private static final String wsp = "[ \\t]"; //space or tab
    private static final String fwsp = wsp + "*";
    private static final String dquote = "\\\"";
    private static final String noWsCtl = "\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F";
    private static final String asciiText = "[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F]";
    private static final String quotedPair = "(\\\\" + asciiText + ")";
    private static final String atext = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]";
    private static final String atom = fwsp + atext + "+" + fwsp;
    private static final String dotAtomText = atext + "+" + "(" + "\\." + atext + "+)*";
    private static final String dotAtom = fwsp + "(" + dotAtomText + ")" + fwsp;
    private static final String qtext = "[" + noWsCtl + "\\x21\\x23-\\x5B\\x5D-\\x7E]";
    private static final String qcontent = "(" + qtext + "|" + quotedPair + ")";
    private static final String quotedString = dquote + "(" + fwsp + qcontent + ")*" + fwsp + dquote;
    private static final String word = "((" + atom + ")|(" + quotedString + "))";
    private static final String phrase = word + "+"; //one or more words.
    private static final String letter = "[a-zA-Z]";
    private static final String letDig = "[a-zA-Z0-9]";
    private static final String letDigHyp = "[a-zA-Z0-9-]";
    private static final String rfcLabel = letDig + "(" + letDigHyp + "{0,61}" + letDig + ")?";
    private static final String rfc1035DomainName = rfcLabel + "(\\." + rfcLabel + ")*\\." + letter + "{2,6}";
    private static final String dtext = "[" + noWsCtl + "\\x21-\\x5A\\x5E-\\x7E]";
    private static final String dcontent = dtext + "|" + quotedPair;
    private static final String domainLiteral = "\\[" + "(" + fwsp + dcontent + "+)*" + fwsp + "\\]";
    private static final String rfc2822Domain = "(" + dotAtom + "|" + domainLiteral + ")";
    private static final String domain = ALLOW_DOMAIN_LITERALS ? rfc2822Domain : rfc1035DomainName;
    private static final String localPart = "((" + dotAtom + ")|(" + quotedString + "))";
    private static final String addrSpec = localPart + "@" + domain;
    private static final String angleAddr = "<" + addrSpec + ">";
    private static final String nameAddr = "(" + phrase + ")?" + fwsp + angleAddr;
    private static final String mailbox = nameAddr + "|" + addrSpec;
    private static final String patternString = ALLOW_QUOTED_IDENTIFIERS ? mailbox : addrSpec;
    public static final Pattern VALID_PATTERN = Pattern.compile(patternString);


    public static boolean isValid(String userEnteredEmailString) {
        return VALID_PATTERN.matcher(userEnteredEmailString).matches();
    }

}
