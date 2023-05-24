import { DocumentenLijstOrganism } from "../../ui/organisms";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { ContainerAtom } from "../../ui/atoms";
import { BrowserRouter as Router } from "react-router-dom";
import { ApiFactory } from "../../api";
import { searchDocuments } from "../../store/reducers/search";

ApiFactory.isTestSuite = true;
const store = setupStore();

export default {
  title: "KOOP-React/Organisms",
  component: DocumentenLijstOrganism
} as ComponentMeta<typeof DocumentenLijstOrganism>;

export const Documenten: ComponentStory<typeof DocumentenLijstOrganism> = (
  args: any
) => (
  <Router>
    <Provider store={setupStore()}>
      <CenteredLayout>
        <ContainerAtom>
          <DocumentenLijstOrganism {...args} />
        </ContainerAtom>
      </CenteredLayout>
    </Provider>
  </Router>
);
