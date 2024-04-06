package org.example.project.exceptions.custom;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ValidateException extends AbstractThrowableProblem {

  public ValidateException(String message) {
    super(
        null,
        "Validate Error",
        Status.BAD_REQUEST,
        message);
  }
}