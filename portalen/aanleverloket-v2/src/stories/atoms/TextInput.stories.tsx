import { TextInputAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: TextInputAtom,
    args: {
        id: 'input-id',
        onEnter: () => alert('Enter ingedrukt'),
        placeholder: "Placeholder",
        type: 'text',
        error: false,
        disabled: false,
        
    },
} as ComponentMeta<typeof TextInputAtom>

export const TextInput: ComponentStory<typeof TextInputAtom> = (args: any) => <CenteredLayout>
    <TextInputAtom {...args} />
</CenteredLayout>