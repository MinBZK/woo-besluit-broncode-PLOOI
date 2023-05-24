import { CenteredLayout } from "../../ui/layouts";
import { TooltipMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
  title: "KOOP-React/Molecules",
  component: TooltipMolecule,
  args: {
    content: 'Hello World'
  }
} as ComponentMeta<typeof TooltipMolecule>

export const Tooltip: ComponentStory<typeof TooltipMolecule> = (args: any) => <CenteredLayout>
  <TooltipMolecule {...args} />
</CenteredLayout>