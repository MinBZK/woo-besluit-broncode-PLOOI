import { LabelMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: LabelMolecule,
    args: { 
        id:"Label",
        label:"Label",
        tooltip: "Tooltip info",
        required: true,
    },
} as ComponentMeta<typeof LabelMolecule>

export const Label: ComponentStory<typeof LabelMolecule> = (args: any) => <CenteredLayout>
    <LabelMolecule {...args} />
</CenteredLayout>