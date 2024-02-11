package com.odde.doughnut.testability.builders;

import com.odde.doughnut.entities.*;
import com.odde.doughnut.services.QuestionType;
import com.odde.doughnut.testability.EntityBuilder;
import com.odde.doughnut.testability.MakeMe;

public class AnswerBuilder extends EntityBuilder<Answer> {
  public AnswerBuilder(MakeMe makeMe) {
    super(makeMe, new Answer());
  }

  @Override
  protected void beforeCreate(boolean needPersist) {
    if (needPersist) {
      if (entity.getQuestion().getId() == null) {
        makeMe.modelFactoryService.save(entity.getQuestion());
      }
    }
  }

  public AnswerBuilder withValidQuestion(QuestionType questionType, ReviewPoint reviewPoint) {
    entity.setQuestion(
        makeMe
            .aQuestion()
            .buildValid(reviewPoint, questionType.getQuizQuestionFactory(reviewPoint.getNote()))
            .inMemoryPlease());
    if (entity.getQuestion() == null)
      throw new RuntimeException(
          "Failed to generate a question of type "
              + questionType.name()
              + ", perhaps no enough data.");
    return this;
  }

  public AnswerBuilder ofSpellingQuestion(Note note) {
    entity.setQuestion(makeMe.aQuestion().spellingQuestionOfReviewPoint(note).inMemoryPlease());
    return this;
  }

  public AnswerBuilder forQuestion(QuizQuestionEntity quizQuestion) {
    entity.setQuestion(quizQuestion);
    return this;
  }

  public AnswerBuilder answerWithSpelling(String answer) {
    this.entity.setChoiceIndex(null);
    this.entity.setSpellingAnswer(answer);
    return this;
  }

  public AnswerBuilder choiceIndex(int index) {
    this.entity.setSpellingAnswer(null);
    this.entity.setChoiceIndex(index);
    return this;
  }
}
