package com.odde.doughnut.factoryServices.quizFacotries.presenters;

import com.odde.doughnut.controllers.json.QuizQuestion;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionPresenter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class QuizQuestionWithOptionsPresenter implements QuizQuestionPresenter {

  protected final QuizQuestionEntity quizQuestion;

  public QuizQuestionWithOptionsPresenter(QuizQuestionEntity quizQuestion) {
    this.quizQuestion = quizQuestion;
  }

  @Override
  public List<QuizQuestion.Choice> getOptions(ModelFactoryService modelFactoryService) {
    List<Integer> idList = quizQuestion.getChoiceNoteIds();
    Stream<Note> noteStream =
        modelFactoryService
            .noteRepository
            .findAllByIds(idList)
            .sorted(Comparator.comparing(v -> idList.indexOf(v.getId())));
    return getOptionsFromNote(noteStream);
  }

  protected List<QuizQuestion.Choice> getOptionsFromNote(Stream<Note> noteStream) {
    return noteStream
        .map(
            note -> {
              QuizQuestion.Choice choice = new QuizQuestion.Choice();
              choice.setDisplay(note.getTopicConstructor());
              return choice;
            })
        .toList();
  }
}
