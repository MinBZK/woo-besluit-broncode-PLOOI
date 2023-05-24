import { ChipAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: ChipAtom,
    args: { 
        text: "Chip"
    },
} as ComponentMeta<typeof ChipAtom>

export const Chip: ComponentStory<typeof ChipAtom> = (args: any) => <CenteredLayout>
    <ChipAtom {...args} />
</CenteredLayout>