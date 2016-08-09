package ru.atott.combiq.rest.request;

import java.util.List;

/**
 * Created by Леонид on 08.08.2016.
 */
public class QuestionRequest {

    public String id;

    public List requestedFields;

    public List getRequestedFields() {
        return requestedFields;
    }

    public void setRequestedFields(List requestedFields) {
        this.requestedFields = requestedFields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
