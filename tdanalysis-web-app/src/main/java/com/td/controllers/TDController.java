package com.td.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.td.facades.TDFacade;
import com.td.models.ChangeTD;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TDController extends BaseController {

    @Autowired
    private TDFacade tdFacade;

    @GetMapping("/td/items")
    public List<TechnicalDebtItem> getTDItems() {
        return tdFacade.getAllTDItems();
    }

    @GetMapping("/td/items/item")
    public TechnicalDebtItem getTDItem(@RequestParam("category") String category, @RequestParam("code") String code) {
        return tdFacade.getTDItem(category, code);
    }

    @GetMapping("/repos/{id}/commits/{sha}/td")
    public TechnicalDebt getTechnicalDebtOfCommit(@PathVariable String id, @PathVariable String sha) {
        return tdFacade.getTechnicalDebtOfCommit(id, sha);
    }

    @GetMapping("repos/{id}/td/timeline")
    public List<TechnicalDebt> getTechnicalDebtTimeline(@PathVariable String id) {
        return tdFacade.getTechnicalDebtTimeline(id).collect(Collectors.toList());
    }

    @GetMapping("repos/{id}/td/changeset")
    public List<ChangeTD> getChangeSetTechnicalDebt(@PathVariable String id) {
        return tdFacade.getChangeSetTechnicalDebt(id);
    }
}