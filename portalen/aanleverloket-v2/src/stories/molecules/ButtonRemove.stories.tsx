import { ButtonRemoveMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Molecules',
    component: ButtonRemoveMolecule,
    args: { 
        onClick: () => alert('Click')
    },
} as ComponentMeta<typeof ButtonRemoveMolecule>

export const ButtonRemove: ComponentStory<typeof ButtonRemoveMolecule> = (args: any) => <CenteredLayout>
    <ButtonRemoveMolecule {...args} />
</CenteredLayout>