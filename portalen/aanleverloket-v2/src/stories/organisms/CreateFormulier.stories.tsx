import { CreateFormulierOrganism } from "../../ui/organisms";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { ContainerAtom } from "../../ui/atoms";

export default {
  title: "KOOP-React/Organisms",
  component: CreateFormulierOrganism
} as ComponentMeta<typeof CreateFormulierOrganism>;

export const CreateFormulier: ComponentStory<typeof CreateFormulierOrganism> = (
  args: any
) => (
  <Provider store={setupStore()}>
    <CenteredLayout>
      <ContainerAtom>
        <CreateFormulierOrganism {...args} />
      </ContainerAtom>
    </CenteredLayout>
  </Provider>
);
