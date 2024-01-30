package com.odde.doughnut.models.quizFacotries;

import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.DESCRIPTION_LINK_TARGET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.odde.doughnut.controllers.json.QuizQuestion;
import com.odde.doughnut.entities.AnsweredQuestion;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DescriptionLinkTargetQuizFactoryTest {

  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note target;
  Note source;
  Note anotherSource;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote().creatorAndOwner(userModel).please();
    target = makeMe.aNote("rome").under(top).please();
    source =
        makeMe
            .aNote("saying")
            .details("Rome is not built in a day")
            .under(top)
            .linkTo(target)
            .please();
    reviewPoint =
        makeMe.aReviewPointFor(source.getLinks().get(0).getThing()).by(userModel).inMemoryPlease();
    anotherSource = makeMe.aNote("pompeii").under(top).please();
    makeMe.refresh(top);
  }

  @Nested
  class WhenTheNoteDoesnotHaveDescription {
    @BeforeEach
    void setup() {
      makeMe.theNote(source).details("").please();
    }

    @Test
    void shouldBeInvalid() {
      assertThat(buildQuestion(), nullValue());
    }
  }

  @Test
  void shouldIncludeRightAnswers() {
    assertThat(
        buildQuestion().getStem(),
        containsString(
            "<p>The following descriptions is a specialization of:</p><p><mark title='Hidden text that is matching the answer'>[...]</mark> is not built in a day</p>\n"));
  }

  @Test
  void shouldIncludeMasks() {
    makeMe.theNote(source).titleConstructor("token").details("token /.").please();
    assertThat(
        buildQuestion().getStem(),
        containsString("<mark title='Hidden text that is matching the answer'>[...]</mark> /."));
  }

  @Nested
  class Answer {
    @Test
    void correct() {
      AnsweredQuestion answerResult =
          makeMe
              .anAnswerViewedByUser()
              .validQuestionOfType(DESCRIPTION_LINK_TARGET, reviewPoint)
              .choiceIndex(1)
              .inMemoryPlease();
      assertTrue(answerResult.correct);
    }
  }

  private QuizQuestion buildQuestion() {
    return makeMe.buildAQuestion(DESCRIPTION_LINK_TARGET, reviewPoint);
  }
}
