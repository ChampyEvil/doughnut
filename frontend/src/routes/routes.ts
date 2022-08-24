import { RouteLocation, RouteRecordRaw } from "vue-router";
import HomePage from "@/pages/HomePage.vue";
import BazaarPage from "@/pages/BazaarPage.vue";
import NotebooksPage from "@/pages/NotebooksPage.vue";
import NoteShowPage from "@/pages/NoteShowPage.vue";
import NoteShowMindmapPage from "@/pages/NoteShowMindmapPage.vue";
import NoteShowArticlePage from "@/pages/NoteShowArticlePage.vue";
import ReviewHome from "@/pages/ReviewHome.vue";
import RepeatPage from "@/pages/RepeatPage.vue";
import InitialReviewPage from "@/pages/InitialReviewPage.vue";
import CircleShowPage from "@/pages/CircleShowPage.vue";
import CircleJoinPage from "@/pages/CircleJoinPage.vue";
import FailureReportListPage from "@/pages/FailureReportListPage.vue";
import FailureReportPage from "@/pages/FailureReportPage.vue";
import AnswerShowPage from "@/pages/AnswerShowPage.vue";
import NestedPage from "../pages/commons/NestedPage";

const NestedInitialReviewPage = NestedPage(InitialReviewPage, "initial");

const NestedRepeatPage = NestedPage(RepeatPage, "repeat");

const noteAndLinkRoutes = [
  { path: "notebooks", name: "notebooks", component: NotebooksPage },

  {
    path: `notes/:noteId`,
    name: "noteShow",
    component: NoteShowPage,
    props: (route: RouteLocation) => ({ noteId: Number(route.params.noteId) }),
  },

  {
    path: `notes/mindmap/:noteId`,
    name: "noteShowMindmap",
    component: NoteShowMindmapPage,
    props: (route: RouteLocation) => ({ noteId: Number(route.params.noteId) }),
  },

  {
    path: `notes/article/:noteId`,
    name: "noteShowArticle",
    component: NoteShowArticlePage,
    props: (route: RouteLocation) => ({ noteId: Number(route.params.noteId) }),
  },

  {
    path: `answers/:answerId`,
    name: "answer",
    component: AnswerShowPage,
    props: true,
  },
  {
    path: "circles/:circleId",
    name: "circleShow",
    component: CircleShowPage,
    props: true,
  },
];

const nestedNoteAndLinkRoutes = (prefix: string) =>
  noteAndLinkRoutes.map((route) => ({ ...route, name: prefix + route.name }));

const routes = [
  ...noteAndLinkRoutes.map((route) => ({ ...route, path: `/${route.path}` })),
  { path: "/", name: "root", component: HomePage },
  {
    path: "/bazaar",
    name: "bazaar",
    component: BazaarPage,
    meta: { userProp: true },
  },
  {
    path: "/circles/join/:invitationCode?",
    name: "circleJoin",
    component: CircleJoinPage,
    props: true,
    meta: { userProp: true },
  },
  {
    path: "/bazaar/notes/:noteId",
    name: "bnoteShow",
    component: NoteShowPage,
    props: true,
  },
  { path: "/reviews", name: "reviews", component: ReviewHome },
  {
    path: "/reviews/initial",
    name: "initial",
    component: NestedInitialReviewPage,
    children: nestedNoteAndLinkRoutes("initial-"),
  },
  {
    path: "/reviews/repeat",
    name: "repeat",
    component: NestedRepeatPage,
    children: [...nestedNoteAndLinkRoutes("repeat-")],
  },
  {
    path: "/failure-report-list",
    name: "failureReportList",
    component: FailureReportListPage,
  },
  {
    path: "/failure-report-list/show/:failureReportId",
    name: "failureReport",
    component: FailureReportPage,
    props: true,
  },
] as RouteRecordRaw[];

export default routes;
