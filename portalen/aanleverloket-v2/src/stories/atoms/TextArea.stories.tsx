import { TextAreaAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: TextAreaAtom,
    args: {
        id: 'area-id',
        placeholder: 'Placeholder',
        onChange: (value) => console.log(value),        
        readonly: false,
        rows: 4
    },
} as ComponentMeta<typeof TextAreaAtom>

export const TextArea: ComponentStory<typeof TextAreaAtom> = (args: any) => <CenteredLayout>
    <TextAreaAtom {...args} />
</CenteredLayout>