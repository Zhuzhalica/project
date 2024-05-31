package org.example.project.exceptions.custom;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * Exception when entity with wrong.
 */
public class ValidateException extends AbstractThrowableProblem {

  /**
   * Exception constructor.
   *
   * @param message exception message
   */
  public ValidateException(String message) {
    super(
        null,
        "Validate Error",
        Status.BAD_REQUEST,
        message);
  }
}