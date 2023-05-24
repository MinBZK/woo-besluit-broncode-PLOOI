import { HeadingH1Atom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Headings',
    component: HeadingH1Atom,
    args: { 
        children: 'Heading H1'
    },
} as ComponentMeta<typeof HeadingH1Atom>

export const H1: ComponentStory<typeof HeadingH1Atom> = (args: any) => <CenteredLayout>
    <HeadingH1Atom {...args} />
</CenteredLayout>