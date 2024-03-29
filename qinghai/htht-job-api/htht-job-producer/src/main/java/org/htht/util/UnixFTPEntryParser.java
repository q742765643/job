package org.htht.util;

import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;
import org.apache.commons.net.ftp.parser.FTPTimestampParserImpl;

public class UnixFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {


    static final String DEFAULT_DATE_FORMAT
        = "MMM d yyyy"; //Nov 9 2001

    static final String DEFAULT_RECENT_DATE_FORMAT
        = "MMM d HH:mm"; //Nov 9 20:06

    static final String NUMERIC_DATE_FORMAT
        = "yyyy-MM-dd HH:mm"; //2001-11-09 20:06

    // Suffixes used in Japanese listings after the numeric values
    private static final String JA_MONTH = "\u6708";
    private static final String JA_DAY   = "\u65e5";
    private static final String JA_YEAR  = "\u5e74";

    private static final String DEFAULT_DATE_FORMAT_JA
        = "M'" + JA_MONTH + "' d'" + JA_DAY + "' yyyy'" + JA_YEAR + "'"; //

    private static final String DEFAULT_RECENT_DATE_FORMAT_JA
        = "M'" + JA_MONTH + "' d'" + JA_DAY + "' HH:mm"; //

    /**
     * Some Linux distributions are now shipping an FTP server which formats
     * file listing dates in an all-numeric format:
     * <code>"yyyy-MM-dd HH:mm</code>.
     * This is a very welcome development,  and hopefully it will soon become
     * the standard.  However, since it is so new, for now, and possibly
     * forever, we merely accomodate it, but do not make it the default.
     * <p>
     * For now end users may specify this format only via
     * <code>UnixFTPEntryParser(FTPClientConfig)</code>.
     * Steve Cohen - 2005-04-17
     */
    public static final FTPClientConfig NUMERIC_DATE_CONFIG =
        new FTPClientConfig(
                FTPClientConfig.SYST_UNIX,
                NUMERIC_DATE_FORMAT,
                null);

    /**
     * this is the regular expression used by this parser.
     *
     * Permissions:
     *    r   the file is readable
     *    w   the file is writable
     *    x   the file is executable
     *    -   the indicated permission is not granted
     *    L   mandatory locking occurs during access (the set-group-ID bit is
     *        on and the group execution bit is off)
     *    s   the set-user-ID or set-group-ID bit is on, and the corresponding
     *        user or group execution bit is also on
     *    S   undefined bit-state (the set-user-ID bit is on and the user
     *        execution bit is off)
     *    t   the 1000 (octal) bit, or sticky bit, is on [see chmod(1)], and
     *        execution is on
     *    T   the 1000 bit is turned on, and execution is off (undefined bit-
     *        state)
     *    e   z/OS external link bit
     *    Final letter may be appended:
     *    +   file has extended security attributes (e.g. ACL)
     *    Note: local listings on MacOSX also use '@';
     *    this is not allowed for here as does not appear to be shown by FTP servers
     *    {@code @}   file has extended attributes
     */
    //11-24-18  11:00AM            219319156 AQUA_X_2018_11_20_15_01_A_Prj_ModisDAY01_500_250.ld3
    private static final String REGEX =
    		"((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S+\\s+\\S+)|(?:\\S+))\\s+";
//    		  "(?:\\\\d+[-/]\\\\d+[-/]\\\\d+)"// yyyy-mm-dd
//    		+ "\\s+" 				// separator
//    		+ "(?:\\d+(?::\\d+)?)" 	// (20)
//    		+ "\\s+"  				// separator
//    		+ "(\\d+(?:,\\s*\\d+)?)"  // size 
//    		+ "(.*)"; 				// the rest (21)
//  separator
//        +"(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?" // permissions
//
//        + "\\s*"                                        // separator TODO why allow it to be omitted??
//
//        + "(\\d+)"                                      // link count
//
//        + "\\s+" // separator
//
//        + "(?:(\\S+(?:\\s\\S+)*?)\\s+)?"                // owner name (optional spaces)
//        + "(?:(\\S+(?:\\s\\S+)*)\\s+)?"                 // group name (optional spaces)
//        + "(\\d+(?:,\\s*\\d+)?)"                        // size or n,m
//
//        + "\\s+" // separator
//
//        /*
//         * numeric or standard format date:
//         *   yyyy-mm-dd (expecting hh:mm to follow)
//         *   MMM [d]d
//         *   [d]d MMM
//         *   N.B. use non-space for MMM to allow for languages such as German which use
//         *   diacritics (e.g. umlaut) in some abbreviations.
//         *   Japanese uses numeric day and month with suffixes to distinguish them
//         *   [d]dXX [d]dZZ
//        */
//        + "("+
//            "(?:\\d+[-/]\\d+[-/]\\d+)" + // yyyy-mm-dd
//            "|(?:\\S{3}\\s+\\d{1,2})" +  // MMM [d]d
//            "|(?:\\d{1,2}\\s+\\S{3})" + // [d]d MMM
//            "|(?:\\d{1,2}" + JA_MONTH + "\\s+\\d{1,2}" + JA_DAY + ")"+
//           ")"
//
//        + "\\s+" // separator

        /*
           year (for non-recent standard format) - yyyy
           or time (for numeric or recent standard format) [h]h:mm
           or Japanese year - yyyyXX
        */
//        + "((?:\\d+(?::\\d+)?)|(?:\\d{4}" + JA_YEAR + "))" // (20)
//
//        + "\\s" // separator

//        + "(.*)"; // the rest (21)


    // if true, leading spaces are trimmed from file names
    // this was the case for the original implementation
    final boolean trimLeadingSpaces; // package protected for access from test code

    /**
     * The default constructor for a UnixFTPEntryParser object.
     *
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     */
    public UnixFTPEntryParser()
    {
        this(null);
    }

    /**
     * This constructor allows the creation of a UnixFTPEntryParser object with
     * something other than the default configuration.
     *
     * @param config The {@link FTPClientConfig configuration} object used to
     * configure this parser.
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     * @since 1.4
     */
    public UnixFTPEntryParser(FTPClientConfig config)
    {
        this(config, false);
    }

    /**
     * This constructor allows the creation of a UnixFTPEntryParser object with
     * something other than the default configuration.
     *
     * @param config The {@link FTPClientConfig configuration} object used to
     * configure this parser.
     * @param trimLeadingSpaces if {@code true}, trim leading spaces from file names
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     * @since 3.4
     */
    public UnixFTPEntryParser(FTPClientConfig config, boolean trimLeadingSpaces)
    {
        super(REGEX);
        configure(config);
        this.trimLeadingSpaces = trimLeadingSpaces;
    }

    /**
     * Preparse the list to discard "total nnn" lines
     */
    @Override
    public List<String> preParse(List<String> original) {
        ListIterator<String> iter = original.listIterator();
        while (iter.hasNext()) {
            String entry = iter.next();
            if (entry.matches("^total \\d+$")) { // NET-389
                iter.remove();
            }
        }
        return original;
    }

    /**
     * Parses a line of a unix (standard) FTP server file listing and converts
     * it into a usable format in the form of an <code> FTPFile </code>
     * instance.  If the file listing line doesn't describe a file,
     * <code> null </code> is returned, otherwise a <code> FTPFile </code>
     * instance representing the files in the directory is returned.
     *
     * @param entry A line of text from the file listing
     * @return An FTPFile instance corresponding to the supplied entry
     */
    @Override
    public FTPFile parseFTPEntry(String entry) {
        FTPFile file = new FTPFile();
        file.setRawListing(entry);
        int type;
        boolean isDevice = false;
        if (entry.endsWith("bin")) {
			return file;
		}
        String[] group = entry.split(" ");
        
//        if (matches(entry))
//        {
        //默认group=16
            String typeStr = "-";
            String hardLinkCount = group(15);
            String usr = "";
            String grp = "";
            String filesize = group[group.length-2];
            String datestr = group[0] + " " + group[2];
            String name = group[group.length-1];                    
            if (trimLeadingSpaces) {
                name = name.replaceFirst("^\\s+", "");
            }

            // bcdelfmpSs-
            switch (typeStr.charAt(0))
            {
            case 'd':
                type = FTPFile.DIRECTORY_TYPE;
                break;
            case 'e': // NET-39 => z/OS external link
                type = FTPFile.SYMBOLIC_LINK_TYPE;
                break;
            case 'l':
                type = FTPFile.SYMBOLIC_LINK_TYPE;
                break;
            case 'b':
            case 'c':
                isDevice = true;
                type = FTPFile.FILE_TYPE; // TODO change this if DEVICE_TYPE implemented
                break;
            case 'f':
            case '-':
                type = FTPFile.FILE_TYPE;
                break;
            default: // e.g. ? and w = whiteout
                type = FTPFile.UNKNOWN_TYPE;
            }

            file.setType(type);

            //int g = 4;
//            for (int access = 0; access < 3; access++, g += 4)
//            {
                // Use != '-' to avoid having to check for suid and sticky bits
//                file.setPermission(access, FTPFile.READ_PERMISSION,
//                                   (!group(g).equals("-")));
//                file.setPermission(access, FTPFile.WRITE_PERMISSION,
//                                   (!group(g + 1).equals("-")));

//                String execPerm = group(g + 2);
//                if (!execPerm.equals("-") && !Character.isUpperCase(execPerm.charAt(0)))
//                {
//                    file.setPermission(access, FTPFile.EXECUTE_PERMISSION, true);
//                }
//                else
//                {
//                    file.setPermission(access, FTPFile.EXECUTE_PERMISSION, false);
//                }
//            }

  //          if (!isDevice)
   //         {
    //            try
   //             {
  //                  file.setHardLinkCount(Integer.parseInt(hardLinkCount));
 //               }
 //               catch (NumberFormatException e)
 //               {
                    // intentionally do nothing
 //               }
 //           }

            file.setUser(usr);
            file.setGroup(grp);

            try
            {
                file.setSize(Long.parseLong(filesize));
            }
            catch (NumberFormatException e)
            {
                // intentionally do nothing
            }

            // oddball cases like symbolic links, file names
            // with spaces in them.
            if (type == FTPFile.SYMBOLIC_LINK_TYPE)
            {

                int end = name.indexOf(" -> ");
                // Give up if no link indicator is present
                if (end == -1)
                {
                    file.setName(name);
                }
                else
                {
                    file.setName(name.substring(0, end));
                    file.setLink(name.substring(end + 4));
                }

            }
            else
            {
                file.setName(name);
            }
            return file;
        }
//        return null;
//    }

    /**
     * Defines a default configuration to be used when this class is
     * instantiated without a {@link  FTPClientConfig  FTPClientConfig}
     * parameter being specified.
     * @return the default configuration for this parser.
     */
    @Override
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(
                FTPClientConfig.SYST_UNIX,
                DEFAULT_DATE_FORMAT,
                DEFAULT_RECENT_DATE_FORMAT);
    }


}
