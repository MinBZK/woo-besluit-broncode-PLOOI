import { CheckboxListSelectAllMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: CheckboxListSelectAllMolecule,
    args: {
        selectAllLabel: "Selecteer alle 3 verdragen",
        onSelectAll: () => {},
        options: [
            {
                id: "Option 1",
                label: "Haarlem",
                checked: false,
                disabled: true,
            },
            {
                id: "Option 2",
                label: "Haarlemmermeerdam",
                checked: false,

            },
            {
                id: "Option 3",
                label: "Utrecht",
                checked: true,
            }
        ]
    },
} as ComponentMeta<typeof CheckboxListSelectAllMolecule>

export const CheckboxListMetSelectAll: ComponentStory<typeof CheckboxListSelectAllMolecule> = (args: any) => <CenteredLayout>
    <CheckboxListSelectAllMolecule {...args} />
</CenteredLayout>
