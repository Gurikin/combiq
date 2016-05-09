package ru.atott.combiq.service.question;

import ru.atott.combiq.dao.entity.QuestionComment;

import java.util.Collection;

public interface QuestionBuilder {

    QuestionBuilder setTitle(String title);

    QuestionBuilder setTags(Collection<String> tags);

    QuestionBuilder setLevel(int level);

    QuestionBuilder setLevel(String level);

    QuestionBuilder setBody(String content);

    QuestionBuilder setLinkedQuestions(Collection<String> linkedQuestions);

    QuestionComment addComment(String commentContent);

    QuestionComment setCommentContent(String commentId, String commentContent);

    void removeComment(String commentId);
}
