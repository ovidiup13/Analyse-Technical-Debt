package com.td.helpers.analysis;

import java.util.Optional;

import com.td.models.CommitTD;
import com.td.models.TechnicalDebtPriority;
import com.td.models.CommitTD.CodeLocation;
import com.td.models.TechnicalDebtItem.CompositeKey;

class TechnicalDebtMapper {

    static final String SEPARATOR = ":";

    static Optional<CommitTD> parseFindBugsOutput(String line) {

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
    static CodeLocation parseLocation(String line) {
        int separatorIndex = line.lastIndexOf(SEPARATOR);
        int nameIndex = line.lastIndexOf("At ") + 3; // note the space
        int lastIndex = line.lastIndexOf("]");

        String fileName = line.substring(nameIndex, separatorIndex);
        int lineNumber = Integer.parseInt(line.substring(separatorIndex + 6, lastIndex).trim());

        return new CodeLocation(fileName, lineNumber);
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