package com.td.helpers.analysis;

import java.util.Optional;

import com.td.models.CommitTD;
import com.td.models.TechnicalDebtPriority;
import com.td.models.CommitTD.CodeLocation;
import com.td.models.TechnicalDebtItem.CompositeKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicalDebtMapper {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalDebtMapper.class);

    static final String SEPARATOR = ":";

    // denotes lines
    static final String SEPARATOR_AT = "at ";

    // denotes class
    static final String SEPARATOR_IN = "in ";

    public static Optional<CommitTD> parseFindBugsOutput(String line) {

        try {
            CommitTD result = new CommitTD();
            int firstIndex = line.indexOf(SEPARATOR);

            if (firstIndex < 0) {
                return Optional.empty();
            }

            String[] codes = line.substring(0, firstIndex).split(" ");

            String categoryInitial = codes[1];
            String issueCode = codes[2];
            CodeLocation location = parseLocation(line);
            TechnicalDebtPriority priority = getPriority(codes[0]);

            result.setId(new CompositeKey(categoryInitial, issueCode));
            result.setLocation(location);
            result.setPriority(priority);

            return Optional.of(result);
        } catch (Exception e) {
            /**
             * I know this is generic, and things like this should not appear in
             * the code. However, findbugs output is a mess and it extremely
             * difficult to parse. There might be some lines of the output which
             * are weird and not covered by the parsing algorithm.
             *
             * This try-catch block aims to cover these issues and keep the
             * thread running in case of an exception. The line will be ignored
             * in such cases.
             */
            logger.error("An error occurred while parsing findbugs output.");
            logger.error("Line error: " + line);
            return Optional.empty();
        }
    }

    /**
     * Parses a line of findbugs output to CodeLocation object.
     * E.g. M D NP: executor must be non-null but is marked as nullable  At AsyncCompleter.java:[line 287]
     */
    public static CodeLocation parseLocation(String line) {
        String lowerCaseLine = line.toLowerCase();
        int separatorAt = lowerCaseLine.lastIndexOf(SEPARATOR_AT);
        int separatorIn = lowerCaseLine.lastIndexOf(SEPARATOR_IN);

        if (separatorAt < 0) {
            return parseClassLocation(line.substring(separatorIn));
        }

        return parseCodeLocation(line.substring(separatorAt));
    }

    static CodeLocation parseClassLocation(String line) {
        String className = line.split(" ")[1].trim();
        return new CodeLocation(className, null);
    }

    static CodeLocation parseCodeLocation(String line) {
        int separatorIndex = line.lastIndexOf(SEPARATOR);
        int nameIndex = line.indexOf("At ") + 3; // note the space
        int bracketOpenIndex = line.indexOf("[");
        int bracketClosedIndex = line.indexOf("]");

        String className = line.substring(nameIndex, separatorIndex).trim();
        String location = line.substring(bracketOpenIndex + 1, bracketClosedIndex).split(" ")[1];

        return new CodeLocation(className, location);
    }

    static TechnicalDebtPriority getPriority(String c) {
        switch (c) {
        case "H":
            return TechnicalDebtPriority.HIGH;
        case "M":
            return TechnicalDebtPriority.MEDIUM;
        case "L":
        default:
            return TechnicalDebtPriority.LOW;
        }
    }

}