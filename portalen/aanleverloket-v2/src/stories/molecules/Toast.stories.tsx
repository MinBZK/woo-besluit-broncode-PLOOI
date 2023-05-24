import { ToastMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: ToastMolecule,
    args: { 
        type: 'info',
        title: 'Bericht titel',
        message: 'Dit is een informatief testbericht'
    },
} as ComponentMeta<typeof ToastMolecule>

export const Toast: ComponentStory<typeof ToastMolecule> = (args: any) => <CenteredLayout>
    <ToastMolecule {...args} />
</CenteredLayout>