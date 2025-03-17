//StringUtils.java
//Written by Jan Wigginton

package edu.umich.batchmatch.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringUtils {

	public static String removeNonAscii(String str) {
		return str.replaceAll("[^\\x00-\\x7F]", "");
	}

	public static String removeNonPrintable(String str) // All Control Char
	{
		return str.replaceAll("[\\p{C}]", "");
	}

	public static String removeSomeControlChar(String str) // Some Control Char
	{
		return str.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
	}

	public static String removeFullControlChar(String str) {
		return removeNonPrintable(str).replaceAll("[\\r\\n\\t]", "");
	}

	public static String camelToWords(String camelString) {
		String wordsString = "", oldCase = "lower";
		for (int c = 0; c < camelString.length(); c++) {
			Character letter = (camelString.charAt(c));
			boolean isUpper = letter.toString().toUpperCase().equals(letter.toString());
			String newCase = isUpper ? "upper" : "lower";
			if (c == 0)
				wordsString = letter.toString().toUpperCase();
			else if (c > 0 && !newCase.equals(oldCase))
				wordsString += " " + letter.toString().toUpperCase();
			else
				wordsString += letter.toString();
		}
		return wordsString;
	}

	// MERGE 06/09
	public static String capitalize(String str) {
		if (isEmptyOrNull(str))
			return "";

		if (str.length() == 1)
			return str.toUpperCase();

		String capitalizedStr = str.toUpperCase();

		return (capitalizedStr.charAt(0) + str.substring(1));
	}

	public static String safeToString(Object o) {
		return (o == null ? null : o.toString());
	}

	public static List<String> getAsArrayList(String str, String regEx) {
		List<String> tokenList = new ArrayList<String>();
		String[] tokens = splitAndTrim(str, regEx);
		for (int i = 0; i < tokens.length; i++)
			tokenList.add(tokens[i]);

		return tokenList;
	}

	public static List<String> getAsArrayList(String str) {
		List<String> tokenList = new ArrayList<String>();
		String[] tokens = splitAndTrim(str);
		for (int i = 0; i < tokens.length; i++)
			tokenList.add(tokens[i]);

		return tokenList;
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	public static boolean isNullOrEmpty(String str) {
		return isEmpty(str);
	}

	public static boolean isNonEmpty(String str) {
		return str != null && !"".equals(str.trim());
	}

	public static String[] subSpacesForTabs(String line) {
		return new String[5];
	}

	public static String[] splitAndTrim(String line) {
		return splitAndTrim(line, "\\s");
	}

	public static String[] splitAndTrim(String line, String regex) {
		return splitAndTrim(line, regex, false);
	}

	public static String[] splitAndTrim(String line, String regex, Boolean keepBlanks) {
		String[] rawTokens = line != null ? line.split(regex) : null;

		ArrayList<String> trimTokens = new ArrayList<String>();

		for (int i = 0; i < rawTokens.length; i++) {
			String val = rawTokens[i].trim();
			if (keepBlanks || val.length() > 0)
				trimTokens.add(val);
		}
		String[] processedTokens = new String[trimTokens.size()];

		for (int i = 0; i < trimTokens.size(); i++)
			processedTokens[i] = trimTokens.get(i);

		return processedTokens;
	}

	public static String nThTokenSplitOn(String strToSplit, String splitRegex, int targetToken) {
		String[] tokens = strToSplit != null ? strToSplit.split(splitRegex) : null;

		return StringUtils.safeNthToken(tokens, targetToken);
	}

	public static String lastTokenSplitOn(String strToSplit, String splitRegex) {
		String[] tokens = strToSplit != null ? strToSplit.split(splitRegex) : null;

		if (tokens == null)
			return null;

		int targetToken = tokens.length - 1;
		return StringUtils.safeNthToken(tokens, targetToken);
	}

	public static String safeNthToken(String[] tokens, int targetToken) {
		return (tokens != null && tokens.length > targetToken) ? tokens[targetToken] : null;
	}

	public static String buildDatabaseListFromList(List<String> lst) {
		StringBuilder sb = new StringBuilder();

		sb.append("('");
		int i = 0;
		for (String str : lst) {
			String extra = i == 0 ? "" : "', '";
			sb.append(extra + str);
			i++;
		}

		sb.append("')");

		return sb.toString();
	}

	public static String buildDatabaseListFromSet(Set<String> lst) {
		StringBuilder sb = new StringBuilder();

		sb.append("('");
		int i = 0;
		for (String str : lst) {
			String extra = i == 0 ? "" : "', '";
			sb.append(extra + str);
			i++;
		}

		sb.append("')");

		return sb.toString();
	}

	public static Boolean valueIsInt(String token) {
		try {
			Integer.parseInt(token);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static Boolean interpretAsBoolean(String value) {
		if (isEmptyOrNull(value))
			return null;

		String comp = value.toLowerCase();

		if ("false".equals(comp) || "f".equals(comp))
			return false;

		if ("no".equals(comp) || "n".equals(comp))
			return false;

		if ("0".equals(comp))
			return false;

		if ("true".equals(comp) || "t".equals(comp))
			return false;

		if ("yes".equals(comp) || "y".equals(comp))
			return false;

		if ("1".equals(comp))
			return false;

		return null;
	}

	public static BigDecimal interpretAsBigDecimal(String value) {
		if (isEmptyOrNull(value))
			return null;

		BigDecimal retVal = null;
		try {
			retVal = new BigDecimal(value);
		} catch (NumberFormatException e) {
		}

		return retVal;
	}

	public static Integer interpretAsInteger(String str) {
		Integer retVal = null;
		try {
			retVal = Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}

		return retVal;
	}

	public static Double interpretAsDouble(String str) {
		Double retVal = null;
		try {
			retVal = Double.parseDouble(str);
		} catch (NumberFormatException e) {
		}

		return retVal;
	}

	public static boolean isEmptyOrNull(String value) {
		return checkEmptyOrNull(value);
	}

	public static Boolean valueIsDouble(String token) {
		try {
			Double.parseDouble(token);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static Integer extractIntegerPortion(String str, int i) throws Exception {
		Integer value = 0;

		try {
			value = Integer.parseInt(str.replaceAll("[^\\d]", ""));
		} catch (Exception e) {
			throw new Exception("Error while parsing integer from string");
		}

		return value;
	}

	public static boolean checkEmptyOrNull(String checked) {
		return checked == null || checked.trim().equals("");
	}

	public static boolean trimEquals(String str1, String str2) {
		if (str1 == null && str2 == null)
			return true;

		if (str1 == null || str2 == null)
			return false;

		return str1.trim().equals(str2.trim());
	}

	public static String makeAlertMessage(String msg) {
		return "alert('" + msg + "');";
	}

	public static String grabRandomDigitString(int nDigits) {
		Integer digit = (int) Math.floor(Math.random() * 10);
		String digitString = "";

		for (int i = 0; i < nDigits; i++) {
			digitString += digit.toString();
			digit = (int) Math.floor(Math.random() * 10);
		}
		return digitString;
	}

	public static String grabRandomLetterString(int nLetters) {
		return grabRandomLetterString(nLetters, false);
	}

	public static String grabRandomLetterString(int nLetters, boolean forceUpperCase) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < nLetters; i++)
			sb.append(rndChar());

		return (forceUpperCase ? sb.toString().toUpperCase() : sb.toString());
	}

	public static char rndChar() {
		int rnd = (int) (Math.random() * 52); // or use Random or whatever
		char base = (rnd < 26) ? 'A' : 'a';
		return (char) (base + rnd % 26);
	}

	public static Integer colForExcelAddress(String addr) {
		if (isEmptyOrNull(addr))
			return null;
		String letterPrefix = addr.replaceAll("[\\d]", "");
		if (letterPrefix.length() < 1)
			return null;

		Integer sum = 0;

		for (int i = 0; i < letterPrefix.length(); i++) {
			sum *= 26;
			sum += (letterPrefix.charAt(i) - 'A' + 1);
		}

		return sum;
	}

	public static String removeSpaces(String input) {
		if (input == null)
			return "";
		return input.replaceAll("[\\s]", "");
	}

	public static String removeParens(String input) {
		if (input == null)
			return "";

		String temp = input.replaceAll("\\)", "");

		return temp.replaceAll("\\(", "");
	}

	public static String removeUnderscores(String input) {
		if (input == null)
			return "";
		return input.replaceAll("\\_", "");
	}

	public static String removeCommas(String input) {
		if (input == null)
			return "";

		return input.replaceAll("\\,", "");
	}

	public static String removeSlashes(String input) {
		if (input == null)
			return "";

		return input.replaceAll("\\/", "");
	}

	public static String removeDashes(String input) {
		if (input == null)
			return "";

		return input.replaceAll("\\-", "");
	}

	public static String removeAsterisks(String input) {
		if (input == null)
			return "";

		return input.replaceAll("\\*", "");
	}

	// MERGE 06/09
	public static String removeTrailingWhiteSpace(String input) {
		if (input == null)
			return "";

		return input.replaceAll("\\s+$", "");
	}

	public static String cleanAndTrim(String input) {
		if (isEmpty(input))
			return "";

		String temp = removeCommas(input);
		String temp2 = removeUnderscores(temp);
		String temp3 = removeParens(temp2);
		String temp4 = removeSpaces(temp3);
		String temp5 = removeDashes(temp4);
		String temp6 = removeAsterisks(temp5);

		return removeSlashes(temp6);
	}

	public static String trimPhrase(String input, String phrase) {
		if (input == null || phrase == null)
			return "";

		String temp = input;
		if (temp.startsWith(phrase))
			temp = input.substring(0, phrase.length());

		if (temp.endsWith(phrase))
			return temp.substring(temp.length() - phrase.length() - 1, temp.length());

		return temp;
	}

	public static boolean isMemberOfStringArray(String choice, String[] choices) {
		for (int i = 0; i < choices.length; i++) {
			if (choice.equalsIgnoreCase(choices[i])) {
				return true;
			}
		}
		return false;
	}

}