package com.xperia.xpense_tracker.models.response;


import java.util.Objects;

public abstract class AbstractResponse {

    protected Object body;

    protected int status;


    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "body=" + body +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResponse that = (AbstractResponse) o;
        return status == that.status && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, status);
    }
}
