import {  ToastOrganism } from "../../ui/organisms";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { ContainerAtom } from "../../ui/atoms";

export default {
  title: "KOOP-React/Organisms",
  component: ToastOrganism
} as ComponentMeta<typeof ToastOrganism>;

export const Toast: ComponentStory<typeof ToastOrganism> = (
  args: any
) => (
  <Provider store={setupStore()}>
    <CenteredLayout>
      <ContainerAtom>
        <ToastOrganism {...args} />
      </ContainerAtom>
    </CenteredLayout>
  </Provider>
);
