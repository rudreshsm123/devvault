package com.devvault.config.seed;

import com.devvault.entity.Lesson;

import java.util.List;

/** Builds {@link Lesson} objects. The line-by-line text uses one "fragment ==> reason" pair per line. */
final class LessonFactory {

    private LessonFactory() {
    }

    /**
     * @param chapterIntro only for the first lesson of a chapter (otherwise null)
     */
    static void add(List<Lesson> out, int chapterNo, String chapterTitle, String chapterIntro, int lessonNo,
                    String title, String whyLearn, String concept, String codeLanguage, String code,
                    String lineByLine, String interviewView, String commonMistakes) {
        Lesson l = new Lesson();
        l.setChapterNo(chapterNo);
        l.setChapterTitle(chapterTitle);
        l.setChapterIntro(chapterIntro);
        l.setLessonNo(lessonNo);
        l.setTitle(title);
        l.setWhyLearn(whyLearn.strip());
        l.setConcept(concept.strip());
        l.setCodeLanguage(codeLanguage);
        l.setCode(code == null ? null : code.strip());
        l.setLineByLine(lineByLine == null ? null : lineByLine.strip());
        l.setInterviewView(interviewView.strip());
        l.setCommonMistakes(commonMistakes == null ? null : commonMistakes.strip());
        out.add(l);
    }
}
