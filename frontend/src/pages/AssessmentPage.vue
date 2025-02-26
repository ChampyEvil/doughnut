<template>
  <h3>Assessment For {{ topicConstructor }}</h3>
  <div>
    <div v-if="errors != ''">
      {{ errors }}
    </div>
    <QuizQuestionComp
      v-else-if="currentQuestion < quizQuestions.length"
      :quiz-question="quizQuestions[currentQuestion]!"
      @answered="questionAnswered"
    />
    <div v-else-if="assessmentCompleted">
      <p>Your score: {{ correctAnswers }} / {{ quizQuestions.length }}</p>
    </div>
  </div>

  <div v-if="notesOfWrongAnswers.length > 0 && assessmentCompleted">
    <h1>CONTENT BELOW IS IN PROGRESS</h1>
    <h5>Improve your knowledge by studying these notes</h5>
    <Cards :note-topics="notesOfWrongAnswers"> </Cards>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import useLoadingApi from "@/managedApi/useLoadingApi"
import {
  NoteTopic,
  QuestionAnswerPair,
  QuizQuestion,
} from "@/generated/backend"
import { useRouter } from "vue-router"
import QuizQuestionComp from "@/components/review/QuizQuestion.vue"
import Cards from "@/components/notes/Cards.vue"

const { managedApi } = useLoadingApi()
const router = useRouter()
const props = defineProps({
  notebookId: { type: Number, required: true },
})
const topicConstructor = computed(() => {
  return router.currentRoute.value.query?.topic
})
const quizQuestions = ref<QuizQuestion[]>([])
const notesOfWrongAnswers = ref<NoteTopic[]>([])
const currentQuestion = ref(0)
const questionsAnswerCollection = ref<QuestionAnswerPair[]>([])
const errors = ref("")
const correctAnswers = ref(0)
const assessmentCompleted = computed(
  () =>
    currentQuestion.value >= quizQuestions.value.length &&
    quizQuestions.value.length > 0
)

const questionAnswered = async (answerResult) => {
  questionsAnswerCollection.value.push({
    questionId: quizQuestions.value[currentQuestion.value]!.id,
    answerId: answerResult.answerId,
  })

  if (answerResult.correct) {
    correctAnswers.value += 1
  }
  currentQuestion.value += 1
  if (assessmentCompleted.value) {
    const assessmentResult =
      await managedApi.restAssessmentController.submitAssessmentResult(
        <number>props.notebookId,
        questionsAnswerCollection.value
      )
    for (const noteIdAndTitle of assessmentResult.noteIdAndTitles!) {
      notesOfWrongAnswers.value.push({
        id: noteIdAndTitle.id ?? 0,
        topicConstructor: noteIdAndTitle.title ?? "",
      })
    }
  }
}

const generateAssessmentQuestions = () => {
  managedApi.restAssessmentController
    .generateAssessmentQuestions(<number>props.notebookId)
    .then((response) => {
      quizQuestions.value = response
    })
    .catch((res) => {
      errors.value = res.body.message
    })
}

onMounted(() => {
  generateAssessmentQuestions()
})
</script>
