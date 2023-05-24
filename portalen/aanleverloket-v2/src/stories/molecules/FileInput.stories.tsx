import { FileInputMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { MaxFileSizeValidator } from '../../validations/maxSizeValidator';

export default {
    title: 'KOOP-React/Molecules',
    component: FileInputMolecule,
    args: { 
        onSelectFile: (file) => alert('Geselecteerd: ' + file?.name),
        selectedFileName: "Bestand.pdf",
        validations: [new MaxFileSizeValidator(1000)]
    },
} as ComponentMeta<typeof FileInputMolecule>

export const BestandUploaden: ComponentStory<typeof FileInputMolecule> = (args: any) => <CenteredLayout>
    <FileInputMolecule {...args} />
    </CenteredLayout>