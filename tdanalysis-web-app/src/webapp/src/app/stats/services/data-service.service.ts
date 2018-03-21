import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs/Observable";

// models
import { Repository } from "../models/repository.interface";

// env
import { environment } from "../../../environments/environment.dev";
import { Issue } from "../models/issue.interface";
import { Commit, CommitMap } from "../models/commit.interface";
import { IssueStats, CommitStats } from "../models/stats.interface";

@Injectable()
export class DataService {
  private static API: string = environment.api;

  constructor(private http: HttpClient) {}

  /**
   * Returns metadata about the repository.
   * @param id of the repository
   */
  getRepositories(): Observable<Repository[]> {
    return this.http.get<Repository[]>(`${DataService.API}/repos`);
  }

  /**
   * Returns metadata about the repository.
   * @param id of the repository
   */
  getRepository(id: string): Observable<Repository> {
    return this.http.get<Repository>(`${DataService.API}/repos/${id}`);
  }

  /**
   * Returns all issues associated with the repository.
   * @param id of the repository
   */
  getIssues(id: string): Observable<Issue[]> {
    return this.http.get<Issue[]>(`${DataService.API}/repos/${id}/issues`);
  }

  /**
   * Returns metadata about the issue.
   * @param repoId repository ID
   * @param issueId issue ID
   */
  getIssue(repoId: string, issueId: string): Observable<Issue> {
    return this.http.get<Issue>(
      `${DataService.API}/repos/${repoId}/issues/${issueId}`
    );
  }

  /**
   * Returns all commits from the repository, ordered by timestamp.
   * @param id of the repository
   */
  getCommits(id: string): Observable<Commit[]> {
    return this.http.get<Commit[]>(`${DataService.API}/repos/${id}/commits`);
  }

  /**
   * Returns metadata about a specific commit (revision) of the repository.
   * @param repoId repository ID
   * @param sha commit SHA
   */
  getCommit(repoId: string, sha: string): Observable<Commit> {
    return this.http.get<Commit>(
      `${DataService.API}/repos/${repoId}/commits/${sha}`
    );
  }

  /**
   * Returns all commits from the repository referencing the specified issue.
   * @param repoId id of the repository
   * @param issueId id of the ticket from issue tracker
   */
  getCommitsByIssue(repoId: string, issueId: string) {
    return this.http.get<Commit[]>(
      `${DataService.API}/repos/${repoId}/issues/${issueId}/commits`
    );
  }

  /**
   * Returns an object of all issues and their associated commits.
   * @param repoId
   */
  getCommitsByIssues(repoId: string): Observable<CommitMap[]> {
    return this.http.get<CommitMap[]>(
      `${DataService.API}/repos/${repoId}/issue-commits`
    );
  }

  /**
   * Returns stats for a specific repository. Includes work effort and
   * technical debt data. Results are filtered by number of commits (>1).
   * @param repoId repository ID
   */
  getTicketStats(repoId: string): Observable<IssueStats[]> {
    return this.http.get<IssueStats[]>(
      `${DataService.API}/repos/${repoId}/stats/tickets`
    );
  }

  /**
   * Returns a list of stats for all tickets. Stats include total commits only.
   * Results are NOT filtered by number of commits.
   * @param repoId
   */
  getTicketStatsRaw(repoId: string): Observable<IssueStats[]> {
    return this.http.get<IssueStats[]>(
      `${DataService.API}/repos/${repoId}/stats/tickets/raw`
    );
  }

  /**
   * Returns stats related to commits from the repository.
   * @param repoId repository ID
   */
  getCommitStats(repoId: string): Observable<CommitStats> {
    return this.http.get<CommitStats>(
      `${DataService.API}/repos/${repoId}/stats/commits`
    );
  }
}
