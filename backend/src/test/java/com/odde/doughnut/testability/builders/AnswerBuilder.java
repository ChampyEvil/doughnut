package com.odde.doughnut.testability.builders;

import com.odde.doughnut.entities.Answer;
import com.odde.doughnut.entities.AnswerViewedByUser;
import com.odde.doughnut.entities.QuizQuestion;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.testability.EntityBuilder;
import com.odde.doughnut.testability.MakeMe;

public class AnswerBuilder extends EntityBuilder<Answer> {
  private QuizQuestion.QuestionType questionType = QuizQuestion.QuestionType.SPELLING;
  private ReviewPoint reviewPoint;

  public AnswerBuilder(MakeMe makeMe) {
    super(makeMe, new Answer());
  }

  @Override
  protected void beforeCreate(boolean needPersist) {
    if (entity.getQuestion() == null) {
      entity.setQuestion(makeMe.aQuestion().of(questionType, reviewPoint).inMemoryPlease());
    }
  }

  public AnswerBuilder withValidQuestion() {
    entity.setQuestion(makeMe.aQuestion().buildValid(questionType, reviewPoint).inMemoryPlease());
    if (entity.getQuestion() == null)
      throw new RuntimeException(
        "Failed to generate a question of type "
          + questionType.name()
          + ", perhaps no enough data.");
    return this;
  }

  public AnswerBuilder forReviewPoint(ReviewPoint reviewPoint) {
    this.reviewPoint = reviewPoint;
    return this;
  }

  public AnswerBuilder type(QuizQuestion.QuestionType questionType) {
    this.questionType = questionType;
    return this;
  }

  public AnswerBuilder answerWithSpelling(String answer) {
    this.entity.setAnswerNoteId(null);
    this.entity.setSpellingAnswer(answer);
    return this;
  }

  public AnswerBuilder answerWithId(Integer id) {
    this.entity.setSpellingAnswer(null);
    this.entity.setAnswerNoteId(id);
    return this;
  }
}
