import { CheckboxAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: CheckboxAtom,
    args: {
        checked: true,
        id: 'checkbox-id',
        disabled: false,
        label: "Test waarde"
        
    },
} as ComponentMeta<typeof CheckboxAtom>

export const Checkbox: ComponentStory<typeof CheckboxAtom> = (args: any) => <CenteredLayout>
    <CheckboxAtom {...args} />
</CenteredLayout>