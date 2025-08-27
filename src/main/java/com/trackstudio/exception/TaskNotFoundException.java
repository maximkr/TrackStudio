package com.trackstudio.exception;

public class TaskNotFoundException extends UserException {
    public TaskNotFoundException(String task) {
        super("ERROR_SPECIFIED_TASK_NOT_FOUND", new String[]{task});
        printStackTrace();
    }
}
