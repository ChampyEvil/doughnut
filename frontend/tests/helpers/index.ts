import { defineComponent } from "vue";
import RenderingHelper from "./RenderingHelper";
import setupApiMock from "./apiMockImpl/setupApiMock";
import { ApiMock } from "./ApiMock";

class StoredComponentTestHelper {
  private mockedApi?: ApiMock;

  get apiMock(): ApiMock {
    if (!this.mockedApi) throw new Error("please call resetWithApiMock first.");
    return this.mockedApi;
  }

  reset() {
    this.mockedApi = undefined;
    return this;
  }

  resetWithApiMock(beforeEach: jest.Lifecycle, afterEach: jest.Lifecycle) {
    beforeEach(() => {
      this.reset();
      this.mockedApi = setupApiMock();
    });
    afterEach(() => {
      this.mockedApi?.assertNoUnexpectedOrMissedCalls();
    });
    return this;
  }

  // eslint-disable-next-line class-methods-use-this
  component(comp: ReturnType<typeof defineComponent>) {
    return new RenderingHelper(comp);
  }
}

export default new StoredComponentTestHelper();
export { setupApiMock };
