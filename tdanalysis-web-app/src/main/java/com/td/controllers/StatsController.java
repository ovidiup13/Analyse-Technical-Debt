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

    @GetMapping("/repos/{id}/stats/tickets")
    public List<IssueStats> getSimpleStats(@PathVariable String id) {
        return this.statsFacade.getIssueStats(id);
    }

    @GetMapping("/repos/{id}/stats/commits")
    public CommitStats getCommitStats(@PathVariable String id) {
        return this.statsFacade.getCommitStats(id);
    }

}