import { FormAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: FormAtom,
    args: { 
       children: "Leeg formulier"
    },
} as ComponentMeta<typeof FormAtom>

export const Formulier: ComponentStory<typeof FormAtom> = (args: any) => <CenteredLayout>
    <FormAtom {...args} />
</CenteredLayout>