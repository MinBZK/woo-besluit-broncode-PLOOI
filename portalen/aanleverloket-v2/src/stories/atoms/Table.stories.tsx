import { CenteredLayout } from "../../ui/layouts";
import { ContainerAtom, TableAtom } from "../../ui/atoms";
import { ComponentStory, ComponentMeta } from "@storybook/react";

export interface Args {
  type?: "primary";
}

export default {
  title: "KOOP-React/Atoms",
  component: TableAtom,
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
} as ComponentMeta<typeof TableAtom>;

export const Table: ComponentStory<typeof TableAtom> = (args: any) => (
  <CenteredLayout>
    <ContainerAtom centered type="flex">
      <TableAtom {...args} />
    </ContainerAtom>
  </CenteredLayout>
);
