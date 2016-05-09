package ru.atott.combiq.service.question;

import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.site.UserContext;

import java.util.List;
import java.util.Set;

public interface QuestionService {

    void deleteQuestion(UserContext uc, String questionId);

    void restoreQuestion(UserContext uc, String questionId);

    List<Question> getQuestions(Set<String> questionIds);

    Question getQuestion(String id);

    List<String> refreshMentionedClassNames(Question question);

    List<String> getMentionedClassNames(String title);

    QuestionBuilder createQuestionBuilder(UserContext userContext, String questionId);

    Question saveQuestion(UserContext userContext, QuestionBuilder questionBuilder);
}
