package com.td.helpers.analysis;

import java.util.Optional;

import com.td.models.CommitTD;
import com.td.models.TechnicalDebtPriority;
import com.td.models.CommitTD.CodeLocation;
import com.td.models.TechnicalDebtItem.CompositeKey;

public class TechnicalDebtMapper {

    static final String SEPARATOR = ":";

    // denotes lines
    static final String SEPARATOR_AT = "at ";

    // denotes class
    static final String SEPARATOR_IN = "in ";

    public static Optional<CommitTD> parseFindBugsOutput(String line) {

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
        default:
            return TechnicalDebtPriority.LOW;
        }
    }

}