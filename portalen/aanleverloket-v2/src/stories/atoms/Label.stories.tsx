import { LabelAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: LabelAtom,
    args: { 
        bold: false,
        for: "some-id",
        italic: false,
        underlined: false,
        type: 'form',
        children: "Label Atom"
    },
} as ComponentMeta<typeof LabelAtom>

export const Label: ComponentStory<typeof LabelAtom> = (args: any) => <CenteredLayout>
    <LabelAtom {...args} />
</CenteredLayout>