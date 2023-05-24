import { CenteredLayout } from "../../ui/layouts";
import { PopupMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
  title: "KOOP-React/Molecules",
  component: PopupMolecule,
  args: {
    title: "title",
    text: "popup text",
    cancelText: "Cancel",
    okeText: "Oke",
    cancelButton: () => {},
    okeButton: () => {},
    extraInfo: "extra info",
  }
} as ComponentMeta<typeof PopupMolecule>

export const Popup: ComponentStory<typeof PopupMolecule> = (args: any) => <CenteredLayout>
  <PopupMolecule {...args} />
</CenteredLayout> 