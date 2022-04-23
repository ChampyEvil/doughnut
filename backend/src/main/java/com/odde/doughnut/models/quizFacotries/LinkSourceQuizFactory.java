package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.User;
import java.util.List;

public class LinkSourceQuizFactory implements QuizQuestionFactory, QuestionOptionsFactory {
  protected final Link link;
  private QuizQuestionServant servant;
  protected final Note answerNote;
  private final User user;
  private List<Note> cachedFillingOptions = null;

  public LinkSourceQuizFactory(ReviewPoint reviewPoint, QuizQuestionServant servant) {
    this.link = reviewPoint.getLink();
    this.servant = servant;
    this.answerNote = link.getSourceNote();
    this.user = reviewPoint.getUser();
  }

  @Override
  public List<Note> generateFillingOptions() {
    if (cachedFillingOptions == null) {
      List<Note> cousinOfSameLinkType = link.getCousinsOfSameLinkType(user);
      cachedFillingOptions =
          servant.chooseFromCohort(
              answerNote,
              n ->
                  !n.equals(answerNote)
                      && !n.equals(link.getTargetNote())
                      && !cousinOfSameLinkType.contains(n));
    }
    return cachedFillingOptions;
  }

  @Override
  public Note generateAnswer() {
    return answerNote;
  }

  @Override
  public List<Note> knownRightAnswers() {
    return List.of(answerNote);
  }
}
