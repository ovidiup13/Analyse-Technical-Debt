package com.td.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.td.facades.TDFacade;
import com.td.models.ChangeTD;
import com.td.models.TechnicalDebt;
import com.td.models.WorkTD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TDController extends BaseController {

    @Autowired
    private TDFacade tdFacade;

    @GetMapping("repos/{id}/td/timeline")
    public List<TechnicalDebt> getTechnicalDebtTimeline(@PathVariable String id) {
        return tdFacade.getTechnicalDebtTimeline(id).collect(Collectors.toList());
    }

    @GetMapping("repos/{id}/td/changeset")
    public List<ChangeTD> getChangeSetTechnicalDebt(@PathVariable String id) {
        return tdFacade.getChangeSetTechnicalDebt(id);
    }

    @GetMapping("repos/{id}/td/work/ticket")
    public List<WorkTD> getWorkTDByTicket(@PathVariable String id) {
        return tdFacade.getWorkTDByTicket(id);
    }

    @GetMapping("repos/{id}/td/work/commit")
    public List<WorkTD> getWorkTDByCommit(@PathVariable String id) {
        return tdFacade.getWorkTDByCommit(id);
    }
}