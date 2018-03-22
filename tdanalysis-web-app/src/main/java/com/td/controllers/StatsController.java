package com.td.controllers;

import com.td.facades.StatsFacade;
import com.td.models.CommitStats;
import com.td.models.IssueStats;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController extends BaseController {

    @Autowired
    private StatsFacade statsFacade;

    @GetMapping("/repos/{id}/stats/tickets/by-commit")
    public List<IssueStats> getTicketStatsByCommitTimestamp(@PathVariable String id) {
        return this.statsFacade.getIssueStatsByCommitTimestamp(id);
    }

    @GetMapping("/repos/{id}/stats/tickets/by-ticket")
    public List<IssueStats> getTicketStatsByTicketTimestamp(@PathVariable String id) {
        return this.statsFacade.getIssueStatsByIssueTimestamp(id);
    }

    @GetMapping("/repos/{id}/stats/tickets/raw")
    public List<IssueStats> getIssueStatsRaw(@PathVariable String id) {
        return this.statsFacade.getIssueStatsRaw(id);
    }

    @GetMapping("/repos/{id}/stats/commits")
    public CommitStats getCommitStats(@PathVariable String id) {
        return this.statsFacade.getCommitStats(id);
    }

}