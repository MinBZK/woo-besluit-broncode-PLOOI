import { CenteredLayout } from "../../ui/layouts";
import { SpinnerPopupMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

// export interface Args {
//     type: 'default' | 'primary'
// }

export default {
    title: "KOOP-React/Molecules",
    component: SpinnerPopupMolecule,
    args: {
        text: 'Verwijderen'
    }
} as ComponentMeta<typeof SpinnerPopupMolecule>

export const SpinnerPopup: ComponentStory<typeof SpinnerPopupMolecule> = (args: any) => <CenteredLayout>
    <SpinnerPopupMolecule {...args} />
</CenteredLayout>