import { CenteredLayout } from "../../ui/layouts";
import { ContainerAtom, VirtualTableAtom } from "../../ui/atoms";
import { ComponentStory, ComponentMeta } from "@storybook/react";

export interface Args {
  type?: "primary";
}

export default {
  title: "KOOP-React/Atoms",
  component: VirtualTableAtom,
  args: {
    theads: ["nr", "naam"],
    rows: [
      ["1.", "test1"],
      ["2.", "test2"],
      ["3.", "test3"],
    ],
    columnWidth: ["45%", "50%"],
    OnEndOfScroll: () => {},
  },
} as ComponentMeta<typeof VirtualTableAtom>;

export const VirtualTable: ComponentStory<typeof VirtualTableAtom> = (args: any) => (
  <CenteredLayout>
    <ContainerAtom centered type="flex">
      <VirtualTableAtom {...args} />
    </ContainerAtom>
  </CenteredLayout>
);
