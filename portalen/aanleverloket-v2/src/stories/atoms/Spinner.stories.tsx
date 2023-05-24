import { SpinnerAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: SpinnerAtom,
    args: { 
        type: 'primary'
    },
} as ComponentMeta<typeof SpinnerAtom>

export const Spinner: ComponentStory<typeof SpinnerAtom> = (args: any) => <CenteredLayout>
    <SpinnerAtom {...args} />
</CenteredLayout>