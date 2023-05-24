import { CheckboxListMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: CheckboxListMolecule,
    args: {
        title: "Select options",
        options: [
            {
                id: "Option 1",
                label: "Option 1",
                checked: false,
                disabled: true,
            },
            {
                id: "Option 2",
                label: "Option 2",
                checked: false,

            },
            {
                id: "Option 3",
                label: "Option 3",
                checked: true,
            }
        ]
    },
} as ComponentMeta<typeof CheckboxListMolecule>

export const CheckboxList: ComponentStory<typeof CheckboxListMolecule> = (args: any) => <CenteredLayout>
    <CheckboxListMolecule {...args} />
</CenteredLayout>
