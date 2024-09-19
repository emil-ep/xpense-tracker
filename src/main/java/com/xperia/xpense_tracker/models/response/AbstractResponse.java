package com.xperia.xpense_tracker.models.response;


import java.util.Objects;

public abstract class AbstractResponse {

    protected Object data;

    protected int status;


    public Object getData() {
        return data;
    }

    public void setData(Object body) {
        this.data = body;
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
                "body=" + data +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResponse that = (AbstractResponse) o;
        return status == that.status && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, status);
    }
}
