import { CenteredLayout } from "../../ui/layouts";
import { IconButtonMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

export interface Args {
    type: 'default' | 'primary',
    icon: string,
    rtl?: boolean
}

export default {
    title: "KOOP-React/Molecules",
    component: IconButtonMolecule,
    argTypes: {
        type: {
            options: ['default', 'primary'],
            control: { type: 'select' }
        },
        icon: {
            options: ['icon-cross-blue', 'icon-cross-white'],
            control: { type: 'select' }
        }
    },
    args: {
        id: "id",
        icon: 'icon-cross-blue',
        type: 'default',
        text: 'Verwijderen',
        rtl: false
    }
} as ComponentMeta<typeof IconButtonMolecule>

export const IconButton: ComponentStory<typeof IconButtonMolecule> = (args: any) => <CenteredLayout>
    <IconButtonMolecule {...args} />
</CenteredLayout>