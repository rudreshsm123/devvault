package com.devvault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Exposes the read-only flag to every Thymeleaf page so the layout can show a banner. */
@ControllerAdvice
public class ReadOnlyModelAdvice {

    @Value("${devvault.readonly:false}")
    private boolean readOnly;

    @ModelAttribute("readOnly")
    public boolean readOnly() {
        return readOnly;
    }
}
