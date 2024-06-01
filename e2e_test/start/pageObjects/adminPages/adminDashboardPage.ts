import { adminFineTuningPage } from "./adminFineTuningPage"

export function assumeAdminDashboardPage() {
  return {
    goToFailureReportList() {
      this.goToTabInAdminDashboard("Failure Reports")
      cy.findByText("Failure report list")
      return {
        shouldContain(content: string) {
          cy.get("body").should("contain", content)
        },
      }
    },

    goToTabInAdminDashboard(tabName: string) {
      cy.findByRole("button", { name: tabName }).click()
    },

    goToFineTuningData() {
      this.goToTabInAdminDashboard("Fine Tuning Data")
      return adminFineTuningPage()
    },

    goToModelManagement() {
      this.goToTabInAdminDashboard("Manage Models")
      return {
        chooseModel(model: string, task: string) {
          cy.findByLabelText(task).select(model)
          cy.findByRole("button", { name: "Save" }).click()
        },
      }
    },

    goToBazaarManagement() {
      this.goToTabInAdminDashboard("Manage Bazaar")
      return {
        delete(_notebook: string) {},
      }
    },

    goToAssistantManagement() {
      this.goToTabInAdminDashboard("Manage Assistant")
      return {
        recreate() {
          cy.findByRole("button", { name: "Recreate All Assistants" }).click()
          return {
            expectNewAssistant(newId: string, nameOfAssistant: string) {
              cy.findByLabelText(nameOfAssistant).should("have.value", newId)
            },
          }
        },
      }
    },
  }
}
