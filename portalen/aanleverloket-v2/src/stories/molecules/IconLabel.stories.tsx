import { IconLabelMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: IconLabelMolecule,
    args: { 
        icon: "icon-alert-red",
        label: "Ongeldig e-mailadres",
        type: 'alert',
    },
} as ComponentMeta<typeof IconLabelMolecule>

export const IconLabel: ComponentStory<typeof IconLabelMolecule> = (args: any) => <CenteredLayout>
    <IconLabelMolecule {...args} />
</CenteredLayout>