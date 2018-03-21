export interface IssueStats {
  issueKey: string;
  workEffort: number;
  technicalDebt: number;
  totalCommits: number;
  author: string;
}

export interface CommitStats {
  totalCommits: number;
  commitsWithIssues: number;
  commitsWithoutIssues: number;
  numberOfAuthors: number;
  meanTicketsPerCommit: number;
  meanTDItemsPerCommit: number;
}

export interface SimpleStats {
  mean: number;
  std: number;
}
