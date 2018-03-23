package com.td.helpers.analysis;

import com.td.models.RepositoryModel;
import com.td.models.TechnicalDebt;

public interface StaticAnalysisHelper {
    TechnicalDebt executeAnalysis(RepositoryModel repository);
}