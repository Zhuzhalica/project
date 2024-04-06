package org.example.project.exceptions.custom;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

  public EntityNotFoundException(String message) {
    super(
        null,
        "Entity not found",
        Status.NOT_FOUND,
        message);
  }
}