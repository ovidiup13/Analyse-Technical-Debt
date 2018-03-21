export interface Commit {
  sha: string;
  repositoryId: string;
  issueIds: string[];
  timestamp: Date;
  message: string;
  author: string;
  buildStatus: string;
  diff: {
    totalChanges: number;
    additions: number;
    deletions: number;
    modifications: number;
    additionSet: string[];
    deletionSet: string[];
    modificationSet: string[];
  };
  bugs?: String[];
}

export interface CommitMap {
  [key: string]: Commit[];
}
