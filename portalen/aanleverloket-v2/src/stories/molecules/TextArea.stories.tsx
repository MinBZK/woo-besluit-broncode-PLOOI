import { TextAreaMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { ContainerAtom } from '../../ui/atoms';

export default {
    title: 'KOOP-React/Molecules',
    component: TextAreaMolecule,
    args: { 
        id: 'area-id',
        label: 'Label',
        placeholder: 'Placeholder',
        required: true
    },
} as ComponentMeta<typeof TextAreaMolecule>

export const TextArea: ComponentStory<typeof TextAreaMolecule> = (args: any) => <CenteredLayout>
    <ContainerAtom>
    <TextAreaMolecule {...args} />
    </ContainerAtom>
</CenteredLayout>