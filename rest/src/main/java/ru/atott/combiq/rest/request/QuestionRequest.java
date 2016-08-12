package ru.atott.combiq.rest.request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuestionRequest {

    public static QuestionRequest EXAMPLE;
    static {
        EXAMPLE = new QuestionRequest();
        String[] requestedFields = {"title", "level", "body", "tags", "linkedQuestions"};
        EXAMPLE.setRequestedFields(new LinkedList<String>());
        for(String s:requestedFields) {
            EXAMPLE.getRequestedFields().add(s);
        }
    }
    private List<String> requestedFields;

    public List<String> getRequestedFields() {
        return requestedFields;
    }

    public void setRequestedFields(List<String> requestedFields) {
        this.requestedFields = requestedFields;
    }
}
