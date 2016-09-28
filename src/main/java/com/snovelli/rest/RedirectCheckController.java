package com.snovelli.rest;

import com.snovelli.seo.redirect.RedirectChainAnalyser;
import com.snovelli.model.RedirectChain;
import com.snovelli.model.RedirectSpecification;
import com.snovelli.model.RedirectCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Salvatore on 24/04/2016.
 */

@RestController
public class RedirectCheckController {

    public static final String REDIRECT_CHECK_URI = "/redirect-check";


    @Autowired
    private RedirectChainAnalyser analyser;


    @RequestMapping(value = REDIRECT_CHECK_URI, method = RequestMethod.POST)
    public RedirectCheckResponse checkRedirect(@RequestBody RedirectSpecification request) {

        RedirectChain redirectChain = analyser.analyseRedirectChain(request.getSourceURI());
        return new RedirectCheckResponse(request, redirectChain);

    }


}
