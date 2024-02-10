package com.odde.doughnut.factoryServices.quizFacotries.factories;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.quizQuestions.QuizQuestionPictureSelection;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionFactory;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionNotPossibleException;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionServant;
import java.util.List;

public class PictureSelectionQuizFactory implements QuizQuestionFactory, QuestionOptionsFactory {
  private final Note answerNote;
  private QuizQuestionServant servant;

  public PictureSelectionQuizFactory(Note note, QuizQuestionServant servant) {
    this.answerNote = note;
    this.servant = servant;
  }

  @Override
  public List<Note> generateFillingOptions() {
    return servant.chooseFromCohort(answerNote, n -> n.getPictureWithMask().isPresent());
  }

  @Override
  public Note generateAnswer() {
    return answerNote;
  }

  @Override
  public void validatePossibility() throws QuizQuestionNotPossibleException {
    if (answerNote.getPictureWithMask().isEmpty()) {
      throw new QuizQuestionNotPossibleException();
    }
  }

  @Override
  public QuizQuestionEntity buildQuizQuestion() {
    return new QuizQuestionPictureSelection();
  }
}
