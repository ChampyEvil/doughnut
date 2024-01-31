package com.odde.doughnut.factoryServices.quizFacotries.factories;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.Thing;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionFactory;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionServant;
import java.util.List;

public class LinkTargetQuizFactory implements QuizQuestionFactory, QuestionOptionsFactory {
  protected final Thing link;
  private QuizQuestionServant servant;
  protected final Note answerNote;
  private List<Note> cachedFillingOptions = null;

  public LinkTargetQuizFactory(Thing thing, QuizQuestionServant servant) {
    this.link = thing;
    this.servant = servant;
    this.answerNote = link.getTargetNote();
  }

  @Override
  public List<Note> generateFillingOptions() {
    if (cachedFillingOptions == null) {
      cachedFillingOptions = servant.chooseFromCohortAvoidUncles(link, answerNote);
    }
    return cachedFillingOptions;
  }

  @Override
  public Note generateAnswer() {
    return answerNote;
  }
}
