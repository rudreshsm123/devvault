package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A runnable coding exercise attached to a problem: starter code plus the test cases that judge an answer. */
@Entity
@Table(name = "code_challenges")
public class CodeChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long problemId;

    /** The function (JavaScript) or method of class Solution (Java) the learner must implement. */
    @Column(nullable = false, length = 80)
    private String functionName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String jsStarter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String javaStarter;

    /** JSON array of {"args": [...], "expected": ...}. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String testsJson;

    public CodeChallenge() {
    }

    public CodeChallenge(Long problemId, String functionName, String jsStarter, String javaStarter, String testsJson) {
        this.problemId = problemId;
        this.functionName = functionName;
        this.jsStarter = jsStarter;
        this.javaStarter = javaStarter;
        this.testsJson = testsJson;
    }

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    public String getJsStarter() { return jsStarter; }
    public void setJsStarter(String jsStarter) { this.jsStarter = jsStarter; }
    public String getJavaStarter() { return javaStarter; }
    public void setJavaStarter(String javaStarter) { this.javaStarter = javaStarter; }
    public String getTestsJson() { return testsJson; }
    public void setTestsJson(String testsJson) { this.testsJson = testsJson; }
}
