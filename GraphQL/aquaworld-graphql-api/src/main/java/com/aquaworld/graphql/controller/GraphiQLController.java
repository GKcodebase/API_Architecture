package com.aquaworld.graphql.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Custom GraphiQL Controller
 * Serves a local GraphiQL interface without external CDN dependencies
 */
@Controller
public class GraphiQLController {

    @GetMapping("/graphiql")
    public String graphiql() {
        // Serve the static index.html file that contains GraphiQL
        return "forward:/index.html";
    }
}
