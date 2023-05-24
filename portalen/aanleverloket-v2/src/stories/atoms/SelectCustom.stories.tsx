import { SelectCustomAtom } from "../../ui/atoms";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";

export default {
  title: "KOOP-React/Atoms",
  component: SelectCustomAtom,
  args: {
    id:"select",
    ariaLabel:"select",
    options: ["Option 1", "Option 2", "Option 123456789"],
    disabled: false,
  },
} as ComponentMeta<typeof SelectCustomAtom>;

export const SelectCustom: ComponentStory<typeof SelectCustomAtom> = (
  args: any
) => (
  <CenteredLayout>
    <SelectCustomAtom {...args} />
  </CenteredLayout>
);
