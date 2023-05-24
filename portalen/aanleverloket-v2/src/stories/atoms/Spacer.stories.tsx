import { SpacerAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: SpacerAtom,
    args: {
        space: 1
    },
} as ComponentMeta<typeof SpacerAtom>

export const Spacer: ComponentStory<typeof SpacerAtom> = (args: any) => <CenteredLayout>
    <div style={{ width: '100%', backgroundColor: '#eee', height: 50 }} />
    <SpacerAtom {...args} />
    <div style={{ width: '100%', backgroundColor: '#eee', height: 50 }} />
</CenteredLayout>