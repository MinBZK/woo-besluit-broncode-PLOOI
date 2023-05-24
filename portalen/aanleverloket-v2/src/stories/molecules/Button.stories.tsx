import { CenteredLayout } from "../../ui/layouts";
import { ButtonMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

// export interface Args {
//     type: 'default' | 'primary'
// }

export default {
    title: "KOOP-React/Molecules",
    component: ButtonMolecule,
    args: {
        id: 'id',
        type: 'default',
        disabled: false,
        loading: false,
        text: 'Verwijderen'
    }
} as ComponentMeta<typeof ButtonMolecule>

export const StandardButton: ComponentStory<typeof ButtonMolecule> = (args: any) => <CenteredLayout>
    <ButtonMolecule {...args} />
</CenteredLayout>