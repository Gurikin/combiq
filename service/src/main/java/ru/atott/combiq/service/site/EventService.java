package ru.atott.combiq.service.site;

import org.springframework.data.domain.Page;
import ru.atott.combiq.dao.entity.EventType;
import ru.atott.combiq.dao.entity.Link;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.service.bean.Event;
import ru.atott.combiq.service.bean.UserType;

public interface EventService {

    void createEvent(UserContext uc, EventType type, String text, Link... relevantLinks);

    Page<Event> getEvents(long page, long size);

    default void createPostQuestionCommentEvent(UserContext uc, QuestionEntity questionEntity, String commentId) {
        createEvent(uc, EventType.POST_QUESTION_COMMENT,
                "Добавлен новый комментарий к вопросу <b>" + questionEntity.getTitle() + "</b>.",
                new Link("Вопрос", "/questions/" + questionEntity.getId()),
                new Link("Комментарий", "/questions/" + questionEntity.getId() + "#comment-" + commentId));
    }

    default void createEditQuestionCommentEvent(UserContext uc, QuestionEntity questionEntity, String commentId) {
        createEvent(uc, EventType.EDIT_QUESTION_COMMENT,
                "Изменён комментарий к вопросу <b>" + questionEntity.getTitle() + "</b>.",
                new Link("Вопрос", "/questions/" + questionEntity.getId()),
                new Link("Комментарий", "/questions/" + questionEntity.getId() + "#comment-" + commentId));
    }

    default void createRegisterUserEvent(UserType userType, String login) {
        createEvent(null, EventType.REGISTER_USER,
                "Зарегистрировался новый пользователь type: " + userType + ", login: " + login);
    }

    default void createQuestion(UserContext uc, QuestionEntity questionEntity){
        String username = uc.getUserName() == null ? "" : " пользователем " + uc.getUserName();
        createEvent(uc, EventType.CREATE_QUESTION,
                "Создан вопрос"+username+" <b>" + questionEntity.getTitle() + "</b>.",
                new Link("Вопрос", "/questions/" + questionEntity.getId()));
    }

    default void editQuestion(UserContext uc, QuestionEntity questionEntity){
        String username = " пользователем " + uc.getUserName();
        createEvent(uc, EventType.EDIT_QUESTION,
                "Вопрос <b>" + questionEntity.getTitle() + "</b>. Изменен " + username + ".",
                new Link("Вопрос", "/questions/" + questionEntity.getId()));
    }

    default void deleteQuestion(UserContext uc, QuestionEntity questionEntity){
        createEvent(uc, EventType.DELETE_QUESTION,
                "Вопрос <b>" + questionEntity.getTitle()+ "</b>. Удален пользователем " +
                        uc.getUserName() + ".",
                new Link("Вопрос", "/questions/" + questionEntity.getId()));
    }

    default void restoreQuestion(UserContext uc, QuestionEntity questionEntity){
        createEvent(uc, EventType.RESTORE_QUESTION,
                "Вопрос <b>" + questionEntity.getTitle() + "</b>. Востанновлен пользователем " +
                        uc.getUserName() + ".",
                new Link("Вопрос", "/questions/" + questionEntity.getId()));
    }

    default void recountQuestions() {
        createEvent(null, EventType.RESTORE_QUESTION, "ВОпросы обновленны");

    }
}
