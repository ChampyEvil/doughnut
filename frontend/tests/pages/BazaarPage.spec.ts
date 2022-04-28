/**
 * @jest-environment jsdom
 */
import BazaarPage from "@/pages/BazaarPage.vue";
import helper from "../helpers";
import makeMe from "../fixtures/makeMe";

helper.resetWithApiMock(beforeEach, afterEach);

describe("bazaar page", () => {
  it("fetch API to be called ONCE on mount", async () => {
    const notebook = makeMe.aNotebook.please();
    const bazaarNotebooks = makeMe.bazaarNotebooks.notebooks(notebook).please();
    helper.apiMock.expecting("/api/bazaar", bazaarNotebooks);
    helper.component(BazaarPage).render();
  });
});
