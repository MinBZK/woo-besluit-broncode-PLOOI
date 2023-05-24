import { ToastAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: ToastAtom,
    args: { 
        type: 'info',
        children: 'Some Message'
    },
} as ComponentMeta<typeof ToastAtom>

export const Toast: ComponentStory<typeof ToastAtom> = (args: any) => <CenteredLayout>
    <ToastAtom {...args} />
</CenteredLayout>