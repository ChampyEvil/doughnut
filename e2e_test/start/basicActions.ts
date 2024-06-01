import { assumeAdminDashboardPage } from "./pageObjects/adminPages/adminDashboardPage"
import { assumeChatAboutNotePage } from "./pageObjects/chatAboutNotePage"
import { assumeNotePage } from "./pageObjects/notePage"
import { bazaar } from "./pageObjects/bazaar"
import { sidebar } from "./pageObjects/sidebar"
import { routerToNotebooksPage } from "./pageObjects/notebooksPage"
import { navigateToCircle } from "./pageObjects/circlePage"
import { assumeAnsweredQuestionPage } from "./pageObjects/AnsweredQuestionPage"
import { assumeQuestionPage } from "./pageObjects/QuizQuestionPage"
import { assumeClarifyingQuestionDialog } from "./pageObjects/clarifyingQuestionDialog"
import testability from "./testability"

export default {
  bazaar,
  sidebar,
  assumeNotePage,
  assumeAnsweredQuestionPage,
  assumeChatAboutNotePage,
  assumeQuestionPage,
  assumeAdminDashboardPage,
  assumeClarifyingQuestionDialog,
  routerToNotebooksPage,
  navigateToCircle,
  // jumptoNotePage is faster than navigateToPage
  //    it uses the note id memorized when creating them with testability api
  jumpToNotePage: (noteTopic: string, forceLoadPage = false) => {
    testability()
      .getSeededNoteIdByTitle(noteTopic)
      .then((noteId) => {
        const url = `/notes/${noteId}`
        if (forceLoadPage) cy.visit(url)
        else cy.routerPush(url, "noteShow", { noteId: noteId })
      })

    return assumeNotePage(noteTopic)
  },

  loginAsAdmin: () => {
    cy.logout()
    cy.loginAs("admin")
  },

  goToAdminDashboard: () => {
    cy.reload()
    cy.openSidebar()
    cy.findByText("Admin Dashboard").click()
    return assumeAdminDashboardPage()
  },

  loginAsAdminAndGoToAdminDashboard() {
    this.loginAsAdmin()
    return this.goToAdminDashboard()
  },
}
