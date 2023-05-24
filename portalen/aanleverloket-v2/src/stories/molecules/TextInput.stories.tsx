import { TextInputMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { ContainerAtom } from '../../ui/atoms';
import { RequiredValidator } from '../../validations/requiredValidator';

export default {
    title: 'KOOP-React/Molecules',
    component: TextInputMolecule,
    args: {
        id: 'input-id',
        label: 'Input label',
        onEnter: () => alert('Enter pressed'),
        placeholder: 'placeholder',
        validations: [new RequiredValidator()]
    },
} as ComponentMeta<typeof TextInputMolecule>

export const TextInput: ComponentStory<typeof TextInputMolecule> = (args: any) => <CenteredLayout>
    <ContainerAtom>
        <TextInputMolecule {...args} />
    </ContainerAtom>
</CenteredLayout>