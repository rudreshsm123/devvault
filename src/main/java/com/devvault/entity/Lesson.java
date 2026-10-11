package com.devvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One lesson inside a chapter of the learning path. */
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int chapterNo;

    @Column(nullable = false)
    private String chapterTitle;

    /** Only filled on the first lesson of a chapter: what the chapter covers and why it matters. */
    @Column(columnDefinition = "TEXT")
    private String chapterIntro;

    private int lessonNo;

    @Column(nullable = false)
    private String title;

    /** Why a beginner needs to learn this at all. */
    @Column(columnDefinition = "TEXT")
    private String whyLearn;

    /** The concept in plain English. */
    @Column(columnDefinition = "TEXT")
    private String concept;

    private String codeLanguage;

    @Column(columnDefinition = "TEXT")
    private String code;

    /** One "code fragment => why it is there" pair per line. */
    @Column(columnDefinition = "TEXT")
    private String lineByLine;

    /** What interviewers and recruiters expect you to know about this topic. */
    @Column(columnDefinition = "TEXT")
    private String interviewView;

    @Column(columnDefinition = "TEXT")
    private String commonMistakes;

    public Lesson() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getChapterNo() { return chapterNo; }
    public void setChapterNo(int chapterNo) { this.chapterNo = chapterNo; }
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    public String getChapterIntro() { return chapterIntro; }
    public void setChapterIntro(String chapterIntro) { this.chapterIntro = chapterIntro; }
    public int getLessonNo() { return lessonNo; }
    public void setLessonNo(int lessonNo) { this.lessonNo = lessonNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getWhyLearn() { return whyLearn; }
    public void setWhyLearn(String whyLearn) { this.whyLearn = whyLearn; }
    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
    public String getCodeLanguage() { return codeLanguage; }
    public void setCodeLanguage(String codeLanguage) { this.codeLanguage = codeLanguage; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLineByLine() { return lineByLine; }
    public void setLineByLine(String lineByLine) { this.lineByLine = lineByLine; }
    public String getInterviewView() { return interviewView; }
    public void setInterviewView(String interviewView) { this.interviewView = interviewView; }
    public String getCommonMistakes() { return commonMistakes; }
    public void setCommonMistakes(String commonMistakes) { this.commonMistakes = commonMistakes; }
}
