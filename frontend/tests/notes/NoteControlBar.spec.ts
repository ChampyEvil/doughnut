/**
 * @jest-environment jsdom
 */
import { screen } from "@testing-library/vue";
import Breadcrumb from "@/components/toolbars/Breadcrumb.vue";
import helper from "../helpers";
import makeMe from "../fixtures/makeMe";

describe("note wth child cards", () => {
  beforeEach(() => {
    helper.reset();
  });

  it("view note belongs to other people in bazaar", async () => {
    const note = makeMe.aNoteRealm.please();
    const notePosition = makeMe.aNotePosition.inBazaar().please();
    helper.store.loadNoteRealms([note]);
    helper.component(Breadcrumb).withProps(notePosition).render();
    await screen.findByText("Bazaar");
  });
});
