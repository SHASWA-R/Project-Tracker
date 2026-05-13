package com.example.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kanban")
public class KanbanController {

    @GetMapping("/week/{weekId}")
    public String showKanbanBoard(@PathVariable Long weekId, Model model) {
        model.addAttribute("weekId", weekId);
        return "kanban";
    }
}