package ru.tastika.tools.string;


/**
 * User: osa
 * Date: 04.05.2008
 * Time: 12:25:02
 */
public class SimpleReplace {


    public static String replace(final String aInput, final String aOldPattern, final String aNewPattern) {
        if (aOldPattern.equals("")) {
            throw new IllegalArgumentException("Old pattern must have content.");
        }

        final StringBuffer result = new StringBuffer();
        //startIdx and idxOld delimit various chunks of aInput; these
        //chunks always end where aOldPattern begins
        int startIdx = 0;
        int idxOld = 0;
        while ((idxOld = aInput.indexOf(aOldPattern, startIdx)) >= 0) {
            //grab a part of aInput which does not include aOldPattern
            result.append(aInput.substring(startIdx, idxOld));
            //add aNewPattern to take place of aOldPattern
            result.append(aNewPattern);

            //reset the startIdx to just after the current match, to see
            //if there are any further matches
            startIdx = idxOld + aOldPattern.length();
        }
        //the final chunk will go to the end of aInput
        result.append(aInput.substring(startIdx));
        return result.toString();
    }


}
