import { SmallAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: SmallAtom,
    args: { 
       text: "Small tekst"
    },
} as ComponentMeta<typeof SmallAtom>

export const Small: ComponentStory<typeof SmallAtom> = (args: any) => <CenteredLayout>
    <SmallAtom {...args} />
</CenteredLayout>