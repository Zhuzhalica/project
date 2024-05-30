package org.example.project.exceptions.custom;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * Exceptions when entity not exist.
 */
public class EntityNotFoundException extends AbstractThrowableProblem {

  /**
   * Exception constructor.
   *
   * @param message exception message
   */
  public EntityNotFoundException(String message) {
    super(
        null,
        "Entity not found",
        Status.NOT_FOUND,
        message);
  }
}